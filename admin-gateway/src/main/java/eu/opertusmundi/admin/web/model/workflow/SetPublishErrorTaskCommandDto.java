package eu.opertusmundi.admin.web.model.workflow;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetPublishErrorTaskCommandDto extends CompleteTaskTaskCommandDto {

    @NotEmpty
    private String message;
}
