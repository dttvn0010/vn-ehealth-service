package vn.ehealth.emr.model.dto;

import java.util.Date;

import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.ResourceType;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.emr.utils.MessageUtils;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.EncounterType;

import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

public class VaoKhoa extends BaseModelDTO {
	public BaseRef hsbaEncounter;
    public BaseRef patient;
    public BaseRef falcultyOrganization;
        
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioVao;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioKetThucDieuTri;
    
    public BaseRef bacSiDieuTri;
    
    public VaoKhoa() {
        super();
    }
    
    public VaoKhoa(Encounter obj) {
        super(obj);
        if(obj == null) return;
        
        this.hsbaEncounter = new BaseRef(obj.getPartOf());
        this.hsbaEncounter.data = DotKhamBenh.fromFhir((Encounter) this.hsbaEncounter.resource);
        
        this.patient = new BaseRef(obj.getSubject());
        this.patient.data = BenhNhan.fromFhir((Patient) this.patient.resource);
        
        this.falcultyOrganization = new BaseRef(obj.getServiceProvider());
        this.falcultyOrganization.data = KhoaDieuTri.fromFhir((Organization) this.falcultyOrganization.resource);

        this.ngayGioVao = obj.hasPeriod()? obj.getPeriod().getStart() : null;
        this.ngayGioKetThucDieuTri = obj.hasPeriod()? obj.getPeriod().getEnd() : null;
        
        if(obj.hasParticipant()) {
        	var participant = obj.getParticipantFirstRep();
        	if(participant.hasIndividual()) {
        		this.bacSiDieuTri = new BaseRef(participant.getIndividual());
        		this.bacSiDieuTri.data = CanboYte.fromFhir((Practitioner) this.bacSiDieuTri.resource);
        	}
        }
    }
    
    public static VaoKhoa fromFhir(Encounter obj) {
        if(obj == null) return null;
        return new VaoKhoa(obj);
    }
    
    public static Encounter toFhir(VaoKhoa dto) {
        if(dto == null) return null;
        var obj = new Encounter();
        obj.setSubject(BaseRef.toPatientRef(dto.patient));
        obj.setPartOf(BaseRef.toEncounterRef(dto.hsbaEncounter));
        obj.setServiceProvider(BaseRef.toOrganizationRef(dto.falcultyOrganization));
        
        obj.setPeriod(createPeriod(dto.ngayGioVao, dto.ngayGioKetThucDieuTri));
        
        if(dto.bacSiDieuTri != null) {
        	var participant = obj.addParticipant();
        	participant.setIndividual(BaseRef.toPractitionerRef(dto.bacSiDieuTri));        	
        }
        
        var encType = createCodeableConcept(EncounterType.VAO_KHOA, 
			                MessageUtils.get("text.VK"), 
			                CodeSystemValue.ENCOUTER_TYPE);
        
        obj.setType(listOf(encType));
        
        return obj;
    }

    @Override
    public ResourceType getType() {
        return ResourceType.Encounter;
    }
    
}
