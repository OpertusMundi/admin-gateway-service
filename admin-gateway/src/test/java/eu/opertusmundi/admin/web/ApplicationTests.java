package eu.opertusmundi.admin.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import eu.opertusmundi.admin.test.support.BaseIntegrationTest;
import eu.opertusmundi.admin.web.controller.HomeController;


@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT
)
public class ApplicationTests extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private HomeController controller;

	@Test
	public void contextLoads() {
		assertThat(this.controller).isNotNull();
	}

}
