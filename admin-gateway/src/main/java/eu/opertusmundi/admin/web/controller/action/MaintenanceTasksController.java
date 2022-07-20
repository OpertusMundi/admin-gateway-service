package eu.opertusmundi.admin.web.controller.action;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.opertusmundi.admin.web.model.workflow.MaintenanceTasksCommandDto;
import eu.opertusmundi.common.model.BaseResponse;

@RequestMapping(produces = "application/json")
public interface MaintenanceTasksController {

    @PostMapping(value = "/action/maintenance/tasks")
    BaseResponse startTasks(@RequestBody MaintenanceTasksCommandDto command);

}
