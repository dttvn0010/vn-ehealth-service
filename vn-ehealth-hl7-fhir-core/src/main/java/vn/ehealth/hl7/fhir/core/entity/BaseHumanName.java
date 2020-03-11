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

    public static BaseHumanName fromHumanName(HumanName object) {
        if(object == null) return null;
        
        var entity = new BaseHumanName();
        
        if (object.hasUse()) {
            entity.use = object.getUse().toCode();
        }
        
        entity.text = object.getText();
        entity.family = object.getFamily();
        entity.given = transform(object.getGiven(), x -> x.getValue());
        entity.prefix = transform(object.getPrefix(), x -> x.getValue());
        entity.suffix = transform(object.getSuffix(), x -> x.getValue());
        entity.period = BasePeriod.fromPeriod(object.getPeriod());
        
        return entity;
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
