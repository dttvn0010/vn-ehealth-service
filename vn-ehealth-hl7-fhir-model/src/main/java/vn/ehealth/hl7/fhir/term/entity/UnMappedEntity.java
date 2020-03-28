package vn.ehealth.hl7.fhir.term.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;


/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
public class UnMappedEntity {
    @Id
    public ObjectId id;
    public String groupElementEntityID;
    public String mode;
    public String code;
    public String display;
    public String url;
}
