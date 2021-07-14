package eu.opertusmundi.admin.web.config;

import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@EnableJdbcHttpSession(tableName = "admin.spring_session")
public class HttpSessionConfiguration {

}