package eu.opertusmundi.admin.web.model;

import eu.opertusmundi.common.model.MessageCode;

public enum AdminMessageCode implements MessageCode {
    // Account error codes
    MarketplaceAccountExists,
    
    IdpAccountAlreadyExists,
    IdpAccountCreateFailed,
    IdpAccountDeleteFailed,
    
    CannotDeleteSelf,
    CannotRevokeLastAdmin,
    CannotUpdatePassword,
    
    // Provider error codes
    ExternalProviderAlreadyExists,
    OpenDatasetProviderAlreadyExists,
    ;

    @Override
    public String key() {
        return this.getClass().getSimpleName() + '.' + this.name();
    }

}
