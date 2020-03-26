package vn.ehealth.emr.model;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ca.uhn.fhir.rest.param.TokenParam;
import vn.ehealth.emr.model.dto.CanboYte;
import vn.ehealth.emr.utils.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_can_bo_yte")
public class EmrCanboYte {
    @Id public ObjectId id;
    
    public String ten;
    public String chungchihanhnghe;
    public ObjectId emrPersonId;
    
    @JsonIgnore
    public String getFhirId() {
        var params = mapOf("identifier", new TokenParam(IdentifierSystem.CHUNG_CHI_HANH_NGHE, this.chungchihanhnghe));
        var fhirObj = (Practitioner) DaoFactory.getPractitionerDao().search(params);
        if(fhirObj == null) {
            var dto = toDto();
            fhirObj = CanboYte.toFhir(dto);
            fhirObj = DaoFactory.getPractitionerDao().create(fhirObj);
        }
        return fhirObj.getId();
    }
    
    private CanboYte _toDto() {
        var dto = new CanboYte();
        dto.chungChiHanhNghe = this.chungchihanhnghe;
        dto.ten = this.ten;
        return dto;
    }
    
    public CanboYte toDto() {
        var dto = _toDto();        
        dto.id = getFhirId();
        return dto;
    }
}
