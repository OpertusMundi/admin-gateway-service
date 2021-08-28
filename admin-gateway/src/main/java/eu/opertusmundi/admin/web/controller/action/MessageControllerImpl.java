package eu.opertusmundi.admin.web.controller.action;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.common.feign.client.MessageServiceFeignClient;
import eu.opertusmundi.common.model.BasicMessageCode;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.message.client.ClientContactDto;
import eu.opertusmundi.common.model.message.client.ClientMessageCollectionResponse;
import eu.opertusmundi.common.model.message.client.ClientMessageCommandDto;
import eu.opertusmundi.common.model.message.client.ClientMessageDto;
import eu.opertusmundi.common.model.message.client.ClientMessageThreadResponse;
import eu.opertusmundi.common.model.message.server.ServerMessageCommandDto;
import eu.opertusmundi.common.model.message.server.ServerMessageDto;
import eu.opertusmundi.common.repository.AccountRepository;
import eu.opertusmundi.common.repository.HelpdeskAccountRepository;
import feign.FeignException;

@RestController
@Secured({ "ROLE_USER" })
public class MessageControllerImpl extends BaseController implements MessageController {

    @Autowired
    private ObjectProvider<MessageServiceFeignClient> messageClient;

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private HelpdeskAccountRepository helpdeskAccountRepository;

    @Override
    public RestResponse<?> findUnassignedMessages(Integer pageIndex, Integer pageSize, ZonedDateTime dateFrom, ZonedDateTime dateTo, Boolean read) {
        try {
            final ResponseEntity<RestResponse<PageResultDto<ServerMessageDto>>> e = this.messageClient.getObject()
                .getHelpdeskInbox(pageIndex, pageSize, dateFrom, dateTo, read);

            final RestResponse<PageResultDto<ServerMessageDto>> serviceResponse = e.getBody();

            if(!serviceResponse.getSuccess()) {
                // TODO: Add logging ...
                return RestResponse.failure();
            }

            final List<ClientContactDto> contacts = this.getContacts(serviceResponse.getResult().getItems());
              
            final PageResultDto<ClientMessageDto> serviceResult = serviceResponse.getResult().convert(ClientMessageDto::from);

            final ClientMessageCollectionResponse result = new ClientMessageCollectionResponse(serviceResult, contacts);

            return result;
        } catch (final FeignException fex) {
            final BasicMessageCode code = BasicMessageCode.fromStatusCode(fex.status());

            // TODO: Add logging ...

            return RestResponse.error(code, "An error has occurred");
        }
    }
    
    public RestResponse<?> countUnassignedMessages() {
        final Long result = this.messageClient.getObject().countUnassignedMessages().getBody().getResult();

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> findMessages(Integer pageIndex, Integer pageSize, ZonedDateTime dateFrom, ZonedDateTime dateTo, Boolean read) {
        try {
            final ResponseEntity<RestResponse<PageResultDto<ServerMessageDto>>> e = this.messageClient.getObject()
                .findMessages(this.currentUserKey(), pageIndex, pageSize, dateFrom, dateTo, read);

            final RestResponse<PageResultDto<ServerMessageDto>> serviceResponse = e.getBody();

            if(!serviceResponse.getSuccess()) {
                // TODO: Add logging ...
                return RestResponse.failure();
            }

            final List<ClientContactDto> contacts = this.getContacts(serviceResponse.getResult().getItems());
              
            final PageResultDto<ClientMessageDto> serviceResult = serviceResponse.getResult().convert(ClientMessageDto::from);

            final ClientMessageCollectionResponse result = new ClientMessageCollectionResponse(serviceResult, contacts);

            return result;
        } catch (final FeignException fex) {
            final BasicMessageCode code = BasicMessageCode.fromStatusCode(fex.status());

            // TODO: Add logging ...

            return RestResponse.error(code, "An error has occurred");
        }
    }
    
    public RestResponse<?> countUserNewMessages() {
        final Long result = this.messageClient.getObject().countUserNewMessages(this.currentUserKey()).getBody().getResult();

        return RestResponse.result(result);
    }
    
    @Override
    public RestResponse<?> assignMessage(UUID messageKey) {
        try {
            final ResponseEntity<RestResponse<ServerMessageDto>> e = this.messageClient.getObject()
                .assignMessage(messageKey, this.currentUserKey());

            final RestResponse<ServerMessageDto> serviceResponse = e.getBody();

            if (serviceResponse.getSuccess()) {
                final ClientMessageDto result = ClientMessageDto.from(serviceResponse.getResult());
                this.injectContracts(result);
                return RestResponse.result(result);
            }

            return RestResponse.failure();
        } catch (final FeignException fex) {
            final BasicMessageCode code = BasicMessageCode.fromStatusCode(fex.status());

            // TODO: Add logging ...

            return RestResponse.error(code, "An error has occurred");
        }
    }
    
    @Override
    public RestResponse<?> readMessage(UUID messageKey) {
        try {
            final ResponseEntity<RestResponse<ServerMessageDto>> e = this.messageClient.getObject().readMessage(this.currentUserKey(), messageKey);

            final RestResponse<ServerMessageDto> serviceResponse = e.getBody();

            if (serviceResponse.getSuccess()) {
                final ClientMessageDto result = ClientMessageDto.from(serviceResponse.getResult());
                this.injectContracts(result);
                return RestResponse.result(result);
            }

            return RestResponse.failure();
        } catch (final FeignException fex) {
            final BasicMessageCode code = BasicMessageCode.fromStatusCode(fex.status());

            // TODO: Add logging ...

            return RestResponse.error(code, "An error has occurred");
        }
    }
    
    @Override
    public RestResponse<?> sendMessage(UUID userKey, ClientMessageCommandDto clientMessage) {
        try {
            final ServerMessageCommandDto serverMessage = new ServerMessageCommandDto();

            serverMessage.setSender(this.currentUserKey());
            serverMessage.setRecipient(userKey);
            serverMessage.setText(clientMessage.getText());

            final ResponseEntity<RestResponse<ServerMessageDto>> e               = this.messageClient.getObject().sendMessage(serverMessage);
            final RestResponse<ServerMessageDto>                 serviceResponse = e.getBody();

            if (serviceResponse.getSuccess()) {
                final ClientMessageDto result = ClientMessageDto.from(serviceResponse.getResult());
                this.injectContracts(result);
                return RestResponse.result(result);
            }
            
            return RestResponse.failure();
        } catch (final FeignException fex) {
            final BasicMessageCode code = BasicMessageCode.fromStatusCode(fex.status());

            // TODO: Add logging ...

            return RestResponse.error(code, "An error has occurred");
        }
    }
    
    @Override
    public RestResponse<?> replyToMessage(UUID threadKey, ClientMessageCommandDto clientMessage) {
        try {
            final ServerMessageCommandDto serverMessage = new ServerMessageCommandDto();

            serverMessage.setSender(this.currentUserKey());
            serverMessage.setThread(threadKey);
            serverMessage.setText(clientMessage.getText());

            final ResponseEntity<RestResponse<ServerMessageDto>> e               = this.messageClient.getObject().sendMessage(serverMessage);
            final RestResponse<ServerMessageDto>                 serviceResponse = e.getBody();

            if (serviceResponse.getSuccess()) {
                final ClientMessageDto result = ClientMessageDto.from(serviceResponse.getResult());
                this.injectContracts(result);
                return RestResponse.result(result);
            }
            
            return RestResponse.failure();
        } catch (final FeignException fex) {
            final BasicMessageCode code = BasicMessageCode.fromStatusCode(fex.status());

            // TODO: Add logging ...

            return RestResponse.error(code, "An error has occurred");
        }
    }
    
    @Override
    public RestResponse<?> getMessageThread(UUID threadKey) {
        try {
            final ResponseEntity<RestResponse<List<ServerMessageDto>>> e = this.messageClient.getObject()
                .getMessageThread(threadKey, this.currentUserKey());

            final RestResponse<List<ServerMessageDto>> serviceResponse = e.getBody();

            if(!serviceResponse.getSuccess()) {
                return RestResponse.failure();
            }

            final List<ClientMessageDto> messages = serviceResponse.getResult().stream()
               .map(ClientMessageDto::from)
               .collect(Collectors.toList());


            final List<ClientContactDto>      contracts = this.getContacts(serviceResponse.getResult());
            final ClientMessageThreadResponse result    = new ClientMessageThreadResponse(messages, contracts);

            return result;
        } catch (final FeignException fex) {
            final BasicMessageCode code = BasicMessageCode.fromStatusCode(fex.status());

            // TODO: Add logging ...

            return RestResponse.error(code, "An error has occurred");
        }
    }
    
    private List<ClientContactDto> getContactsFromKeys(List<UUID> keys) {
        final List<ClientContactDto> contacts = new ArrayList<>();

        final List<UUID> uniqueContractKeys = keys.stream().filter(k -> k != null).distinct().collect(Collectors.toList());
        
        this.accountRepository.findAllByKey(uniqueContractKeys).stream()
            .map(ClientContactDto::new)
            .forEach(contacts::add);

        this.helpdeskAccountRepository.findAllByKey(uniqueContractKeys).stream()
            .map(ClientContactDto::new)
            .forEach(contacts::add);

        return contacts;
    }
    
    private void injectContracts(ClientMessageDto message) {
        final List<ClientContactDto> contacts = this.getContactsFromKeys(Arrays.asList(message.getSenderId(), message.getRecipientId()));
        
        message.setRecipient(contacts.stream().filter(c -> c.getId().equals(message.getRecipientId())).findFirst().orElse(null));
        message.setSender(contacts.stream().filter(c -> c.getId().equals(message.getSenderId())).findFirst().orElse(null));
    }
    
    private List<ClientContactDto> getContacts(List<ServerMessageDto> messages) {
        final List<UUID> contractKeys = new ArrayList<>();

        messages.stream()
            .map(i -> i.getSender())
            .forEach(contractKeys::add);

        messages.stream()
            .map(i -> i.getRecipient())
            .forEach(contractKeys::add);

        return this.getContactsFromKeys(contractKeys);
    }

}
