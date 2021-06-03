package eu.opertusmundi.admin.web.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import eu.opertusmundi.common.model.BasicMessageCode;
import eu.opertusmundi.common.model.RestResponse;

@Controller
public class HomeController {

    @Autowired
    private MessageSource messageSource;

    /**
     * Default request handler
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * Request handler for client URLs
     */
    @GetMapping("/{name:(?!action).*}")
    public String clientPage() {
        return "index";
    }

    /**
     * Handles HTML error pages
     */
    @GetMapping(path = "/error/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public String errorHtml(@PathVariable(name = "id", required = true) int id) {
        return "index";
    }

    /**
     * Handles errors for XHR requests
     */
    @GetMapping(path = "/error/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> error(@PathVariable(name = "id", required = true) int id) {
        final HttpStatus       status      = HttpStatus.valueOf(id) == null ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.valueOf(id);
        final BasicMessageCode code        = BasicMessageCode.fromStatusCode(status);
        final String           description = this.messageSource.getMessage(code.key(), null, Locale.getDefault());
        final RestResponse<?>  response    = RestResponse.failure(code, description);

        return new ResponseEntity<>(response, status);
    }
    
}
