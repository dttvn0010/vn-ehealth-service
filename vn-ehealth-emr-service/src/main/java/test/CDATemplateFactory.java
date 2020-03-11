package test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.openhealthtools.mdht.uml.hl7.datatypes.DatatypesFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.ehealth.emr.utils.EmrUtils;

public class CDATemplateFactory {

    private CDATemplate cdaTemplate;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object createObject(String dataTypeId) throws ReflectiveOperationException {
        var dataTypeOpt = cdaTemplate.dataTypes.stream().filter(x -> dataTypeId.equals(x.id)).findFirst();
        if(dataTypeOpt.isPresent()) {
            var dataType = dataTypeOpt.get();
            var obj = CDAUtil.createObject(dataType.parent);
            
            var method = CDAUtil.findMethod(obj.getClass(), "getTemplateIds").orElse(null);
            if(method != null) {
                var templateIds = (List) method.invoke(obj);
                templateIds.add(DatatypesFactory.eINSTANCE.createII(dataType.templateId));
            }
            
            for(var fieldDefault : dataType.fieldDefaults) {
                CDAUtil.setProperty(obj, fieldDefault.field, fieldDefault.value);
            }
            return obj;
        }
        return null;
    }
    
    public CDATemplateFactory(InputStream is) throws IOException {
        ObjectMapper mapper = EmrUtils.createObjectMapper();
        var jsonSt = new String(is.readAllBytes());
        is.close();        
        cdaTemplate =  mapper.readValue(jsonSt, CDATemplate.class);
    }
}
