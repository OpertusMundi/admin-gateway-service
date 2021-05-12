package eu.opertusmundi.admin.web.controller.action;

import java.util.List;

import org.camunda.bpm.engine.rest.dto.CountResultDto;
import org.camunda.bpm.engine.rest.dto.runtime.IncidentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.common.feign.client.BpmServerFeignClient;
import eu.opertusmundi.common.model.RestResponse;
import feign.FeignException;

@RestController
public class WorkflowControllerImpl implements WorkflowController {

	private static final Logger logger = LoggerFactory.getLogger(WorkflowControllerImpl.class);
	
    @Autowired
    private ObjectProvider<BpmServerFeignClient> bpmClient;
    
    @Override
	public RestResponse<?> countIncidents() {
        try {
            final CountResultDto result = this.bpmClient.getObject().countIncidents();

            return RestResponse.result(result.getCount());
        } catch (final FeignException fex) {
        	logger.error("Operation has failed", fex);

            return RestResponse.failure();
        }
	}

	@Override
	public RestResponse<List<IncidentDto>> getIncidents() {
		try {
			final List<IncidentDto> result = this.bpmClient.getObject().getIncidents(
				null, null, null, null, "incidentTimestamp", "desc"
			);

			return RestResponse.result(result);
		} catch (final FeignException fex) {
			logger.error("Operation has failed", fex);

			return RestResponse.failure();
		}
	}

}