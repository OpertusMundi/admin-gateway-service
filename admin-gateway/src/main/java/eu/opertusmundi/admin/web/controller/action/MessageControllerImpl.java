package eu.opertusmundi.admin.web.controller.action;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.message.EnumMessageStatus;
import eu.opertusmundi.common.model.message.client.ClientContactDto;
import eu.opertusmundi.common.model.message.client.ClientMessageCollectionResponse;
import eu.opertusmundi.common.model.message.client.ClientMessageCommandDto;
import eu.opertusmundi.common.model.message.client.ClientMessageDto;
import eu.opertusmundi.common.model.message.client.ClientMessageThreadResponse;
import eu.opertusmundi.common.service.messaging.MessageService;

@RestController
@Secured({ "ROLE_USER" })
public class MessageControllerImpl extends BaseController implements MessageController {

    private final MessageService messageService;

    public MessageControllerImpl(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public RestResponse<List<ClientContactDto>> findContacts(String email) {
        final List<ClientContactDto> result = this.messageService.findContacts(email);
        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> countUnassignedMessages() {
        final Long result = this.messageService.countUnassignedMessages();
        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> findUnassignedMessages(Integer pageIndex, Integer pageSize, ZonedDateTime dateFrom, ZonedDateTime dateTo, Boolean read) {
        final PageResultDto<ClientMessageDto> messages = this.messageService.findUnassignedMessages(
            pageIndex, pageSize, dateFrom, dateTo, read
        );
        final List<ClientContactDto>          contacts = this.messageService.findContacts(messages.getItems());
        final ClientMessageCollectionResponse result   = new ClientMessageCollectionResponse(messages, contacts);
        return result;
    }

    @Override
    public RestResponse<?> findMessages(
        Integer pageIndex, Integer pageSize, ZonedDateTime dateFrom, ZonedDateTime dateTo, EnumMessageStatus status, UUID contactKey
    ) {
            final PageResultDto<ClientMessageDto> messages = this.messageService.findMessages(
                this.currentUserKey(), pageIndex, pageSize, dateFrom, dateTo, status, contactKey
            );
            final List<ClientContactDto>          contacts = this.messageService.findContacts(messages.getItems());
            final ClientMessageCollectionResponse result   = new ClientMessageCollectionResponse(messages, contacts);
            return result;
    }

    public RestResponse<?> countUserNewMessages() {
        final Long result = this.messageService.countUserNewMessages(this.currentUserKey());
        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> assignMessage(UUID messageKey) {
        final ClientMessageDto result = this.messageService.assignMessage(messageKey, this.currentUserKey());
        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> readMessage(UUID messageKey) {
        final ClientMessageDto result = this.messageService.readMessage(this.currentUserKey(), messageKey);
        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> sendMessage(UUID userKey, ClientMessageCommandDto clientMessage) {
        final ClientMessageDto result = this.messageService.sendMessage(this.currentUserKey(), userKey, clientMessage);
        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> replyToMessage(UUID threadKey, ClientMessageCommandDto clientMessage) {
        final ClientMessageDto result = this.messageService.replyToMessage(this.currentUserKey(), threadKey, clientMessage);
        return RestResponse.result(result);
    }

    @Override
    public RestResponse<?> getMessageThread(UUID threadKey) {
        final List<ClientMessageDto>      messages  = this.messageService.getMessageThread(threadKey, this.currentUserKey());
        final List<ClientContactDto>      contracts = this.messageService.findContacts(messages);
        final ClientMessageThreadResponse result    = new ClientMessageThreadResponse(messages, contracts);
        return result;
    }

}
