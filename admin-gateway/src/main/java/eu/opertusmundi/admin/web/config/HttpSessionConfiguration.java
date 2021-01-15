package eu.opertusmundi.admin.web.config;

import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@EnableJdbcHttpSession(tableName = "web.spring_session")
public class HttpSessionConfiguration {

}