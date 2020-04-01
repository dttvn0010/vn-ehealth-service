package vn.ehealth.cdr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories("vn.ehealth")
@EntityScan("vn.ehealth")
@ComponentScan("vn.ehealth")
public class CDRApplication {

    @Autowired
    ApplicationContext context;
    
	public static void main(String[] args) {
		SpringApplication.run(CDRApplication.class, args);
	}
}