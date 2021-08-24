package eu.opertusmundi.admin.web.controller.action;

import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.logging.EnumEventLevel;
import eu.opertusmundi.common.model.logging.EnumEventSortField;
import eu.opertusmundi.common.model.logging.EventDto;

@RequestMapping(value = "/action/events", produces = MediaType.APPLICATION_JSON_VALUE)
public interface EventController {

    @GetMapping(value = { "" })
    RestResponse<PageResultDto<EventDto>> findAll(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "25") @Max(100) @Min(1) int size,
        @RequestParam(name = "level", required = false) Set<EnumEventLevel> level,
        @RequestParam(name = "logger", required = false) String logger,
        @RequestParam(name = "userName", required = false) String userName,
        @RequestParam(name = "clientAddress", required = false) String clientAddress,
        @RequestParam(name = "orderBy", defaultValue = "TIMESTAMP") EnumEventSortField orderBy,
        @RequestParam(name = "order", defaultValue = "DESC") EnumSortingOrder order
    );

}
