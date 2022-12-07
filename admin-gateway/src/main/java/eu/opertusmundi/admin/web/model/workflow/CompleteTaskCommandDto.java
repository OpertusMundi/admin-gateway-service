package eu.opertusmundi.admin.web.model.workflow;

import java.util.UUID;

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
    @Type(name = TaskNameConstants.CONSUMER_REGISTRATION_SET_ERROR,   value = SetErrorTaskCommandDto.class),
    @Type(name = TaskNameConstants.PROVIDER_REGISTRATION_SET_ERROR,   value = SetErrorTaskCommandDto.class),
    @Type(name = TaskNameConstants.PUBLISH_CATALOGUE_ASSET_SET_ERROR, value = SetErrorTaskCommandDto.class),
    @Type(name = TaskNameConstants.PUBLISH_USER_SERVICE_SET_ERROR,    value = SetErrorTaskCommandDto.class),
})
@Getter
@Setter
public class CompleteTaskCommandDto {

    @JsonIgnore
    protected UUID helpdeskUserKey;

    @JsonIgnore
    protected String processInstanceId;

    protected String taskName;

}
