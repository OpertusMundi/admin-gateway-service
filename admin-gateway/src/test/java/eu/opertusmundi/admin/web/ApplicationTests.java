package eu.opertusmundi.admin.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import eu.opertusmundi.admin.web.controller.HomeController;


@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("testing")
@AutoConfigureMockMvc
public class ApplicationTests
{

    @LocalServerPort
    private int port;

    @Autowired
    private HomeController controller;

	@Test
	public void contextLoads() {
		assertThat(this.controller).isNotNull();
	}

}
