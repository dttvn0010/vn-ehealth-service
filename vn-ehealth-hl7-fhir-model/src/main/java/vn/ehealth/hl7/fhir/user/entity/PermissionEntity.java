package vn.ehealth.hl7.fhir.user.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "authPermission")
@CompoundIndex(def = "{'id':1,'name':1}", name = "index_by_default")
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
