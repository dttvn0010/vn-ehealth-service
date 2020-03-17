package vn.ehealth.emr.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_can_bo_yte")
public class EmrCanboYte {
    @Id public ObjectId id;
    
    public String ten;
    public String chungchihanhnghe;
    public ObjectId emrPersonId;
}
