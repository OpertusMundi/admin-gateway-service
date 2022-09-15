package eu.opertusmundi.admin.web.service;

import java.util.List;

import eu.opertusmundi.admin.web.model.workflow.ProcessInstanceResource;
import eu.opertusmundi.admin.web.model.workflow.VariableDto;

public interface ProcessInstanceResourceResolver {

    ProcessInstanceResource resolve(String workflowKey, String businessKey, List<VariableDto> variables);

}
