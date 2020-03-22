package vn.ehealth.emr.model.dto;

import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResourceType;

public class CoSoKhamBenh extends BaseModelDTO {
    
    public String ma;
    public String ten;
    
    public CoSoKhamBenh() {
        super();
    }
    
    public CoSoKhamBenh(Organization obj) {
        super(obj);
        if(obj == null) return;        
        this.ma = obj.hasIdentifier()? obj.getIdentifierFirstRep().getValue() : "";
        this.ten = obj.hasName() ? obj.getName() : "";
    }
    
    public static CoSoKhamBenh fromFhir(Organization obj) {
        if(obj == null) return null;
        return new CoSoKhamBenh(obj);        
    }
    
    public static CoSoKhamBenh fromReference(Reference ref) {
        if(ref != null && ref.hasReference()) {
            var obj = DaoFactory.getOrganizationDao().read(createIdType(ref));
            return fromFhir(obj);
        }
        return null;        
    }
    
    public static Organization toFhir(CoSoKhamBenh dto) {
        if(dto == null) return null;
        var obj = new Organization();
        obj.setId(dto.id);
        obj.setIdentifier(listOf(createIdentifier(dto.ma, CodeSystemValue.CO_SO_KHAM_BENH)));
        obj.setName(dto.ten);
        return obj;
    }

    @Override
    public ResourceType getType() {
        return ResourceType.Organization;
    }
}
