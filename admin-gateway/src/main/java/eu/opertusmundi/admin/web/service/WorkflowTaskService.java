package eu.opertusmundi.admin.web.service;

import eu.opertusmundi.admin.web.model.workflow.CompleteTaskTaskCommandDto;
import eu.opertusmundi.common.model.ServiceException;

public interface WorkflowTaskService {

    void completeTask(CompleteTaskTaskCommandDto command) throws ServiceException;

}
