package eu.opertusmundi.admin.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.opertusmundi.common.model.BasicMessageCode;
import eu.opertusmundi.common.model.RestResponse;

@Controller
public class HomeController {

    @Autowired
    private MessageSource messageSource;

	@RequestMapping("*")
	public String index(HttpSession session, HttpServletRequest request) {
		// Prevent infinite redirects
		if (request.getServletPath().equalsIgnoreCase("/helpdesk/")) {
			return "index";
		}
		return "redirect:/helpdesk/";
	}

	@RequestMapping("/helpdesk/**")
	public String helpdesk(HttpSession session, HttpServletRequest request) {
		// Handle all requests except for API calls
		return "index";
	}

    /**
     * Handles HTML error pages
     */
    @GetMapping(path = "/error/{id}", produces = MediaType.TEXT_HTML_VALUE )
    public String errorHtml(@PathVariable(name = "id", required = true) int id) {
        return "index";
    }

    /**
     * Handles errors for XHR requests
     */
    @GetMapping(path = "/error/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> error(@PathVariable(name = "id", required = true) int id) {
        final HttpStatus status = HttpStatus.valueOf(id) == null ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.valueOf(id);

        final BasicMessageCode code = BasicMessageCode.fromStatusCode(status);

        final String description = this.messageSource.getMessage(code.key(), null, Locale.getDefault());

        final RestResponse<?> response = RestResponse.failure(code, description);

        return new ResponseEntity<>(response, status);
    }

	@RequestMapping(value = "/login", method = RequestMethod.GET, produces = "text/html")
	public String login(HttpSession session, @RequestParam(required = false) String error) {
		return "redirect:/helpdesk/";
	}

}
