package eu.opertusmundi.admin.web.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.rest.dto.ModificationDto;
import org.camunda.bpm.engine.rest.dto.VariableValueDto;
import org.camunda.bpm.engine.rest.dto.history.HistoricActivityInstanceDto;
import org.camunda.bpm.engine.rest.dto.history.HistoricIncidentDto;
import org.camunda.bpm.engine.rest.dto.history.HistoricProcessInstanceDto;
import org.camunda.bpm.engine.rest.dto.history.HistoricVariableInstanceDto;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionQueryDto;
import org.camunda.bpm.engine.rest.dto.runtime.modification.CancellationInstructionDto;
import org.camunda.bpm.engine.rest.dto.runtime.modification.ProcessInstanceModificationInstructionDto;
import org.camunda.bpm.engine.rest.dto.runtime.modification.StartBeforeInstructionDto;
import org.camunda.bpm.engine.rest.dto.task.TaskDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import eu.opertusmundi.admin.web.model.mapper.IncidentRowMapper;
import eu.opertusmundi.admin.web.model.mapper.ProcessInstanceHistoryRowMapper;
import eu.opertusmundi.admin.web.model.mapper.ProcessInstanceRowMapper;
import eu.opertusmundi.admin.web.model.mapper.ProcessInstanceTaskRowMapper;
import eu.opertusmundi.admin.web.model.workflow.BpmnMessageCode;
import eu.opertusmundi.admin.web.model.workflow.DeploymentDto;
import eu.opertusmundi.admin.web.model.workflow.EnumIncidentSortField;
import eu.opertusmundi.admin.web.model.workflow.EnumProcessInstanceHistorySortField;
import eu.opertusmundi.admin.web.model.workflow.EnumProcessInstanceSortField;
import eu.opertusmundi.admin.web.model.workflow.EnumProcessInstanceTaskSortField;
import eu.opertusmundi.admin.web.model.workflow.HistoryProcessInstanceDetailsDto;
import eu.opertusmundi.admin.web.model.workflow.IncidentDto;
import eu.opertusmundi.admin.web.model.workflow.ProcessDefinitionHeaderDto;
import eu.opertusmundi.admin.web.model.workflow.ProcessInstanceDetailsDto;
import eu.opertusmundi.admin.web.model.workflow.ProcessInstanceDto;
import eu.opertusmundi.admin.web.model.workflow.ProcessInstanceResource;
import eu.opertusmundi.admin.web.model.workflow.ProcessInstanceTaskDto;
import eu.opertusmundi.admin.web.model.workflow.VariableDto;
import eu.opertusmundi.common.model.BasicMessageCode;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.ServiceException;
import eu.opertusmundi.common.model.account.AccountDto;
import eu.opertusmundi.common.model.workflow.EnumProcessInstanceVariable;
import eu.opertusmundi.common.repository.AccountRepository;
import eu.opertusmundi.common.util.BpmEngineUtils;
import feign.FeignException;

@Service
public class DefaultBpmEngineService implements BpmEngineService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultBpmEngineService.class);

    private static final String START_EVENT = "startEvent";

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProcessInstanceResourceResolver resourceResolver;

    @Autowired
    private BpmEngineUtils bpmEngineUtils;

    @Autowired
    @Qualifier("camundaDataSource")
    private DataSource camundaDataSource;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    private void init() {
        jdbcTemplate = new JdbcTemplate(camundaDataSource);
    }

    @Override
    public List<DeploymentDto> getDeployments(String sortOrder, String sortBy) {
        var result = this.bpmEngineUtils.getDeployments(sortOrder, sortBy).stream()
            .map(DeploymentDto::from)
            .toList();

        return result;
    }

    @Override
    public void deleteDeployment(String id, boolean cascade) {
        this.bpmEngineUtils.deleteDeployment(id, cascade);
    }

    @Override
    public List<ProcessDefinitionHeaderDto> getProcessDefinitions() {
        try {
            final ProcessDefinitionQueryDto query = new ProcessDefinitionQueryDto();
            query.setLatestVersion(true);
            query.setSortBy("name");
            query.setSortOrder("asc");

            final List<ProcessDefinitionHeaderDto> result = this.bpmEngineUtils
                .getProcessDefinitions(query, 0, 1000)
                .stream()
                .map(ProcessDefinitionHeaderDto::from)
                .collect(Collectors.toList());

            return result;
        } catch (final Exception ex) {
            logger.error("Failed to load process definitions", ex);
        }

        return Collections.emptyList();
    }

    @Override
    public String getBpmnXml(String processDefinitionId) {
        try {
            final String result = this.bpmEngineUtils.getBpmnXml(processDefinitionId);

            return result;
        } catch (final Exception ex) {
            logger.error(String.format("Failed to load process BPMN 2.0 XML [processDefinitionId=%s]", processDefinitionId), ex);
        }

        return null;
    }

    @Override
    public Long countProcessInstances(String deploymentId) {
        final long result = this.bpmEngineUtils.countProcessInstances(deploymentId);

        return result;
    }

    @Override
    public PageResultDto<ProcessInstanceDto> getProcessInstances(
        int page, int size,
        String processDefinitionKey, String businessKey, String task,
        EnumProcessInstanceSortField orderBy, EnumSortingOrder order
    ) {
        String countQuery =
            "select    count(*) as counter " +
            "from      act_ru_execution ex " +
            "            inner join act_re_procdef def " +
            "              on ex.proc_def_id_ = def.id_ " +
            "            inner join act_re_deployment dep " +
            "              on def.deployment_id_ = dep.id_ " +
            "            left outer join act_ru_incident i " +
            "              on i.proc_inst_id_ = ex.id_ and i.id_ = i.root_cause_incident_id_ ";

        if (!StringUtils.isBlank(task)) {
            countQuery +=
                "            left outer join act_ru_task tk " +
                "              on ex.id_ = tk.proc_inst_id_ ";
        }

        countQuery += "where     ex.id_ = ex.proc_inst_id_ ";

        // Add filtering
        if (!StringUtils.isBlank(processDefinitionKey)) {
            countQuery += "and def.key_ = ? ";
        }
        if (!StringUtils.isBlank(businessKey)) {
            countQuery += "and ex.business_key_ = ? ";
        }
        if (!StringUtils.isBlank(task)) {
            countQuery += "and tk.task_def_key_ = ? ";
        }

        final List<Object> args = new ArrayList<>();

        if (!StringUtils.isBlank(processDefinitionKey)) {
            args.add(processDefinitionKey);
        }
        if (!StringUtils.isBlank(businessKey)) {
            args.add(businessKey);
        }
        if (!StringUtils.isBlank(task)) {
            args.add(task);
        }

        final Long count = jdbcTemplate.queryForObject(countQuery, Long.class, args.toArray());

        String selectQuery =
            "select    def.id_                   as process_definition_id, " +
            "          def.name_                 as process_definition_name, " +
            "          def.key_                  as process_definition_key, " +
            "          def.version_              as process_definition_version, " +
            "          def.version_tag_          as process_definition_version_tag, " +
            "          dep.deploy_time_          as process_definition_deployed_on, " +
            "          ex.id_                    as process_instance_id, " +
            "          ex.business_key_          as business_key, " +
            "          hist.start_time_          as started_on, " +
            "          count(i.id_)              as incident_counter, " +
            "          count(tk)                 as task_counter, " +
            "          count(tk) filter (where tk.task_def_key_ = 'task-review')    as task_review_counter, " +
            "          count(tk) filter (where tk.task_def_key_ like '%-set-error') as task_error_counter, " +
            "          array_agg(DISTINCT tk.task_def_key_)                         as task_names, " +
            "          array_agg(v.name_)                                           as variable_names, " +
            "          array_agg(v.text_)                                           as variable_values " +
            "from      act_ru_execution ex " +
            "            inner join act_re_procdef def " +
            "              on ex.proc_def_id_ = def.id_ " +
            "            inner join act_re_deployment dep " +
            "              on def.deployment_id_ = dep.id_ " +
            "            inner join act_hi_procinst hist " +
            "              on ex.id_ = hist.proc_inst_id_ " +
            "            left outer join act_ru_incident i " +
            "              on i.proc_inst_id_ = ex.id_ and i.id_ = i.root_cause_incident_id_ " +
            "            left outer join act_ru_task tk " +
            "              on ex.id_ = tk.proc_inst_id_ " +
            "            left outer join act_ru_variable v " +
            "              on ex.proc_inst_id_ = v.proc_inst_id_ " +
            "where     ex.id_ = ex.proc_inst_id_ ";

        // Add filtering
        if (!StringUtils.isBlank(processDefinitionKey)) {
            selectQuery += "and def.key_ = ? ";
        }
        if (!StringUtils.isBlank(businessKey)) {
            selectQuery += "and ex.business_key_ = ? ";
        }
        if (!StringUtils.isBlank(task)) {
            selectQuery += "and tk.task_def_key_ = ? ";
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
        selectQuery += "offset ? limit ?";

        args.clear();

        if (!StringUtils.isBlank(processDefinitionKey)) {
            args.add(processDefinitionKey);
        }
        if (!StringUtils.isBlank(businessKey)) {
            args.add(businessKey);
        }
        if (!StringUtils.isBlank(task)) {
            args.add(task);
        }

        args.add(Integer.valueOf(page * size));
        args.add(Integer.valueOf(size));

        final List<ProcessInstanceDto> rows = jdbcTemplate.query(
            selectQuery,
            new ProcessInstanceRowMapper(),
            args.toArray()
        );

        return PageResultDto.of(page, size, rows, count);
    }

    @Override
    public Long countProcessInstanceTasks() {
        final long result = this.bpmEngineUtils.countProcessInstanceTasks();

        return result;
    }

    @Override
    public PageResultDto<ProcessInstanceTaskDto> getProcessInstanceTasks(
        int page, int size,
        String processDefinitionKey, String businessKey, String task,
        EnumProcessInstanceTaskSortField orderBy, EnumSortingOrder order
    ) {
        String countQuery =
            "select    count(*) as counter " +
            "from      act_ru_execution ex " +
            "            inner join act_re_procdef def " +
            "              on ex.proc_def_id_ = def.id_ " +
            "            inner join act_re_deployment dep " +
            "              on def.deployment_id_ = dep.id_ " +
            "            inner join act_ru_task tk " +
            "              on ex.id_ = tk.proc_inst_id_ " +
            "            left outer join act_ru_incident i " +
            "              on i.proc_inst_id_ = ex.id_ and i.id_ = i.root_cause_incident_id_ " +
            "where     ex.id_ = ex.proc_inst_id_ ";

        // Add filtering
        if (!StringUtils.isBlank(processDefinitionKey)) {
            countQuery += "and def.key_ = ? ";
        }
        if (!StringUtils.isBlank(businessKey)) {
            countQuery += "and ex.business_key_ = ? ";
        }
        if (!StringUtils.isBlank(task)) {
            countQuery += "and tk.task_def_key_ = ? ";
        }

        final List<Object> args = new ArrayList<>();

        if (!StringUtils.isBlank(processDefinitionKey)) {
            args.add(processDefinitionKey);
        }
        if (!StringUtils.isBlank(businessKey)) {
            args.add(businessKey);
        }
        if (!StringUtils.isBlank(task)) {
            args.add(task);
        }

        final Long count = jdbcTemplate.queryForObject(countQuery, Long.class, args.toArray());

        String selectQuery =
            "select    def.id_                   as process_definition_id, " +
            "          def.name_                 as process_definition_name, " +
            "          def.key_                  as process_definition_key, " +
            "          def.version_              as process_definition_version, " +
            "          def.version_tag_          as process_definition_version_tag, " +
            "          dep.deploy_time_          as process_definition_deployed_on, " +
            "          ex.id_                    as process_instance_id, " +
            "          ex.business_key_          as business_key, " +
            "          hist.start_time_          as started_on, " +
            "          tk.id_                    as task_id, " +
            "          tk.task_def_key_          as task_name, " +
            "          count(i.id_)              as incident_counter " +
            "from      act_ru_execution ex " +
            "            inner join act_re_procdef def " +
            "              on ex.proc_def_id_ = def.id_ " +
            "            inner join act_re_deployment dep " +
            "              on def.deployment_id_ = dep.id_ " +
            "            inner join act_hi_procinst hist " +
            "              on ex.id_ = hist.proc_inst_id_ " +
            "            inner join act_ru_task tk " +
            "              on ex.id_ = tk.proc_inst_id_ " +
            "            left outer join act_ru_incident i " +
            "              on i.proc_inst_id_ = ex.id_ and i.id_ = i.root_cause_incident_id_ " +
            "where     ex.id_ = ex.proc_inst_id_ ";

        // Add filtering
        if (!StringUtils.isBlank(processDefinitionKey)) {
            selectQuery += "and def.key_ = ? ";
        }
        if (!StringUtils.isBlank(businessKey)) {
            selectQuery += "and ex.business_key_ = ? ";
        }
        if (!StringUtils.isBlank(task)) {
            selectQuery += "and tk.task_def_key_ = ? ";
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
            "          hist.start_time_, " +
            "          tk.id_, " +
            "          tk.task_def_key_ ";

        // Add sorting
        selectQuery += String.format("order by %s  %s, ex.id_ ", orderBy.getField(), order.toString());
        // Add pagination
        selectQuery += "offset ? limit ?";

        args.clear();

        if (!StringUtils.isBlank(processDefinitionKey)) {
            args.add(processDefinitionKey);
        }
        if (!StringUtils.isBlank(businessKey)) {
            args.add(businessKey);
        }
        if (!StringUtils.isBlank(task)) {
            args.add(task);
        }

        args.add(Integer.valueOf(page * size));
        args.add(Integer.valueOf(size));

        final List<ProcessInstanceTaskDto> rows = jdbcTemplate.query(
            selectQuery,
            new ProcessInstanceTaskRowMapper(),
            args.toArray()
        );

        return PageResultDto.of(page, size, rows, count);
    }

    @Override
    public Optional<ProcessInstanceDetailsDto> getProcessInstance(String businessKey, String processInstanceId) {
        final ProcessInstanceDetailsDto result = new ProcessInstanceDetailsDto();

        final List<HistoricProcessInstanceDto> processInstances = this.bpmEngineUtils.findHistoryInstances(
            null,
            businessKey,
            processInstanceId
        );
        if (processInstances.size() != 1) {
            return Optional.empty();
        }
        result.setInstance(processInstances.get(0));

        if (result.getInstance().getEndTime() != null) {
            // Instance is already completed, we should search the history
            // endpoint
            return Optional.empty();
        }

        // Set process instance id in case we have found a record by business
        // key
        processInstanceId = result.getInstance().getId();

        final Map<String, VariableValueDto> variables = this.bpmEngineUtils.getProcessInstanceVariables(processInstanceId);
        variables.keySet().stream().map(k -> VariableDto.from(k, variables.get(k))).forEach(result.getVariables()::add);

        final List<HistoricActivityInstanceDto> activities = this.bpmEngineUtils
            .getHistoryProcessInstanceActivityInstances(processInstanceId);
        activities.sort(this::compareActivities);
        result.setActivities(activities);

        final List<org.camunda.bpm.engine.rest.dto.runtime.IncidentDto> incidents = this.bpmEngineUtils
            .getIncidents(null, null, processInstanceId, null, null, null);
        result.setIncidents(incidents);

        result.getIncidents().forEach(i -> {
            final String details = this.bpmEngineUtils.getExternalTaskErrorDetails(i.getConfiguration());
            result.getErrorDetails().put(i.getActivityId(), details);
        });

        final VariableValueDto startUserVariable = variables.get(EnumProcessInstanceVariable.START_USER_KEY.getValue());
        if (startUserVariable != null && !StringUtils.isBlank((String) startUserVariable.getValue())) {
            final Optional<AccountDto> startUser = accountRepository.findOneByKeyObject(
                UUID.fromString((String) startUserVariable.getValue())
            );
            result.setOwner(startUser.orElse(null));
        }

        final String processDefinitionId  = result.getInstance().getProcessDefinitionId();
        final String processDefinitionKey = result.getInstance().getProcessDefinitionKey();

        final String bpmn2Xml = this.getBpmnXml(processDefinitionId);
        result.setBpmn2Xml(bpmn2Xml);

        // Get business key from result in case the method argument is null
        final ProcessInstanceResource resource = this.resourceResolver.resolve(
            processDefinitionKey, result.getInstance().getBusinessKey(), result.getVariables()
        );
        result.setResource(resource);

        return Optional.of(result);
    }

    @Override
    public PageResultDto<ProcessInstanceDto> getHistoryProcessInstances(
        int page, int size,
        String processDefinitionKey, String businessKey,
        EnumProcessInstanceHistorySortField orderBy, EnumSortingOrder order
    ) {
        String countQuery =
            "select    count(*) " +
            "from      act_hi_procinst hist " +
            "            inner join act_re_procdef def " +
            "              on hist.proc_def_id_ = def.id_ " +
            "            inner join act_re_deployment dep " +
            "              on def.deployment_id_ = dep.id_ " +
            "where     hist.id_ = hist.proc_inst_id_ ";

        // Add filtering
        if (!StringUtils.isBlank(processDefinitionKey)) {
            countQuery += "and hist.proc_def_key_ = ? ";
        }
        if (!StringUtils.isBlank(businessKey)) {
            countQuery += "and hist.business_key_ = ? ";
        }

        final List<Object> args = new ArrayList<>();

        if (!StringUtils.isBlank(processDefinitionKey)) {
            args.add(processDefinitionKey);
        }
        if (!StringUtils.isBlank(businessKey)) {
            args.add(businessKey);
        }

        final Long count = jdbcTemplate.queryForObject(countQuery, Long.class, args.toArray());

        String selectQuery =
            "select    def.id_                   as process_definition_id, " +
            "          def.name_                 as process_definition_name, " +
            "          def.key_                  as process_definition_key, " +
            "          def.version_              as process_definition_version, " +
            "          def.version_tag_          as process_definition_version_tag, " +
            "          dep.deploy_time_          as process_definition_deployed_on, " +
            "          hist.id_                  as process_instance_id, " +
            "          hist.business_key_        as business_key, " +
            "          hist.start_time_          as started_on, " +
            "          hist.end_time_            as completed_on " +
            "from      act_hi_procinst hist " +
            "            inner join act_re_procdef def " +
            "              on hist.proc_def_id_ = def.id_ " +
            "            inner join act_re_deployment dep " +
            "              on def.deployment_id_ = dep.id_ " +
            "where     hist.id_ = hist.proc_inst_id_ ";

        // Add filtering
        if (!StringUtils.isBlank(processDefinitionKey)) {
            selectQuery += "and hist.proc_def_key_ = ? ";
        }
        if (!StringUtils.isBlank(businessKey)) {
            selectQuery += "and hist.business_key_ = ? ";
        }

        // Add sorting
        selectQuery += String.format("order by %s  %s, hist.id_ ", orderBy.getField(), order.toString());
        // Add pagination
        selectQuery += "offset    ? limit ?";

        args.clear();

        if (!StringUtils.isBlank(processDefinitionKey)) {
            args.add(processDefinitionKey);
        }
        if (!StringUtils.isBlank(businessKey)) {
            args.add(businessKey);
        }

        args.add(Integer.valueOf(page * size));
        args.add(Integer.valueOf(size));

        final List<ProcessInstanceDto> rows = jdbcTemplate.query(
            selectQuery,
            new ProcessInstanceHistoryRowMapper(),
            args.toArray()
        );

        return PageResultDto.of(page, size, rows, count);
    }

    @Override
    public Optional<HistoryProcessInstanceDetailsDto> getHistoryProcessInstance(
        String businessKey, String processInstanceId
    ) {
        final HistoryProcessInstanceDetailsDto result = new HistoryProcessInstanceDetailsDto();

        final List<HistoricProcessInstanceDto> processInstances = this.bpmEngineUtils.findHistoryInstances(
            null,
            businessKey,
            processInstanceId
        );
        if (processInstances.size() != 1) {
            return Optional.empty();
        }
        result.setInstance(processInstances.get(0));

        // Set process instance id in case we have found a record by business
        // key
        processInstanceId = result.getInstance().getId();

        final List<HistoricVariableInstanceDto> variables = this.bpmEngineUtils
            .getHistoryProcessInstanceVariables(processInstanceId);
        variables.stream().map(VariableDto::from).forEach(result.getVariables()::add);

        final List<HistoricActivityInstanceDto> activities = this.bpmEngineUtils
            .getHistoryProcessInstanceActivityInstances(processInstanceId);
        activities.sort(this::compareActivities);
        result.setActivities(activities);

        final List<HistoricIncidentDto> incidents = this.bpmEngineUtils
            .getHistoryIncidents(processInstanceId);
        result.setIncidents(incidents);

        result.getIncidents().forEach(i -> {
            final String details = this.bpmEngineUtils.getHistoryExternalTaskLogErrorDetails(i.getHistoryConfiguration());
            result.getErrorDetails().put(i.getActivityId(), details);
        });

        final VariableValueDto startUserVariable = variables.stream()
            .filter(v -> v.getName().equals(EnumProcessInstanceVariable.START_USER_KEY.getValue()))
            .findFirst()
            .orElse(null);

        if (startUserVariable != null && !StringUtils.isBlank((String) startUserVariable.getValue())) {
            final Optional<AccountDto> startUser = accountRepository.findOneByKeyObject(
                UUID.fromString((String) startUserVariable.getValue())
            );
            result.setOwner(startUser.orElse(null));
        }

        final String processDefinitionId  = result.getInstance().getProcessDefinitionId();
        final String processDefinitionKey = result.getInstance().getProcessDefinitionKey();

        final String bpmn2Xml = this.getBpmnXml(processDefinitionId);

        // Get business key from result in case the method argument is null
        final ProcessInstanceResource resource = this.resourceResolver.resolve(
            processDefinitionKey, result.getInstance().getBusinessKey(), result.getVariables()
        );
        result.setResource(resource);

        result.setBpmn2Xml(bpmn2Xml);

        return Optional.of(result);
    }

    @Override
    public void retryExternalTask(String processInstanceId, String externalTaskId) {
        try {
            this.bpmEngineUtils.retryExternalTask(processInstanceId, externalTaskId, 1);
        } catch (final FeignException ex) {
            logger.error(String.format(
                "Failed to retry external task [processInstance=%s, externalTask=%s",
                processInstanceId, externalTaskId
            ), ex);

            throw ex;
        }
    }

    @Override
    public void completeTask(String businessKey, String taskDefinitionKey, Map<String, VariableValueDto> variables) {
        Assert.hasText(businessKey, "Expected a non-empty process instance business key");
        Assert.hasText(taskDefinitionKey, "Expected a non-empty task definition key");
        Assert.notNull(variables, "Expected a non-null collection of variables");

        try {
            // Find workflow instance
            final TaskDto task = this.bpmEngineUtils.findTask(businessKey, taskDefinitionKey).orElse(null);

            if (task == null) {
                throw new ServiceException(BasicMessageCode.BpmServiceError, "Task was not found");
            }

            // Complete task
            this.bpmEngineUtils.completeTask(task.getId(), variables);
        }
        catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(BasicMessageCode.BpmServiceError, "Failed to complete task");
        }
    }

    @Override
    public void deleteProcessInstance(String processInstanceId) {
        this.bpmEngineUtils.deleteProcessInstance(processInstanceId);
    }

    @Override
    public Long countIncidents() {
        return this.bpmEngineUtils.countIncidents();
    }

    @Override
    public PageResultDto<IncidentDto> getIncidents(
        int page, int size, String businessKey, EnumIncidentSortField orderBy, EnumSortingOrder order
    ) {
        String countQuery =
            "select  count(*) " +
            "from    act_ru_execution ex " +
            "          inner join act_re_procdef def " +
            "            on ex.proc_def_id_ = def.id_ " +
            "          inner join act_re_deployment dep " +
            "            on def.deployment_id_ = dep.id_ " +
            "          inner join act_ru_incident i " +
            "            on i.proc_inst_id_ = ex.id_ " +
            "where    ex.id_ = ex.proc_inst_id_ ";


        // Add filtering
        if (!StringUtils.isBlank(businessKey)) {
            countQuery += "and ex.business_key_ = ? ";
        }

        final List<Object> args = new ArrayList<>();

        if (!StringUtils.isBlank(businessKey)) {
            args.add(businessKey);
        }

        final Long count = jdbcTemplate.queryForObject(countQuery, Long.class, args.toArray());

        String selectQuery =
            "select  def.id_                     as process_definition_id, " +
            "        def.name_                   as process_definition_name, " +
            "        def.key_                    as process_definition_key, " +
            "        def.version_                as process_definition_version, " +
            "        def.version_tag_            as process_definition_version_tag, " +
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
        if (!StringUtils.isBlank(businessKey)) {
            selectQuery += "and ex.business_key_ = ? ";
        }

        // Add sorting
        selectQuery += String.format("order by %s  %s, i.id_ ", orderBy.getField(), order.toString());
        // Add pagination
        selectQuery += "offset    ? limit ?";

        args.clear();

        if (!StringUtils.isBlank(businessKey)) {
            args.add(businessKey);
        }

        args.add(Integer.valueOf(page * size));
        args.add(Integer.valueOf(size));

        final List<IncidentDto> rows = jdbcTemplate.query(
            selectQuery,
            new IncidentRowMapper(),
            args.toArray()
        );

        return PageResultDto.of(page, size, rows, count);
    }

    @Override
    public void modify(String processInstanceId, List<String> cancelActivities, List<String> startActivities) {
        Assert.notNull(startActivities, "Expected at least one start task instruction");
        Assert.isTrue(!startActivities.isEmpty(), "Expected at least one start task instruction");

        try {
            final ProcessInstanceDetailsDto instance = this.getProcessInstance(null, processInstanceId).orElse(null);
            if (instance == null) {
                throw new ServiceException(BpmnMessageCode.ProcessInstanceNotFound, "Process instance was not found");
            }

            final ModificationDto                                 modification = new ModificationDto();
            final List<ProcessInstanceModificationInstructionDto> instructions = new ArrayList<>();

            startActivities.forEach(a -> {
                final StartBeforeInstructionDto i = new StartBeforeInstructionDto();
                i.setActivityId(a);
                instructions.add(i);
            });

            cancelActivities.forEach(id -> {
                final List<HistoricActivityInstanceDto> activityInstances = instance.getActivities().stream()
                    .filter(a -> a.getActivityId().equals(id) && a.getEndTime() == null)
                    .collect(Collectors.toList());
                if (activityInstances.isEmpty()) {
                    throw new ServiceException(BpmnMessageCode.ActivityInstanceNotFound, "Activity instance was not found");
                }
                if (activityInstances.size() > 1) {
                    throw new ServiceException(BpmnMessageCode.ActivityInstanceNotActive, "Only one active activity instance is supported");
                }

                final CancellationInstructionDto i = new CancellationInstructionDto();
                i.setActivityId(id);
                instructions.add(i);
            });

            modification.setInstructions(instructions);

            this.bpmEngineUtils.modifyProcessInstance(processInstanceId, modification);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error(String.format("Modification instructions execution has failed [processInstanceId=%s]",processInstanceId),ex);
            throw new ServiceException(BasicMessageCode.BpmServiceError, "Failed to modify process instance");
        }
    }

    private int compareActivities(HistoricActivityInstanceDto i1, HistoricActivityInstanceDto i2) {
        // Compare start dates
        int c = i1.getStartTime().compareTo(i2.getStartTime());
        // Compare end dates
        if (c == 0 && i1.getEndTime() != null && i2.getEndTime() != null) {
            c = i1.getEndTime().compareTo(i2.getEndTime());
        } else if (c == 0 && i1.getEndTime() != null && i2.getEndTime() == null) {
            c = -1;
        } else if (c == 0 && i1.getEndTime() == null && i2.getEndTime() != null) {
            c = 1;
        }
        // Compare event types
        if (c == 0) {
            if (i1.getActivityType().equals(START_EVENT)) {
                return -1;
            }
            if (i2.getActivityType().equals(START_EVENT)) {
                return 1;
            }
        }
        return c;
    }

}
