package eu.opertusmundi.admin.web.model.workflow;

import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessDefinitionHeaderDto {

    @JsonIgnore
    private String id;

    private String key;
    private String name;
    private int    version;
    private String versionTag;

    public static ProcessDefinitionHeaderDto from(ProcessDefinitionDto d) {
        final ProcessDefinitionHeaderDto result = new ProcessDefinitionHeaderDto();

        result.setId(d.getId());
        result.setKey(d.getKey());
        result.setName(d.getName());
        result.setVersion(d.getVersion());
        result.setVersionTag(d.getVersionTag());

        return result;
    }
}
