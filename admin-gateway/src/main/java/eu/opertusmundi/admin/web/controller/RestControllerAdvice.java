package eu.opertusmundi.admin.web.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import eu.opertusmundi.common.model.ApplicationException;
import eu.opertusmundi.common.model.BasicMessageCode;
import eu.opertusmundi.common.model.MessageCode;
import eu.opertusmundi.common.model.RestResponse;

@ControllerAdvice(annotations = { Controller.class })
public class RestControllerAdvice {

	private static final Logger logger = LoggerFactory.getLogger(RestControllerAdvice.class);

	public static final String DEFAULT_ERROR_VIEW = "error";
	public static final String STATUS_CODE = "404";
	public static final String TYPE = "Custom Type";

	@ExceptionHandler(value = { NoHandlerFoundException.class })
	public ModelAndView defaultErrorHandler(HttpServletRequest request, Exception e) {
		final ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
		mav.addObject("timestamp", new Date());
		mav.addObject("status", STATUS_CODE);
		mav.addObject("type", TYPE);
		mav.addObject("message", String.format("The requested url is: %s", request.getRequestURL()));
		return mav;
	}

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(Exception.class)
	public @ResponseBody RestResponse<Void> handleException(Exception ex) {

		logger.error(ex.getMessage(), ex);

		final MessageCode code = this.exceptionToErrorCode(ex);

		return RestResponse.failure(code, ex.getMessage());
	}

	private MessageCode exceptionToErrorCode(Exception ex) {
		if (ex instanceof ApplicationException) {
			return ((ApplicationException) ex).getCode();
		}
		if (ex instanceof DataIntegrityViolationException) {
			return BasicMessageCode.ForeignKeyConstraint;
		}

		return BasicMessageCode.InternalServerError;
	}

}
