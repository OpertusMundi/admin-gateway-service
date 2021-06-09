package eu.opertusmundi.admin.web.model.workflow;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class IncidentDto {

    private String        processDefinitionId;
    private String        processDefinitionName;
    private String        processDefinitionKey;
    private Integer       processDefinitionVersion;
    private ZonedDateTime processDefinitionDeployedOn;
    private String        processInstanceId;
    private String        businessKey;
    private ZonedDateTime incidentDateTime;
    private String        incidentId;
    private String        incidentMessage;
    private String        incidentType;
    private String        activityName;
    private String        activityId;
    private String        taskWorker;
    private String        taskName;
    private String        taskErrorMessage;
    private String        taskErrorDetails;

}
