package eu.opertusmundi.admin.web.controller.action;

import javax.validation.Valid;

import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.analytics.AssetViewQuery;
import eu.opertusmundi.common.model.analytics.DataSeries;
import eu.opertusmundi.common.model.analytics.SalesQuery;

@RequestMapping(path = "/action/analytics", produces = "application/json")
public interface AnalyticsController {

    /**
     * Query asset sales data
     *
     * @param request The query to execute
     *
     * @return A {@link RestResponse} with a {@link DataSeries} result
     */
    @PostMapping(value = "/sales", consumes = {"application/json"})
    @Secured({"ROLE_ADMIN"})
    @Validated
    RestResponse<?> executeSalesQuery(@Valid @RequestBody SalesQuery query, BindingResult validationResult);

    /**
     * Query asset views
     *
     * @param request The query to execute
     *
     * @return A {@link RestResponse} with a {@link DataSeries} result
     */
    @PostMapping(value = "/assets", consumes = {"application/json"})
    @Secured({"ROLE_ADMIN"})
    @Validated
    RestResponse<?> executeAssetQuery(@Valid @RequestBody AssetViewQuery query, BindingResult validationResult);

}