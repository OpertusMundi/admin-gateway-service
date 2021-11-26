package eu.opertusmundi.admin.web.controller.action;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.logging.ElasticEventDto;
import eu.opertusmundi.common.model.logging.ElasticEventQueryDto;

@RequestMapping(value = "/action/events", produces = MediaType.APPLICATION_JSON_VALUE)
public interface EventController {

    @PostMapping(value = {""})
    RestResponse<PageResultDto<ElasticEventDto>> findAll(@RequestBody ElasticEventQueryDto query);

}
