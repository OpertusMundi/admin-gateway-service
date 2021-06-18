package eu.opertusmundi.admin.web.model.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.rest.dto.history.HistoricActivityInstanceDto;
import org.camunda.bpm.engine.rest.dto.history.HistoricIncidentDto;
import org.camunda.bpm.engine.rest.dto.history.HistoricProcessInstanceDto;
import org.camunda.bpm.engine.rest.dto.history.HistoricVariableInstanceDto;

import eu.opertusmundi.common.model.account.AccountDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryProcessInstanceDetailsDto {

    private List<HistoricActivityInstanceDto> activities;
    private List<HistoricIncidentDto>         incidents;
    private HistoricProcessInstanceDto        instance;
    private AccountDto                        owner;
    private List<HistoricVariableInstanceDto> variables;

    private Map<String, String> errorDetails = new HashMap<>();

}
