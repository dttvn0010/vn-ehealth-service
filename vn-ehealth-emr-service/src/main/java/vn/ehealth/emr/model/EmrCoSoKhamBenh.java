package vn.ehealth.emr.model;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Organization;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;
import vn.ehealth.utils.MongoUtils;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createIdentifier;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_co_so_kham_benh")
public class EmrCoSoKhamBenh {

    @Id    
    public ObjectId id;
    
    public String getId() { return id != null? id.toHexString() : null; }
    
    public int trangThai;
    public EmrDmContent emrDmPhuongXa;
    public EmrDmContent emrDmQuanHuyen;
    public EmrDmContent emrDmTinhThanh;
    public EmrDmContent emrDmCoSoKhamBenh;
    
    public String ma;
    
    public String ten;
    
    public Short tuyen;
    
    public String diachi;
    
    public String donvichuquan;
    
    public String giamdoc;
    
    public String dienthoai;
    
    public String truongphongth;
    
    @JsonIgnore
    public Organization getOrganizationInDB() {
        var params = mapOf("active", true,
                        "identifier.system", IdentifierSystem.CO_SO_KHAM_BENH,
                        "identifier.value", ma
                    );
        
        var criteria = MongoUtils.createCriteria(params);
        var lst = DaoFactory.getOrganizationDao().findByCriteria(criteria);
        return lst.size() > 0 ? lst.get(0) : null;
    }
    
    @JsonIgnore
    public Organization toFhir() {
        var obj = new Organization();
        obj.setIdentifier(listOf(createIdentifier(ma, IdentifierSystem.CO_SO_KHAM_BENH)));
        obj.setName(ten);
        return obj;
    }
}
