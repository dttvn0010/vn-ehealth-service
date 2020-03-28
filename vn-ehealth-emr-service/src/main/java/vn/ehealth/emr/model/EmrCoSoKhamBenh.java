package vn.ehealth.emr.model;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Organization;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ca.uhn.fhir.rest.param.TokenParam;
import vn.ehealth.emr.utils.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;

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
    
    public static Organization getOrganization(String maCskb) {
        var params = mapOf("identifier", new TokenParam(IdentifierSystem.CO_SO_KHAM_BENH, maCskb));
        return (Organization) DaoFactory.getOrganizationDao().searchOne(params);
    }
}
