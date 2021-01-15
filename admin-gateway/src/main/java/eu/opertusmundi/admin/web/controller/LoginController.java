package eu.opertusmundi.admin.web.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;

import eu.opertusmundi.common.model.RestResponse;

@RestController
@RequestMapping(produces = "application/json")
public class LoginController {

	private static class Token {
		private final CsrfToken token;

		public Token(CsrfToken token) {
			this.token = token;
		}

		@JsonProperty("csrfToken")
		public String getToken() {
			return this.token.getToken();
		}
	}

	@RequestMapping(value = "/logged-in", method = RequestMethod.GET)
	public RestResponse<Token> loggedIn(CsrfToken token) {

		return RestResponse.result(new Token(token));
	}

	@RequestMapping(value = "/logged-out", method = RequestMethod.GET)
	public RestResponse<Token> loggedOut(CsrfToken token) {

		return RestResponse.result(new Token(token));
	}

}
