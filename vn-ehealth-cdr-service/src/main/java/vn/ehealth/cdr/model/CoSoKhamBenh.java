package vn.ehealth.cdr.model;

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
@Document(collection = "co_so_kham_benh")
public class CoSoKhamBenh {

    @Id    
    public ObjectId id;
    
    public String getId() { return id != null? id.toHexString() : null; }
    
    public int trangThai;
    public DanhMuc dmPhuongXa;
    public DanhMuc dmQuanHuyen;
    public DanhMuc dmTinhThanh;
    public DanhMuc dmCoSoKhamBenh;
    
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
        var params = mapOf(
                        "identifier.system", (Object) IdentifierSystem.CO_SO_KHAM_BENH,
                        "identifier.value", ma
                    );
        
        var query = MongoUtils.createQuery(params);
        return DaoFactory.getOrganizationDao().getResource(query);
    }
    
    @JsonIgnore
    public Organization toFhir() {
        var obj = new Organization();
        obj.setIdentifier(listOf(createIdentifier(ma, IdentifierSystem.CO_SO_KHAM_BENH)));
        obj.setName(ten);
        return obj;
    }
}
