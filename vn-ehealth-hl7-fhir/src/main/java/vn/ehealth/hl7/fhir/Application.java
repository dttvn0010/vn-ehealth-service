package vn.ehealth.hl7.fhir;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories("vn.ehealth")
@EntityScan("vn.ehealth")
@ComponentScan("vn.ehealth")
public class Application {

    @Autowired
    private ApplicationContext context;
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new Hl7FhirRestfulServer(context),
                "/R4/*");
        registration.setName("FhirServlet");
        return registration;
    }
}
