package br.com.andersonpiotto.letscode.moviesbattle.config;

import org.h2.server.web.WebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/** Classe responsavel pelas configuraçõe de registro da url do banco h2
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 19/03/2022
 */
@Configuration
public class WebConfiguration {
    @Bean
    ServletRegistrationBean h2servletRegistration(){
        ServletRegistrationBean registrationBean = new ServletRegistrationBean( new WebServlet());
        registrationBean.addUrlMappings("/h2/*");
        return registrationBean;
    }
}