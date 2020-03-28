package vn.ehealth.emr.model.dto;

import vn.ehealth.emr.utils.MessageUtils;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.emr.utils.Constants.IdentifierSystem;
import vn.ehealth.emr.utils.Constants.LoaiToChuc;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

import org.hl7.fhir.r4.model.Organization;

public class CoSoKhamBenh extends ToChuc {
    
    public CoSoKhamBenh() {
        super();
    }
    
    public CoSoKhamBenh(Organization obj) {
        super(obj);
    }
    
    public void getContentFromFhir(Organization obj) {
    }

    public Organization toFhir() {
        var obj = new Organization();
        obj.setId(this.id);
        obj.setIdentifier(listOf(createIdentifier(this.ma, IdentifierSystem.CO_SO_KHAM_BENH)));
        obj.setName(this.ten);
        
        var orgType = createCodeableConcept(LoaiToChuc.CO_SO_KHAM_BENH, 
                                            MessageUtils.get("text.HOS"), 
                                            CodeSystemValue.LOAI_TO_CHUC);
        
        obj.setType(listOf(orgType));
        
        return obj;
    }
    
    public static CoSoKhamBenh fromFhir(Organization obj) {
    	if(obj == null) return null;
    	return new CoSoKhamBenh(obj);
    }
}
