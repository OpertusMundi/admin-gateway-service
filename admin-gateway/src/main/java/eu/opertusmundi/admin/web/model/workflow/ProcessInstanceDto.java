package eu.opertusmundi.admin.web.model.workflow;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProcessInstanceDto {

    private String        processDefinitionId;
    private String        processDefinitionName;
    private String        processDefinitionKey;
    private Integer       processDefinitionVersion;
    private String        processDefinitionVersionTag;
    private ZonedDateTime processDefinitionDeployedOn;
    private String        processInstanceId;
    private String        businessKey;
    private ZonedDateTime startedOn;
    private ZonedDateTime completedOn;
    private Long          incidentCount;

}
