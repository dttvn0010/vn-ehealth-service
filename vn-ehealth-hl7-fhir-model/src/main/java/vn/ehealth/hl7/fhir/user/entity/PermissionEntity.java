package vn.ehealth.hl7.fhir.user.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;




@Document(collection = "authPermission")
public class PermissionEntity {

    @Id public String id;
    public String name;    
    
    public PermissionEntity() {}
    
    public PermissionEntity(String name) {
        this.name = name;
    }
    
    public static class Values {
        public final static String PATIENT_VIEW = "PATIENT_VIEW";
        public final static String PATIENT_LIST = "PATIENT_LIST";
        public final static String PATIENT_ADD = "PATIENT_ADD";
        public final static String PATIENT_DELETE = "PATIENT_DELETE";
    }
}
