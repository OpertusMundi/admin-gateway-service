package eu.opertusmundi.admin.web.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.rest.dto.CountResultDto;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import eu.opertusmundi.admin.web.model.mapper.IncidentRowMapper;
import eu.opertusmundi.admin.web.model.mapper.ProcessInstanceRowMapper;
import eu.opertusmundi.admin.web.model.workflow.EnumIncidentSortField;
import eu.opertusmundi.admin.web.model.workflow.EnumProcessInstanceSortField;
import eu.opertusmundi.admin.web.model.workflow.IncidentDto;
import eu.opertusmundi.admin.web.model.workflow.ProcessInstanceDto;
import eu.opertusmundi.common.feign.client.BpmServerFeignClient;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;

@Service
public class DefaultBpmEngineService implements BpmEngineService {

    @Autowired
    private ObjectProvider<BpmServerFeignClient> bpmClient;

    @Autowired
    @Qualifier("camundaDataSource")
    private DataSource camundaDataSource;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    private void init() {
        jdbcTemplate = new JdbcTemplate(camundaDataSource);
    }

    public PageResultDto<ProcessInstanceDto> getRunningProcessInstances(
        int page, int size, String businessKey, EnumProcessInstanceSortField orderBy, EnumSortingOrder order
    ) {
        final String countQuery =
            "select    count(*) " +
            "from      act_ru_execution ex " +
            "            inner join act_re_procdef def " +
            "              on ex.proc_def_id_ = def.id_ " +
            "            inner join act_re_deployment dep " +
            "              on def.deployment_id_ = dep.id_ " +
            "where     ex.id_ = ex.proc_inst_id_ ";

        final Long count = jdbcTemplate.queryForObject(countQuery, Long.class);

        String selectQuery =
            "select    def.id_                   as process_definition_id, " +
            "          def.name_                 as process_definition_name, " +
            "          def.key_                  as process_definition_key, " +
            "          def.version_              as process_definition_version, " +
            "          dep.deploy_time_          as process_definition_deployed_on, " +
            "          ex.id_                    as process_instance_id, " +
            "          ex.business_key_          as business_key, " +
            "          hist.start_time_          as started_on, " +
            "          count(i.id_)              as incident_counter " +
            "from      act_ru_execution ex " +
            "            inner join act_re_procdef def " +
            "              on ex.proc_def_id_ = def.id_ " +
            "            inner join act_re_deployment dep " +
            "              on def.deployment_id_ = dep.id_ " +
            "            inner join act_hi_procinst hist " +
            "              on ex.id_ = hist.proc_inst_id_ " +
            "            left outer join act_ru_incident i " +
            "              on i.proc_inst_id_ = ex.id_ and i.id_ = i.root_cause_incident_id_ " +
            "where     ex.id_ = ex.proc_inst_id_ ";

        // Add filtering
        if (!StringUtils.isBlank(businessKey)) {
            selectQuery += "and ex.business_key_ = ? ";
        }

        // Add grouping
        selectQuery +=
            "group by  def.id_,  " +
            "          def.name_,  " +
            "          def.key_,  " +
            "          def.version_,  " +
            "          dep.deploy_time_, " +
            "          ex.id_, " +
            "          ex.business_key_, " +
            "          hist.start_time_ ";

        // Add sorting
        selectQuery += String.format("order by %s  %s, ex.id_ ", orderBy.getField(), order.toString());
        // Add pagination
        selectQuery += "offset    ? limit ?";

        final List<Object> args = new ArrayList<>();

        if (!StringUtils.isBlank(businessKey)) {
            args.add(businessKey);
        }

        args.add(Integer.valueOf(page * size));
        args.add(Integer.valueOf(size));

        final List<ProcessInstanceDto> rows = jdbcTemplate.query(
            selectQuery,
            args.toArray(),
            new ProcessInstanceRowMapper()
        );

        return PageResultDto.of(page, size, rows, count);
    }

    public Long countIncidents() {
        final CountResultDto result = this.bpmClient.getObject().countIncidents();

        return result.getCount();
    }

    public PageResultDto<IncidentDto> getIncidents(
        int page, int size, String processInstanceId, EnumIncidentSortField orderBy, EnumSortingOrder order
    ) {
        final String countQuery =
            "select  count(*) " +
            "from    act_ru_execution ex " +
            "          inner join act_re_procdef def " +
            "            on ex.proc_def_id_ = def.id_ " +
            "          inner join act_re_deployment dep " +
            "            on def.deployment_id_ = dep.id_ " +
            "          inner join act_ru_incident i " +
            "            on i.proc_inst_id_ = ex.id_ " +
            "where    ex.id_ = ex.proc_inst_id_ ";

        final Long count = jdbcTemplate.queryForObject(countQuery, Long.class);

        String selectQuery =
            "select  def.id_                     as process_definition_id, " +
            "        def.name_                   as process_definition_name, " +
            "        def.key_                    as process_definition_key, " +
            "        def.version_                as process_definition_version, " +
            "        dep.deploy_time_            as process_definition_deployed_on, " +
            "        ex.id_                      as process_instance_id, " +
            "        ex.business_key_            as business_key, " +
            "        i.id_                       as incident_id, " +
            "        i.incident_msg_             as incident_message, " +
            "        i.incident_type_            as incident_type, " +
            "        i.incident_timestamp_       as incident_datetime, " +
            "        i.activity_id_              as activity_name, " +
            "        i.configuration_            as activity_id, " +
            "        et.worker_id_               as task_worker, " +
            "        et.topic_name_              as task_name, " +
            "        et.error_msg_               as task_error_message, " +
            "        encode(bd.bytes_, 'escape') as task_error_details " +
            "from    act_ru_execution ex " +
            "          inner join act_re_procdef def " +
            "            on ex.proc_def_id_ = def.id_ " +
            "          inner join act_re_deployment dep " +
            "            on def.deployment_id_ = dep.id_ " +
            "          inner join act_ru_incident i " +
            "            on i.proc_inst_id_ = ex.id_ " +
            "          left outer join act_ru_ext_task et " +
            "            on i.configuration_ = et.id_ " +
            "          left outer join act_ge_bytearray bd " +
            "            on et.error_details_id_ = bd.id_ " +
            "where    ex.id_ = ex.proc_inst_id_ ";


        // Add filtering
        if (!StringUtils.isBlank(processInstanceId)) {
            selectQuery += "and ex.id_ = ? ";
        }

        // Add sorting
        selectQuery += String.format("order by %s  %s, i.id_ ", orderBy.getField(), order.toString());
        // Add pagination
        selectQuery += "offset    ? limit ?";

        final List<Object> args = new ArrayList<>();

        if (!StringUtils.isBlank(processInstanceId)) {
            args.add(processInstanceId);
        }

        args.add(Integer.valueOf(page * size));
        args.add(Integer.valueOf(size));

        final List<IncidentDto> rows = jdbcTemplate.query(
            selectQuery,
            args.toArray(),
            new IncidentRowMapper()
        );

        return PageResultDto.of(page, size, rows, count);
    }

}
