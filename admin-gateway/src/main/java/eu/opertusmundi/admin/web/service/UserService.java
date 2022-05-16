package eu.opertusmundi.admin.web.service;

import java.util.Optional;

import javax.annotation.Nullable;

import org.springframework.data.domain.Pageable;

import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.ServiceException;
import eu.opertusmundi.common.model.account.helpdesk.HelpdeskAccountCommandDto;
import eu.opertusmundi.common.model.account.helpdesk.HelpdeskAccountDto;
import eu.opertusmundi.common.model.account.helpdesk.RoleCommand;

public interface UserService {

    PageResultDto<HelpdeskAccountDto> findAll(String email, Pageable pageable);

    Optional<HelpdeskAccountDto> findOne(int id);

    HelpdeskAccountDto create(Integer creatorId, HelpdeskAccountCommandDto command) throws ServiceException;

    HelpdeskAccountDto update(Integer creatorId, HelpdeskAccountCommandDto command);

    @Nullable String registerToIdp(int id) throws ServiceException;
    
    @Nullable String resetPassword(int id) throws ServiceException;
    
    HelpdeskAccountDto grantRole(RoleCommand command);

    HelpdeskAccountDto revokeRole(RoleCommand command);

    void setPassword(int id, String password);

    void delete(int id);

}
