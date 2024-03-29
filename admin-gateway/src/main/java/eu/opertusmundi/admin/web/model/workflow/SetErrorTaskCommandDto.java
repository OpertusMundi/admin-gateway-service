package eu.opertusmundi.admin.web.model.workflow;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetErrorTaskCommandDto extends CompleteTaskCommandDto {

    @NotEmpty
    private String message;
}
