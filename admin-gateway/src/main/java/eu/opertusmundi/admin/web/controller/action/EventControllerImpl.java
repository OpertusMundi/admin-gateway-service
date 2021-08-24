package eu.opertusmundi.admin.web.controller.action;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.common.domain.EventEntity;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.logging.EnumEventLevel;
import eu.opertusmundi.common.model.logging.EnumEventSortField;
import eu.opertusmundi.common.model.logging.EventDto;
import eu.opertusmundi.common.repository.EventRepository;

@RestController
@Secured({"ROLE_ADMIN"})
public class EventControllerImpl extends BaseController implements EventController {

    @Autowired
    private EventRepository eventRepository;


    @Override
    public RestResponse<PageResultDto<EventDto>> findAll(
        int page, int size, 
        Set<EnumEventLevel> level, String logger, String userName, String clientAddress, 
        EnumEventSortField orderBy, EnumSortingOrder order
    ) {
        final Direction   direction   = order == EnumSortingOrder.DESC ? Direction.DESC : Direction.ASC;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, orderBy.getValue()));

        if (level != null && level.isEmpty()) {
            level = null;
        }
        if (StringUtils.isBlank(logger)) {
            logger = null;
        } else {
            if (!logger.startsWith("%")) {
                logger = "%" + logger;
            }
            if (!logger.endsWith("%")) {
                logger += "%";
            }
        }
        if (StringUtils.isBlank(userName)) {
            userName = null;
        } else {
            if (!userName.startsWith("%")) {
                userName = "%" + userName;
            }
            if (!userName.endsWith("%")) {
                userName += "%";
            }
        }
        if (StringUtils.isBlank(clientAddress)) {
            clientAddress = null;
        } else {
            if (!clientAddress.startsWith("%")) {
                clientAddress = "%" + clientAddress;
            }
            if (!clientAddress.endsWith("%")) {
                clientAddress += "%";
            }
        }

        final Page<EventDto> p = this.eventRepository
            .findAll(level, logger, userName, clientAddress, pageRequest)
            .map(EventEntity::toDto);

        final long                    count   = p.getTotalElements();
        final List<EventDto>          records = p.stream().collect(Collectors.toList());
        final PageResultDto<EventDto> result  = PageResultDto.of(page, size, records, count);

        return RestResponse.result(result);
    }


}
