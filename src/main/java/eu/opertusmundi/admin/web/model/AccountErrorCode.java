package eu.opertusmundi.admin.web.model;

import eu.opertusmundi.common.model.MessageCode;

public enum AccountErrorCode implements MessageCode
{
    UNKNOWN,
    ACCOUNT_NOT_SET,
    ACCOUNT_NOT_FOUND,
    ;

    @Override
    public String key()
    {
        return this.getClass().getSimpleName() + '.' + this.name();
    }

}
