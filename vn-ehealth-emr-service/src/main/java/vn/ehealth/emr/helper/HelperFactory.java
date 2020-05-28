package vn.ehealth.emr.helper;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import vn.ehealth.emr.helper.diagnostic.ObservationHelper;

@Component
public class HelperFactory implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private static ObservationHelper observationHelper;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        HelperFactory.applicationContext = applicationContext;        
    }
    
    public static ObservationHelper getObservationHelper() {
        if(observationHelper == null) {
            observationHelper = applicationContext.getBean(ObservationHelper.class);
        }
        
        return observationHelper;
    }

}
