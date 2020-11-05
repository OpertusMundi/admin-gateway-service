package eu.opertusmundi.admin.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(
    scanBasePackageClasses = {
        eu.opertusmundi.common.repository._Marker.class,
        eu.opertusmundi.admin.web.config._Marker.class,
        eu.opertusmundi.admin.web.listener._Marker.class,
        eu.opertusmundi.admin.web.repository._Marker.class,
        eu.opertusmundi.admin.web.service._Marker.class,
        eu.opertusmundi.admin.web.validation._Marker.class,
        eu.opertusmundi.admin.web.controller._Marker.class,
    }
)
@EntityScan(
    basePackageClasses = {
        eu.opertusmundi.common.domain._Marker.class,
        eu.opertusmundi.admin.web.domain._Marker.class,
    }
)
public class Application extends SpringBootServletInitializer  {

    /**
     * Used when packaging as a WAR application
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder)
    {
        return builder.sources(Application.class);
    }

    /**
     * Used when packaging as a standalone JAR (the server is embedded)
     */
	public static void main(String[] args)
	{
		SpringApplication.run(Application.class, args);
	}
}
