package vn.ehealth.hl7.fhir;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.to.FhirTesterMvcConfig;
import ca.uhn.fhir.to.TesterConfig;

@Configuration
@Import(FhirTesterMvcConfig.class)
public class FhirUITesterConfig {

	/**
	    * This bean tells the testing webpage which servers it should configure itself
	    * to communicate with. In this example we configure it to talk to the local
	    * server, as well as one public server. If you are creating a project to 
	    * deploy somewhere else, you might choose to only put your own server's 
	    * address here.
	    * 
	    * Note the use of the ${serverBase} variable below. This will be replaced with
	    * the base URL as reported by the server itself. Often for a simple Tomcat
	    * (or other container) installation, this will end up being something
	    * like "http://localhost:8080/hapi-fhir-jpaserver-example". If you are
	    * deploying your server to a place with a fully qualified domain name, 
	    * you might want to use that instead of using the variable.
	    */
	   @Bean
	   public TesterConfig testerConfig() {
	      TesterConfig retVal = new TesterConfig();
	      retVal
	         .addServer()
	            .withId("home")
	            .withFhirVersion(FhirVersionEnum.R4)
	            .withBaseUrl("${serverBase}/R4")
	            .withName("VEIG Test Server")
	         .addServer()
	            .withId("hapi")
	            .withFhirVersion(FhirVersionEnum.R4)
	            .withBaseUrl("http://hapi.fhir.org/baseR4")
	            .withName("Public HAPI Test Server");
	      
	      /*
	       * Use the method below to supply a client "factory" which can be used 
	       * if your server requires authentication
	       */
	      // retVal.setClientFactory(clientFactory);
	      
	      return retVal;
	   }
}
