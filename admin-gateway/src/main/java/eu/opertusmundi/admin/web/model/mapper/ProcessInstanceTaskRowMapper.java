package eu.opertusmundi.admin.web.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.jdbc.core.RowMapper;

import eu.opertusmundi.admin.web.model.workflow.ProcessInstanceTaskDto;

public class ProcessInstanceTaskRowMapper implements RowMapper<ProcessInstanceTaskDto> {

    @Override
    public ProcessInstanceTaskDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        final ProcessInstanceTaskDto result = new ProcessInstanceTaskDto();

        result.setBusinessKey(rs.getString("business_key"));
        result.setIncidentCount(rs.getLong("incident_counter"));
        result.setProcessDefinitionId(rs.getString("process_definition_id"));
        result.setProcessDefinitionKey(rs.getString("process_definition_key"));
        result.setProcessDefinitionName(rs.getString("process_definition_name"));
        result.setProcessDefinitionVersion(rs.getInt("process_definition_version"));
        result.setProcessDefinitionVersionTag(rs.getString("process_definition_version_tag"));
        result.setProcessInstanceId(rs.getString("process_instance_id"));
        result.setTaskId(rs.getString("task_id"));
        result.setTaskName(rs.getString("task_name"));

        final Timestamp     deployedOnTimestamp = rs.getTimestamp("process_definition_deployed_on");
        final ZonedDateTime deployedOn          = deployedOnTimestamp.toInstant().atZone(ZoneId.of("UTC"));
        result.setProcessDefinitionDeployedOn(deployedOn);

        final Timestamp     startedOnTimestamp = rs.getTimestamp("started_on");
        final ZonedDateTime startedOn          = startedOnTimestamp.toInstant().atZone(ZoneId.of("UTC"));
        result.setStartedOn(startedOn);

        return result;
    }

}
