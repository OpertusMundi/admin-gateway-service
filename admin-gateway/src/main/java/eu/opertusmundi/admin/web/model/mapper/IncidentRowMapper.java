package eu.opertusmundi.admin.web.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.jdbc.core.RowMapper;

import eu.opertusmundi.admin.web.model.workflow.IncidentDto;

public class IncidentRowMapper implements RowMapper<IncidentDto> {

    @Override
    public IncidentDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        final IncidentDto result = new IncidentDto();

        result.setActivityId(rs.getString("activity_id"));
        result.setActivityName(rs.getString("activity_name"));
        result.setBusinessKey(rs.getString("business_key"));
        result.setIncidentId(rs.getString("incident_id"));
        result.setIncidentMessage(rs.getString("incident_message"));
        result.setIncidentType(rs.getString("incident_type"));
        result.setProcessDefinitionId(rs.getString("process_definition_id"));
        result.setProcessDefinitionKey(rs.getString("process_definition_key"));
        result.setProcessDefinitionName(rs.getString("process_definition_name"));
        result.setProcessDefinitionVersion(rs.getInt("process_definition_version"));
        result.setProcessDefinitionVersionTag(rs.getString("process_definition_version_tag"));
        result.setProcessInstanceId(rs.getString("process_instance_id"));
        result.setTaskErrorDetails(rs.getString("task_error_details"));
        result.setTaskErrorMessage(rs.getString("task_error_message"));
        result.setTaskName(rs.getString("task_name"));
        result.setTaskWorker(rs.getString("task_worker"));

        final Timestamp     deployedOnTimestamp = rs.getTimestamp("process_definition_deployed_on");
        final ZonedDateTime deployedOn          = deployedOnTimestamp.toInstant().atZone(ZoneId.of("UTC"));
        result.setProcessDefinitionDeployedOn(deployedOn);

        final Timestamp     IncidentTimestamp = rs.getTimestamp("incident_datetime");
        final ZonedDateTime IncidentDateTime  = IncidentTimestamp.toInstant().atZone(ZoneId.of("UTC"));
        result.setIncidentDateTime(IncidentDateTime);

        return result;
    }

}
