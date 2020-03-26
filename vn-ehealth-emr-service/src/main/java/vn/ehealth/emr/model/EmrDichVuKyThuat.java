package vn.ehealth.emr.model;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

import org.hl7.fhir.r4.model.Encounter;
import ca.uhn.fhir.rest.param.TokenParam;
import vn.ehealth.emr.dto.controller.DichVuKyThuatHelper;
import vn.ehealth.emr.model.dto.BaseRef;
import vn.ehealth.emr.model.dto.DichVuKyThuat;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;

import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;
import static vn.ehealth.hl7.fhir.dao.util.DaoFactory.*;

public abstract class EmrDichVuKyThuat {
	
	public EmrDmContent emrDmKhoaDieuTri;
		
	public Encounter getVaoKhoaEncounter(Encounter hsbaEncounter) {
		if(hsbaEncounter == null 
			|| hsbaEncounter.getServiceProvider() == null
			|| !hsbaEncounter.getServiceProvider().hasReference()
			|| emrDmKhoaDieuTri == null) {
			return null;
		}
		
		var params = mapOf(
				"partOf", hsbaEncounter.getServiceProvider().getReference(),
				"type", new TokenParam(CodeSystemValue.KHOA_DIEU_TRI, emrDmKhoaDieuTri.ma)
			);
	
		var khoaDieuTri = getOrganizationDao().searchOne(params);
		if(khoaDieuTri != null) {
			params = mapOf(
					"partOf", createReference(hsbaEncounter),
					"serviceProvider", createReference(khoaDieuTri)
				);
			return (Encounter) getEncounterDao().searchOne(params);
		}
    	return null;
	}
	
	public void saveToFhirDb(Encounter hsbaEncounter) {
    	if(hsbaEncounter == null) return;
    	var vaoKhoaEncounter = getVaoKhoaEncounter(hsbaEncounter);
    	var encounter = vaoKhoaEncounter != null? vaoKhoaEncounter : hsbaEncounter;
    	
    	var dto = toDto();
    	dto.patient = new BaseRef(encounter.getSubject());
    	dto.encounter = new BaseRef(encounter);
    	DichVuKyThuatHelper.saveDichVuKT(dto);
    	
    }
	
	abstract public DichVuKyThuat toDto();
}
