package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Coding;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

public class BaseCoding {

    public String system;
    public String version;
    public String code;
    public String display;
    public Boolean userSelected;
    
    public static BaseCoding fromCoding(Coding obj) {        
        if(obj == null) return null;
        
        var ent = new  BaseCoding();
        ent.system = obj.hasSystem()? obj.getSystem() : null;
        ent.version = obj.hasVersion()? obj.getVersion() : null;
        ent.code = obj.hasCode()? obj.getCode() : null;
        ent.display = obj.hasDisplay()? obj.getDisplay(): null;
        ent.userSelected = obj.hasUserSelected()? obj.getUserSelected() : null;
        return ent;
    }
    
    public static List<BaseCoding> fromCodingList(List<Coding> lst) { 
        return DataConvertUtil.transform(lst, x -> fromCoding(x));
    }
    
    public static Coding toCoding(BaseCoding ent) {
        if(ent == null) return null;
        var obj = new Coding();
        obj.setSystem(ent.system);
        obj.setVersion(ent.version);
        obj.setCode(ent.code);
        obj.setDisplay(ent.display);
        if(ent.userSelected != null) obj.setUserSelected(ent.userSelected);
        return obj;
    }
    
    
    public static List<Coding> toCodingList(List<BaseCoding> entLst) {
        return DataConvertUtil.transform(entLst, x -> toCoding(x));
    }
}
