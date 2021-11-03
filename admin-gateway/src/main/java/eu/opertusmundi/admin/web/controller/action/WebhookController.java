package eu.opertusmundi.admin.web.controller.action;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.admin.WebhookRegistration;

@RequestMapping(value = "/action/admin/webhooks", produces = MediaType.APPLICATION_JSON_VALUE)
@Secured({"ROLE_ADMIN"})
public interface WebhookController {

    @GetMapping(value = {""})
    RestResponse<List<WebhookRegistration>> getAll();

    @PutMapping(value = {""})
    RestResponse<List<WebhookRegistration>> enableAll();

    @DeleteMapping(value = {""})
    RestResponse<List<WebhookRegistration>> disableAll();

}
