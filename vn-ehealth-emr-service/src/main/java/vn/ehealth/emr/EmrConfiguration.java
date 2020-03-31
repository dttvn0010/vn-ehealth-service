package vn.ehealth.emr;

import java.text.SimpleDateFormat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import vn.ehealth.emr.utils.DateUtil;

@Configuration
public class EmrConfiguration {

	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**");
            }            
        };
    }
	
	
	@Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        var sdf = new SimpleDateFormat(DateUtil.FORMAT_YYYY_MM_DD_HH_MM_SS);
        mapper.setDateFormat(sdf);
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }
	
}
