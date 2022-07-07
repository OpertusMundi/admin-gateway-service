package eu.opertusmundi.admin.web.service;

import eu.opertusmundi.admin.web.model.workflow.CompleteTaskCommandDto;
import eu.opertusmundi.common.model.ServiceException;

public interface WorkflowTaskService {

    void completeTask(CompleteTaskCommandDto command) throws ServiceException;

}
