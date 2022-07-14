package eu.opertusmundi.admin.web.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.RowMapper;

import eu.opertusmundi.admin.web.model.workflow.ProcessInstanceDto;

public class ProcessInstanceRowMapper implements RowMapper<ProcessInstanceDto> {

    @Override
    public ProcessInstanceDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        final ProcessInstanceDto result = new ProcessInstanceDto();

        result.setBusinessKey(rs.getString("business_key"));
        result.setIncidentCount(rs.getLong("incident_counter"));
        result.setProcessDefinitionId(rs.getString("process_definition_id"));
        result.setProcessDefinitionKey(rs.getString("process_definition_key"));
        result.setProcessDefinitionName(rs.getString("process_definition_name"));
        result.setProcessDefinitionVersion(rs.getInt("process_definition_version"));
        result.setProcessDefinitionVersionTag(rs.getString("process_definition_version_tag"));
        result.setProcessInstanceId(rs.getString("process_instance_id"));
        result.setTaskCount(rs.getLong("task_counter"));
        result.setTaskReviewCount(rs.getLong("task_review_counter"));
        result.setTaskErrorCount(rs.getLong("task_error_counter"));

        final String[]     taskNames    = (String[]) rs.getArray("task_names").getArray();
        final List<String> nonNullNames = Arrays.asList(taskNames).stream().filter(n -> n != null).collect(Collectors.toList());
        result.setTaskNames(nonNullNames);

        final String[] variableNames = (String[]) rs.getArray("variable_names").getArray();
        result.setVariableNames(variableNames);

        final String[] variableValues = (String[]) rs.getArray("variable_values").getArray();
        result.setVariableValues(variableValues);

        final Timestamp     deployedOnTimestamp = rs.getTimestamp("process_definition_deployed_on");
        final ZonedDateTime deployedOn          = deployedOnTimestamp.toInstant().atZone(ZoneId.of("UTC"));
        result.setProcessDefinitionDeployedOn(deployedOn);

        final Timestamp     startedOnTimestamp = rs.getTimestamp("started_on");
        final ZonedDateTime startedOn          = startedOnTimestamp.toInstant().atZone(ZoneId.of("UTC"));
        result.setStartedOn(startedOn);

        return result;
    }

}
