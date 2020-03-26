package vn.ehealth.emr.model;

import java.util.Date;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.ResourceType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ca.uhn.fhir.rest.param.TokenParam;
import vn.ehealth.emr.model.dto.BaseRef;
import vn.ehealth.emr.model.dto.VaoKhoa;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@JsonInclude(Include.NON_NULL)
public class EmrVaoKhoa {

    public EmrDmContent emrDmKhoaDieuTri;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaygiovaokhoa;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayketthucdieutri;

    public String tenkhoa;

    public EmrCanboYte bacsidieutri;

    public String phong;

    public String giuong;

    public Integer songaydieutri;

    public String tentruongkhoa;    
    
    public VaoKhoa toDto() {
    	var dto = new VaoKhoa();
    	dto.ngayGioVao = ngaygiovaokhoa;
    	dto.ngayGioKetThucDieuTri = dto.ngayGioKetThucDieuTri;
    	if(this.bacsidieutri != null) {
    	    dto.bacSiDieuTri = bacsidieutri.toRef();
    	}
    	return dto;
    }
    
    public void saveToFhirDb(Encounter encounter) {
    	if(encounter != null) {
    		var dto = toDto();
    		dto.patient = new BaseRef(encounter.getSubject());
    		
    		if(this.emrDmKhoaDieuTri != null && encounter.hasServiceProvider()) {
    			
    			var params = mapOf(
    						"partOf", encounter.getServiceProvider().getReference(),
    						"type", new TokenParam(CodeSystemValue.KHOA_DIEU_TRI, emrDmKhoaDieuTri.ma)
    					);
    			
    			var khoaDieuTri = DaoFactory.getOrganizationDao().searchOne(params);
    			if(khoaDieuTri != null) {
    				dto.falcultyOrganization = new BaseRef(ResourceType.Organization, khoaDieuTri.getId());
    			}
    		}
    		dto.hsbaEncounter = new BaseRef(encounter);
    		DaoFactory.getEncounterDao().create(VaoKhoa.toFhir(dto));
    	}
    }
}
