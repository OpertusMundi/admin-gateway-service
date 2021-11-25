package eu.opertusmundi.admin.web.controller.action;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.logging.ElasticEVentQueryDto;
import eu.opertusmundi.common.model.logging.ElasticEventDto;

@RequestMapping(value = "/action/events", produces = MediaType.APPLICATION_JSON_VALUE)
public interface EventController {

    @GetMapping(value = {""})
    RestResponse<PageResultDto<ElasticEventDto>> findAll(ElasticEVentQueryDto query);

}
