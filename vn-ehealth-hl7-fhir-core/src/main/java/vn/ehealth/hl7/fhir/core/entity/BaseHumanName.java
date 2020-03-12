package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.HumanName.NameUse;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

public class BaseHumanName {
    public String use;
    public String text;
    public String family;
    public List<String> given;
    public List<String> prefix;
    public List<String> suffix;
    public BasePeriod period;

    public static BaseHumanName fromHumanName(HumanName obj) {        
        if(obj == null) return null;
        
        var ent = new BaseHumanName();
        ent.use = obj.hasUse()? obj.getUse().toCode() : null;        
        ent.text = obj.hasText()? obj.getText() : null;
        ent.family = obj.hasFamily()? obj.getFamily() : null;
        ent.given = obj.hasGiven()? transform(obj.getGiven(), x -> x.getValue()) : null;
        ent.prefix = obj.hasPrefix()? transform(obj.getPrefix(), x -> x.getValue()) : null;
        ent.suffix = obj.hasSuffix()? transform(obj.getSuffix(), x -> x.getValue()) : null;
        ent.period = obj.hasPeriod()? BasePeriod.fromPeriod(obj.getPeriod()) : null;
        
        return ent;
    }
    
    public static List<BaseHumanName> fromHumanNameList(List<HumanName> lst) {
        return transform(lst, x -> fromHumanName(x));
        
    }
    
    public static HumanName toHumanName(BaseHumanName entity) {
        if(entity == null) return null;
        
        var object = new HumanName();
        if(entity.use != null) {
            object.setUse(NameUse.fromCode(entity.use.toLowerCase()));
        }
        object.setText(entity.text);
        object.setFamily(entity.family);
        object.setGiven(transform(entity.given, x -> new StringType(x)));
        object.setPrefix(transform(entity.prefix, x -> new StringType(x)));
        object.setSuffix(transform(entity.suffix, x -> new StringType(x)));
        object.setPeriod(BasePeriod.toPeriod(entity.period));
        
        return object;
    }
    
    public static List<HumanName> toHumanNameList(List<BaseHumanName> entityList) {
        return transform(entityList, x -> toHumanName(x));        
    }

}
