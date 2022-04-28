package eu.opertusmundi.admin.web.model;

import eu.opertusmundi.common.model.MessageCode;

public enum AdminMessageCode implements MessageCode {
    ExternalProviderAlreadyExists,
    OpenDatasetProviderAlreadyExists,
    ;

    @Override
    public String key() {
        return this.getClass().getSimpleName() + '.' + this.name();
    }

}
