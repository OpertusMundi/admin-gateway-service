package eu.opertusmundi.admin.web.controller.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.admin.WebhookRegistration;
import eu.opertusmundi.common.service.MangoPayWebhookHelper;

@RestController
public class WebhookControllerImpl extends BaseController implements WebhookController {

    @Autowired
    private MangoPayWebhookHelper webhookHelper;

    @Override
    public RestResponse<List<WebhookRegistration>> getAll() {
        final List<WebhookRegistration> result = this.webhookHelper.getAll();

        return RestResponse.result(result);
    }

    @Override
    public RestResponse<List<WebhookRegistration>> enableAll() {
        this.webhookHelper.enableAll();

        return this.getAll();
    }

    @Override
    public RestResponse<List<WebhookRegistration>> disableAll() {
        this.webhookHelper.disableAll();

        return this.getAll();
    }
}
