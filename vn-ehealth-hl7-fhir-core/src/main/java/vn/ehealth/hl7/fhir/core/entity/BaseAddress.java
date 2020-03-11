package vn.ehealth.hl7.fhir.core.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Extension;
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
    public List<Extension> extension;
    public BasePeriod period;
    
    public static BaseAddress fromAddress(Address object) {
        if(object == null) {
            return null;
        }
        
        var entity = new BaseAddress();
        if (object.hasLine()) {
            if (object.getLine().get(0) != null && !object.getLine().get(0).isEmpty()) {
                entity.addressLine1 = object.getLine().get(0).getValue();
            }

            if (object.getLine().size() > 1 && object.getLine().get(1) != null && !object.getLine().get(1).isEmpty()) {
                entity.addressLine2 = object.getLine().get(1).getValue();
            }
  
            if (object.getLine().size() > 2 && object.getLine().get(2) != null && !object.getLine().get(2).isEmpty()) {
                entity.addressLine3 = object.getLine().get(2).getValue();
            }
        }
        
        entity.district = object.getDistrict();
        entity.city = object.getCity();
        entity.state = object.getState();
        entity.country = object.getCountry();
        entity.addressUse = object.getUse().toCode();
        entity.addressType = object.getType().toCode();
        entity.postalCode = object.getPostalCode();
        entity.text = object.getText();
        entity.extension = object.getExtension();
        entity.period = BasePeriod.fromPeriod(object.getPeriod());
        return entity;
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
        object.setExtension(entity.extension);
        object.setPeriod(BasePeriod.toPeriod(entity.period));
      
        return object;
    }
    
    public static List<Address> toAddressList(List<BaseAddress> entityList) {
        return transform(entityList, x -> toAddress(x));
    }
}
