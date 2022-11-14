package eu.opertusmundi.admin.web.model.workflow;

import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DeploymentDto {

    private String        id;
    private String        name;
    private String        source;
    private LocalDateTime deploymentTime;

    public static DeploymentDto from(org.camunda.bpm.engine.rest.dto.repository.DeploymentDto d) {
        final var result = new DeploymentDto();

        result.id             = d.getId();
        result.name           = d.getName();
        result.source         = d.getSource();
        result.deploymentTime = LocalDateTime.ofInstant(d.getDeploymentTime().toInstant(), ZoneId.systemDefault());

        return result;
    }
}
