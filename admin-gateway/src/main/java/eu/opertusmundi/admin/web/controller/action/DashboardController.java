package eu.opertusmundi.admin.web.controller.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.admin.web.model.dto.DashboardDto;
import eu.opertusmundi.admin.web.repository.DashboardRepository;
import eu.opertusmundi.common.model.RestResponse;

/**
 * Provides actions for loading application overview data
 */
@RestController
@Secured({ "ROLE_USER", "ROLE_ADMIN" })
@RequestMapping(produces = "application/json")
public class DashboardController extends BaseController {

	@Autowired
	private DashboardRepository dashboardRepository;

	/**
	 * Returns data for several KPIs (key performance indicators) relevant to the
	 * workbench application
	 *
	 * @return an instance of {@link DashboardDto}}
	 * @throws Exception if a data access operation fails
	 */
	@RequestMapping(value = "/action/dashboard", method = RequestMethod.GET)
	public RestResponse<DashboardDto> getDashboard() throws Exception {

		final Integer accountId = this.authenticationFacade.isSystemAdmin() ? null : this.authenticationFacade.getCurrentUserId();

		final DashboardDto dashboard = this.dashboardRepository.getDashboard(accountId);

		return RestResponse.result(dashboard);
	}

}
