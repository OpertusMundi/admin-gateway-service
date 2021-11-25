package eu.opertusmundi.admin.web.service;

import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.catalogue.elastic.ElasticServiceException;
import eu.opertusmundi.common.model.logging.ElasticEventDto;
import eu.opertusmundi.common.model.logging.ElasticEVentQueryDto;

public interface LoggingElasticSearchService {

    /**
     * Closes elastic search client
     */
    void close();

    /**
     * Search log events index
     * 
     * @param query
     * @return
     * @throws ElasticServiceException
     */
    PageResultDto<ElasticEventDto> search(ElasticEVentQueryDto query) throws ElasticServiceException;

}
