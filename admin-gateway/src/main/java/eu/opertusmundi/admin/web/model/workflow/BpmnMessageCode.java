package eu.opertusmundi.admin.web.model.workflow;

import eu.opertusmundi.common.model.MessageCode;

public enum BpmnMessageCode implements MessageCode {
    ProcessInstanceNotFound,
    ActivityInstanceNotFound,
    ActivityInstanceNotActive,
    ActivityNotFound,
    ;

    @Override
    public String key() {
        return this.getClass().getSimpleName() + '.' + this.name();
    }

}
