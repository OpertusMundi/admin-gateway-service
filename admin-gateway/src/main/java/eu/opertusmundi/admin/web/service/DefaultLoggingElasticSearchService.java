package eu.opertusmundi.admin.web.service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.opertusmundi.admin.web.config.LoggingElasticConfiguration;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.catalogue.elastic.ElasticServiceException;
import eu.opertusmundi.common.model.logging.ElasticEventDto;
import eu.opertusmundi.common.model.logging.ElasticEventQueryDto;
import eu.opertusmundi.common.model.logging.EnumEventLevel;

@Service
public class DefaultLoggingElasticSearchService implements LoggingElasticSearchService {

    private static final Logger logger = LogManager.getLogger(DefaultLoggingElasticSearchService.class);

    private RestHighLevelClient client = null;

    @Autowired
    private LoggingElasticConfiguration configuration;

    @Autowired
    private ObjectMapper objectMapper;
    
    @PostConstruct
    private void init() {
        try {
            final HttpHost[] hosts = Arrays.asList(configuration.getHosts()).stream()
                .map(c -> new HttpHost(c.getHostName(), c.getPort(), c.getScheme()))
                .toArray(HttpHost[]::new);

            final RestClientBuilder builder = RestClient.builder(hosts);

            client = new RestHighLevelClient(builder);

            logger.debug("Elasticsearch client for log events has been initialized");
        } catch (final Exception ex) {
            logger.error("Failed to initialize client", ex);
        }
    }

    @PreDestroy
    private void shutdown() {
        this.close();
    }

    @Override
    public void close() {
        try {
            client.close();
            client = null;
        } catch (final IOException ex) {
            logger.error("Failed to close elastic search client", ex);
        }
    }

    @Override
    public PageResultDto<ElasticEventDto> search(ElasticEventQueryDto query) throws ElasticServiceException {
        try {
            final DateTimeFormatter       formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
            final int                     from      = query.getFrom();           
            
            // Restrict the request to the asset index
            final SearchRequest       searchRequest       = new SearchRequest(this.configuration.getRsyslogIndex().getName());
            final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            final BoolQueryBuilder    builder             = QueryBuilders.boolQuery();

            // Filter severity
            if (!CollectionUtils.isEmpty(query.getLevels())) {
                final BoolQueryBuilder   tempBool     = QueryBuilders.boolQuery();
                final List<QueryBuilder> levelQueries = new ArrayList<>();
                
                for (final EnumEventLevel l : query.getLevels()) {
                    l.getSeverity().forEach(s -> levelQueries.add(QueryBuilders.matchQuery("severity", s)));
                }
                for (final QueryBuilder currentQuery : levelQueries) {
                    tempBool.should(currentQuery);
                }
                
                builder.must(tempBool);
            }

            // Filter applications
            if (!CollectionUtils.isEmpty(query.getApplications())) {
                final BoolQueryBuilder   tempBool   = QueryBuilders.boolQuery();
                final List<QueryBuilder> appQueries = new ArrayList<>();

                for (final String app : query.getApplications()) {
                    appQueries.add(QueryBuilders.matchQuery("program-name", app));
                }
                for (final QueryBuilder currentQuery : appQueries) {
                    tempBool.should(currentQuery);
                }

                builder.must(tempBool);
            }
            
            // Filter users
            if (!CollectionUtils.isEmpty(query.getUserNames())) {
                final BoolQueryBuilder   tempBool   = QueryBuilders.boolQuery();
                final List<QueryBuilder> userQueries = new ArrayList<>();

                for (final String userName : query.getUserNames()) {
                    userQueries.add(QueryBuilders.matchQuery("client-username", userName));
                }
                for (final QueryBuilder currentQuery : userQueries) {
                    tempBool.should(currentQuery);
                }

                builder.must(tempBool);
            }
            
            // Filter IP Addresses
            if (!CollectionUtils.isEmpty(query.getClientAddresses())) {
                final BoolQueryBuilder   tempBool   = QueryBuilders.boolQuery();
                final List<QueryBuilder> ipQueries = new ArrayList<>();

                for (final String app : query.getClientAddresses()) {
                    ipQueries.add(QueryBuilders.matchQuery("client-address", app));
                }
                for (final QueryBuilder currentQuery : ipQueries) {
                    tempBool.should(currentQuery);
                }

                builder.must(tempBool);
            }
            
            // Filter interval
            if (query.getFromDate() != null && query.getToDate() != null) {
                builder.must(QueryBuilders.boolQuery()
                    .should(QueryBuilders.boolQuery()
                        .must(QueryBuilders.existsQuery("timestamp"))
                        .must(QueryBuilders.rangeQuery("timestamp").from(formatter.format(query.getFromDate())))
                        .must(QueryBuilders.rangeQuery("timestamp").to(formatter.format(query.getToDate())))
                    )
                );
            } else if (query.getFromDate() != null && query.getToDate() == null) {
                builder.must(QueryBuilders.boolQuery()
                    .should(QueryBuilders.boolQuery()
                        .must(QueryBuilders.existsQuery("timestamp"))
                        .must(QueryBuilders.rangeQuery("timestamp").from(formatter.format(query.getFromDate())))
                    )
                );
            } else if (query.getFromDate() == null && query.getToDate() != null) {
                builder.must(QueryBuilders.boolQuery()
                    .should(QueryBuilders.boolQuery()
                        .must(QueryBuilders.existsQuery("timestamp"))
                        .must(QueryBuilders.rangeQuery("timestamp").to(query.getToDate()))
                    )
                );
            }
            
            // Set default builder
            if (builder.must().isEmpty()) {
                searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            } else {
                searchSourceBuilder.query(builder);
            }

            // Sort results
            if (query.getOrderBy() != null) {
                if (query.getOrder().orElse(EnumSortingOrder.ASC) == EnumSortingOrder.ASC) {
                    searchSourceBuilder
                        .sort(new FieldSortBuilder(query.getOrderBy().getValue()).order(SortOrder.ASC))
                        .sort(new ScoreSortBuilder().order(SortOrder.DESC));
                } else {
                    searchSourceBuilder
                        .sort(new FieldSortBuilder(query.getOrderBy().getValue()).order(SortOrder.DESC))
                        .sort(new ScoreSortBuilder().order(SortOrder.DESC));
                }
            }
            
            // If page and size are specified
            searchSourceBuilder.from(from).size(query.getSize() == null ? 10 : query.getSize().orElse(10));

            searchRequest.source(searchSourceBuilder);

            final SearchResponse            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            final SearchHits                hits           = searchResponse.getHits();
            final List<Map<String, Object>> objects        = new ArrayList<>();

            for (final SearchHit hit : hits) {
                final Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                objects.add(sourceAsMap);
            }

            // Convert list of objects to list of features
            final List<ElasticEventDto> events = objectMapper.convertValue(objects, new TypeReference<List<ElasticEventDto>>() { });

            return PageResultDto.of(
                query.getPage() == null ? 0 : query.getPage().orElse(0), 
                query.getSize() == null ? 0 : query.getSize().orElse(10), 
                events, 
                hits.getTotalHits().value
            );
        } catch(final Exception ex) {
            throw new ElasticServiceException("Search operation has failed", ex);
        }
    }

}