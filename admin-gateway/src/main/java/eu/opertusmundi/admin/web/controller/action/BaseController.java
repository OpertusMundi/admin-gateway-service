package eu.opertusmundi.admin.web.controller.action;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.opertusmundi.admin.web.model.EnumRole;
import eu.opertusmundi.admin.web.service.IAuthenticationFacade;
import eu.opertusmundi.common.model.ApplicationException;
import eu.opertusmundi.common.model.BasicMessageCode;
import eu.opertusmundi.common.model.Message;
import eu.opertusmundi.common.model.MessageCode;
import eu.opertusmundi.common.model.RestResponse;

public abstract class BaseController {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	protected IAuthenticationFacade authenticationFacade;

	@Autowired
	protected MessageSource messageSource;

    protected Integer currentUserId() {
        return this.authenticationFacade.getCurrentUserId();
    }

    protected String currentUserName() {
        return this.authenticationFacade.getCurrentUserName();
    }

    protected Locale currentUserLocale() {
        return this.authenticationFacade.getCurrentUserLocale();
    }

    protected boolean hasRole(EnumRole role) {
        return this.authenticationFacade.hasRole(role);
    }

    protected void hasAnyRole(EnumRole... roles) {
        if (!this.authenticationFacade.hasAnyRole(roles)) {
            throw this.accessDenied();
        }
    }

    protected boolean isSystemAdmin() {
        return this.hasRole(EnumRole.ADMIN);
    }

    private ApplicationException accessDenied() {
        return ApplicationException
            .fromPattern(BasicMessageCode.Unauthorized)
            .withFormattedMessage(this.messageSource, this.currentUserLocale());
    }

    protected RestResponse<?> exceptionToResponse(Exception ex, Message.EnumLevel level) {
        if (ex instanceof IOException) {
            return RestResponse.failure(BasicMessageCode.IOError, "An unknown error has occurred", level);
        }


        if (ex instanceof ApplicationException) {
            final ApplicationException typedEx = (ApplicationException) ex;
            return RestResponse.failure(typedEx.toError());
        }

        if (ex instanceof UnsupportedOperationException) {
            return RestResponse.failure(BasicMessageCode.NotImplemented, "Action is not implemented", level);
        }

        return RestResponse.failure(BasicMessageCode.InternalServerError, "An unknown error has occurred", level);
    }

	protected void createErrorResponse(HttpServletResponse response, int status, Exception exception) {
		this.createErrorResponse(response, status, BasicMessageCode.InternalServerError, "An unknown error has occurred.");
	}

	protected void createErrorResponse(HttpServletResponse response, int status, MessageCode code, String message) {

		try {
			final RestResponse<?> data = RestResponse.failure(code, message);

			response.setStatus(status);
			response.getOutputStream().print(this.objectMapper.writeValueAsString(data));
		} catch (final Exception ex) {
			// Ignore
		}
	}

}
