package vn.ehealth.emr.model.dto;

import java.util.Map;

import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ResourceType;

public abstract class DichVuKyThuat extends BaseModelDTO {
    public BaseRef patient;
    public BaseRef encounter;
    public BaseRef falcultyOrganization;
    
    public abstract Map<String, Object> toFhir();
    protected abstract void fromFhir(Procedure procedure, boolean includeSpecimen, boolean includeObservation);
    
    public DichVuKyThuat() {
        super();
    }
    
    public DichVuKyThuat(Procedure procedure, boolean includeSpecimen, boolean includeObservation) {
        super(procedure);
        if(procedure != null) {
            this.patient = new BaseRef(procedure.getSubject());
            this.patient.data = BenhNhan.fromFhir((Patient) this.patient.resource);
            
            this.encounter = new BaseRef(procedure.getEncounter());
            this.encounter.data = VaoKhoa.fromFhir((Encounter) this.encounter.resource);
            
            if(procedure.getEncounter().getResource() != null) {
            	var enc = (Encounter) procedure.getEncounter().getResource();
            	this.falcultyOrganization = new BaseRef(enc.getServiceProvider());
            }
            
            fromFhir(procedure, includeSpecimen, includeObservation);
        }
    }
    
    @Override
    public ResourceType getType() {
        return ResourceType.Procedure;
    }
}
