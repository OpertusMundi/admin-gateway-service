package eu.opertusmundi.admin.web.model.workflow;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetryExternalTaskCommandDto {

    @NotEmpty
    private String processInstanceId;
    
    @NotEmpty
    private String externalTaskId;

}
