package vn.ehealth.hl7.fhir.ehr.entity;

import java.util.Optional;

import org.hl7.fhir.r4.model.EpisodeOfCare.EpisodeOfCareStatus;
import org.hl7.fhir.r4.model.EpisodeOfCare.EpisodeOfCareStatusHistoryComponent;

import vn.ehealth.hl7.fhir.core.entity.BasePeriod;



public class EOCStatusHistoryEntity {

    public String status;
    public BasePeriod period;
    
    public static EOCStatusHistoryEntity fromEpisodeOfCareStatusHistoryComponent(EpisodeOfCareStatusHistoryComponent obj) {
        if(obj == null) return null;
        
        var ent = new EOCStatusHistoryEntity();
        
        ent.status = Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        ent.period = BasePeriod.fromPeriod(obj.getPeriod());
        
        return ent;
    }
    
    
    public static EpisodeOfCareStatusHistoryComponent toEpisodeOfCareStatusHistoryComponent(EOCStatusHistoryEntity ent) {
        if(ent == null) return null;
        
        var obj = new EpisodeOfCareStatusHistoryComponent();
        
        obj.setStatus(EpisodeOfCareStatus.fromCode(ent.status));
        obj.setPeriod(BasePeriod.toPeriod(ent.period));
        
        return obj;
    }

}
