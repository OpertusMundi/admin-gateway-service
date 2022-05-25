package eu.opertusmundi.admin.web.model.workflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "taskName", visible = true
)
@JsonSubTypes({
    @Type(name = TaskNameConstants.PUBLISH_SET_ERROR_TASK, value = SetPublishErrorTaskCommandDto.class),
})
@Getter
@Setter
public class CompleteTaskTaskCommandDto {

    @JsonIgnore
    protected String processInstanceId;

    protected String taskName;

}
