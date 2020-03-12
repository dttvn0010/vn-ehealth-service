package vn.ehealth.hl7.fhir.core.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Address.AddressType;
import org.hl7.fhir.r4.model.Address.AddressUse;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
public class BaseAddress {
    public String addressLine1;
    public String addressLine2;
    public String addressLine3;
    public String ward;
    public String district;
    public String city;
    public String state;
    public String country;
    public String addressUse;
    public String addressType;
    public String postalCode;
    public String text;
    
    public List<BaseExtension> extension;
    public BasePeriod period;
    
    public static BaseAddress fromAddress(Address obj) {
        
        if(obj == null) return null;
        
        var ent = new BaseAddress();
        if (obj.hasLine()) {
            if (obj.getLine().get(0) != null && !obj.getLine().get(0).isEmpty()) {
                ent.addressLine1 = obj.getLine().get(0).getValue();
            }

            if (obj.getLine().size() > 1 && obj.getLine().get(1) != null && !obj.getLine().get(1).isEmpty()) {
                ent.addressLine2 = obj.getLine().get(1).getValue();
            }
  
            if (obj.getLine().size() > 2 && obj.getLine().get(2) != null && !obj.getLine().get(2).isEmpty()) {
                ent.addressLine3 = obj.getLine().get(2).getValue();
            }
        }
        
        ent.district = obj.hasDistrict()? obj.getDistrict() : null;
        ent.city = obj.hasCity()? obj.getCity() : null;
        ent.state = obj.hasState()? obj.getState() : null;
        ent.country = obj.hasCountry()? obj.getCountry() : null;
        ent.addressUse = obj.hasUse()? obj.getUse().toCode() : null;
        ent.addressType = obj.hasType()? obj.getType().toCode() : null;
        ent.postalCode = obj.hasPostalCode()? obj.getPostalCode() : null;
        ent.text = obj.hasText()? obj.getText() : null;
        ent.extension = obj.hasExtension()? transform(obj.getExtension(), BaseExtension::fromExtension) : null;
        ent.period = obj.hasPeriod()? BasePeriod.fromPeriod(obj.getPeriod()) : null;
        return ent;
    }
    
    public static List<BaseAddress> fromAddressList(List<Address> lst) {
        return transform(lst, x -> fromAddress(x));
    }
    
    public static Address toAddress(BaseAddress entity) {
        if(entity == null) {
            return null;
        }
        
        var object = new Address();
        List<StringType> lines = new ArrayList<>();

        if (!StringUtils.isBlank(entity.addressLine1)) {
            lines.add(new StringType(entity.addressLine1));
        }
        
        if (!StringUtils.isBlank(entity.addressLine2)) {
            lines.add(new StringType(entity.addressLine2));
        }
        
        if (!StringUtils.isBlank(entity.addressLine3)) {
            lines.add(new StringType(entity.addressLine3));
        }
        object.setLine(lines);
        object.setDistrict(entity.district);
        object.setCity(entity.city);
        object.setState(entity.state);
        object.setCountry(entity.country);
        object.setUse(AddressUse.fromCode(entity.addressUse));
        object.setType(AddressType.fromCode(entity.addressType));
        object.setPostalCode(entity.postalCode);
        object.setText(entity.text);
        object.setExtension(transform(entity.extension, BaseExtension::toExtension));
        object.setPeriod(BasePeriod.toPeriod(entity.period));
      
        return object;
    }
    
    public static List<Address> toAddressList(List<BaseAddress> entityList) {
        return transform(entityList, x -> toAddress(x));
    }
}
