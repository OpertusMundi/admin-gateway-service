package eu.opertusmundi.admin.web.controller.action;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.opertusmundi.common.model.BaseResponse;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.message.EnumMessageStatus;
import eu.opertusmundi.common.model.message.client.ClientContactDto;
import eu.opertusmundi.common.model.message.client.ClientMessageCommandDto;

@RequestMapping(value = "/action/messages", produces = MediaType.APPLICATION_JSON_VALUE)
public interface MessageController {

    /**
     * Find contacts
     *
     * @param email
     *
     * @return An instance of {@link BaseResponse}
     */
    @GetMapping(value = "/helpdesk/contacts")
    RestResponse<List<ClientContactDto>> findContacts(@RequestParam(name = "email") String email);

    /**
     * Count unassigned messages
     *
     * @return An instance of {@link BaseResponse}
     */
    @GetMapping(value = "/helpdesk/inbox/count")
    RestResponse<?> countUnassignedMessages();

    /**
     * Find unassigned messages
     *
     * @param pageIndex
     * @param pageSize
     * @param dateFrom
     * @param dateTo
     * @param read
     *
     * @return An instance of {@link BaseResponse}
     */
    @GetMapping(value = "/helpdesk/inbox")
    RestResponse<?> findUnassignedMessages(
        @RequestParam(name = "page", required = false) Integer pageIndex,
        @RequestParam(name = "size", required = false) Integer pageSize,
        @RequestParam(name = "dateFrom", required = false) ZonedDateTime dateFrom,
        @RequestParam(name = "dateTo", required = false) ZonedDateTime dateTo,
        @RequestParam(name = "read", required = false) Boolean read
    );

    /**
     * Count user new messages
     *
     * @return An instance of {@link BaseResponse}
     */
    @GetMapping(value = "/user/inbox/count")
    RestResponse<?> countUserNewMessages();

    /**
     * Get user messages
     *
     * @param pageIndex
     * @param pageSize
     * @param dateFrom
     * @param dateTo
     * @param read
     *
     * @return An instance of {@link BaseResponse}
     */
    @GetMapping(value = "/user/inbox")
    RestResponse<?> findMessages(
        @RequestParam(name = "page", required = false) Integer pageIndex,
        @RequestParam(name = "size", required = false) Integer pageSize,
        @RequestParam(name = "dateFrom", required = false) ZonedDateTime dateFrom,
        @RequestParam(name = "dateTo", required = false) ZonedDateTime dateTo,
        @RequestParam(name = "status", required = false, defaultValue = "ALL") EnumMessageStatus status,
        @RequestParam(name = "contact", required = false) UUID contactKey
    );

    /**
     * Assign message
     *
     * @param pageIndex
     * @param pageSize
     * @param dateFrom
     * @param dateTo
     * @param read
     *
     * @return An instance of {@link BaseResponse}
     */
    @PostMapping(value = "/{messageKey}")
    RestResponse<?> assignMessage(@PathVariable(name = "messageKey") UUID messageKey);

    /**
     * Mark message as read
     *
     * @param messageKey
     * @return
     */
    @PutMapping(value = "/{messageKey}")
    RestResponse<?> readMessage(@PathVariable(name = "messageKey") UUID messageKey);

    /**
     * Send a message to the platform user with the specified key
     *
     * @param userKey Recipient user unique key
     * @param message Message command object
     *
     * @return An instance of {@link BaseResponse}
     */
    @PostMapping(value = "/user/{userKey}")
    RestResponse<?> sendMessage(@PathVariable(name = "userKey", required = true) UUID userKey, @RequestBody ClientMessageCommandDto message);

    /**
     * Reply to message
     *
     * @param key Reply to message with the specified key
     * @param message Message command object
     *
     * @return An instance of {@link BaseResponse}
     */
    @PostMapping(value = "/thread/{threadKey}")
    RestResponse<?> replyToMessage(@PathVariable(name = "threadKey") UUID threadKey, @RequestBody ClientMessageCommandDto command);

    /**
     * Get all thread messages
     *
     * @param threadKey The key of any message thread
     *
     * @return An instance of {@link BaseResponse}
     */
    @GetMapping(value = "/thread/{threadKey}")
    RestResponse<?> getMessageThread(@PathVariable(name = "threadKey") UUID threadKey);

}
