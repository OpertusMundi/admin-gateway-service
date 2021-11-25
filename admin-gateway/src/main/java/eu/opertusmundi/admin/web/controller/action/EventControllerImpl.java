package eu.opertusmundi.admin.web.controller.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.admin.web.service.LoggingElasticSearchService;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.logging.ElasticEVentQueryDto;
import eu.opertusmundi.common.model.logging.ElasticEventDto;

@RestController
@Secured({"ROLE_ADMIN"})
public class EventControllerImpl extends BaseController implements EventController {

    @Autowired
    private LoggingElasticSearchService loggingService;

    @Override
    public RestResponse<PageResultDto<ElasticEventDto>> findAll(ElasticEVentQueryDto query) {
        final PageResultDto<ElasticEventDto> result = this.loggingService.search(query);

        return RestResponse.result(result);
    }

}
