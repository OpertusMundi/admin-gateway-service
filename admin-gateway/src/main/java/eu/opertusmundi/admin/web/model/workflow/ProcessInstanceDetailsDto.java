package eu.opertusmundi.admin.web.model.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.camunda.bpm.engine.rest.dto.history.HistoricActivityInstanceDto;
import org.camunda.bpm.engine.rest.dto.history.HistoricProcessInstanceDto;
import org.camunda.bpm.engine.rest.dto.runtime.IncidentDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eu.opertusmundi.common.model.account.AccountDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessInstanceDetailsDto {

    private List<HistoricActivityInstanceDto> activities;
    private List<IncidentDto>                 incidents;
    private HistoricProcessInstanceDto        instance;
    private AccountDto                        owner;

    @JsonInclude(Include.NON_NULL)
    private ProcessInstanceResource resource;

    @JsonInclude(Include.NON_EMPTY)
    private String bpmn2Xml;

    @Setter(value = AccessLevel.PROTECTED)
    private List<VariableDto> variables = new ArrayList<>();

    private Map<String, String> errorDetails = new HashMap<>();

    public Optional<Object> getVariable(String name) {
        return variables.stream()
            .filter(v -> v.getName().equals(name)).map(v -> v.getValue())
            .findFirst();
    }

    public String getVariableAsString(String name) {
        return this.getVariable(name).map(Object::toString).orElse(null);
    }
}
