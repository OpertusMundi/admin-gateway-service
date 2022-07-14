package eu.opertusmundi.admin.web.service;

import java.util.UUID;

import eu.opertusmundi.common.model.ServiceException;
import eu.opertusmundi.common.model.account.AccountDto;

public interface MarketplaceUserService {

    /**
     * Toggle tester status for the account with the given {@code key}.
     *
     * @param adminUserKey
     * @param userKey
     * @return
     */
    AccountDto toggleTesterStatus(UUID adminUserKey, UUID userKey);

    /**
     * Delete all the data related for the given user key
     *
     * <p>
     * A user can be deleted only if she has the {@code ROLE_TESTER} role
     * assigned.
     *
     * <p>
     * If the parameter {@code fileSystemDeleted} is {@code true}, all the files
     * in the user file system will be deleted. Still, the directories like
     * {@code .quota} will be preserved.
     *
     * <p>
     * If the parameter {@code accountDeleted} is {@code true}, the user will be
     * also deleted.
     *
     * @param startUserKey
     * @param deletedUserKey
     * @param accountDeleted
     * @param fileSystemDeleted
     * @throws ServiceException
     */
    void delete(UUID startUserKey, UUID deletedUserKey, boolean accountDeleted, boolean fileSystemDeleted) throws ServiceException;

}
