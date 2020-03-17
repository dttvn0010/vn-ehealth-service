package vn.ehealth.emr.validate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.ehealth.emr.utils.EmrUtils;

public class JsonParser {
    
    Logger logger = LoggerFactory.getLogger(JsonParser.class);
    
    SimpleDateFormat sdf1 = EmrUtils.createSimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat sdf1_1 = EmrUtils.createSimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf2 = EmrUtils.createSimpleDateFormat("dd/MM/yyyy HH:mm");
    SimpleDateFormat sdf2_2 = EmrUtils.createSimpleDateFormat("yyyy-MM-dd HH:mm");
    SimpleDateFormat sdf3 = EmrUtils.createSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdf4 = EmrUtils.createSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    
    ObjectMapper mapper = EmrUtils.createObjectMapper();
    
    Object parseJsonElement(String field, Optional<JsonNode> jsonElement, @Nonnull JsonNode schemaInfo, List<ErrorMessage> errors) {
        
        String type = schemaInfo.get("type").asText();
        var requiredNode = Optional.ofNullable(schemaInfo.get("required"));
        boolean required = false;
        
        if(requiredNode.map(x -> x.isBoolean()).orElse(false)) {
            required = requiredNode.map(x -> x.asBoolean()).orElse(false);
        }
        
        boolean isNull = jsonElement.map(x -> x.isNull()).orElse(true);
        if(isNull) {
            if(required) {
                //errors.add(new ErrorMessage(field, ErrorMessage.Code.MISSING_FIELD, String.format("Field not found : %s", field)));
            }
            return null;
        }
        
        var element = jsonElement.orElseThrow();
        boolean errorType = !element.isValueNode();
        
        switch(type) {
            case DataType.TYPE_INTEGER:
                try {
                    return element.asInt();
                }catch(Exception e) {
                    errorType = true;
                }
                break;
                
            case DataType.TYPE_DOUBLE :
                try {
                    return element.asDouble();
                } catch(Exception e) {
                    errorType = true;
                }
                break;
                
            case DataType.TYPE_BOOLEAN :
                try {
                    return element.asBoolean();
                }catch(Exception e) {
                    errorType = true;
                }
                break;
                
            case DataType.TYPE_STRING :
                try {
                    String st= element.asText();
                    if(required && StringUtils.isEmpty(st)) {
                        //errors.add(new ErrorMessage(field, ErrorMessage.Code.MISSING_FIELD, String.format("Field \"%s\" cannot be blank", field)));
                    }
                    return st;
                }catch(Exception e) {
                    errorType = true;
                }
                break;
                
            case DataType.TYPE_DATE :
                try {
                    String st= element.asText();
                    if(StringUtils.isEmpty(st)) {
                        return null;
                    }
                    
                    if(st.length() == 10) {
                        if(st.contains("/"))
                            return sdf1.parse(st);
                        else
                            return sdf1_1.parse(st);
                    }else if(st.length() == 16) {
                        if(st.contains("/"))
                            return sdf2.parse(st);
                        else
                            return sdf2_2.parse(st);
                    }else if(st.length() == 19) {
                        return sdf3.parse(st);
                    }else {
                        return sdf4.parse(st);
                    }
                }catch(Exception e) {
                    errorType = true;
                }
                
                break;
            
            default:
                throw new RuntimeException("Not allowed type : " + type);
        }
        
        if(errorType) {
            errors.add(new ErrorMessage(field, ErrorMessage.Code.WRONG_DATA_TYPE, 
                            String.format("Wrong data type for field \"%s\" : expected data type %s", field, type)));
        }
        
        return null;
    }
    
    List<Object> parseJsonArray(String field, Optional<JsonNode> jsonArr, @Nonnull JsonNode elementSchemaInfo, List<ErrorMessage> errors) {
        final List<Object> result = new ArrayList<>();
        
        jsonArr.ifPresent(arr -> {
            for(int i = 0; i < arr.size(); i++) {
                var fieldElement = String.format("%s[%d]", field, i);
                var element = Optional.ofNullable(arr.get(i));
                
                if(elementSchemaInfo.get("type") != null) {
                    var obj = parseJsonElement(fieldElement, element, elementSchemaInfo, errors);
                    if(obj != null) {
                        result.add(obj);
                    }
                }else {
                    var obj = parseJsonNode(fieldElement, element, elementSchemaInfo, errors);
                    result.add(obj);
                }
            }
        });
        
        return result;
    }
    
    Map<String, Object> parseJsonNode(String parentField, Optional<JsonNode> jsonNode, @Nonnull JsonNode schemaJsonNode, List<ErrorMessage> errors) {
        var objMap = new HashMap<String, Object>();
        var keys = new HashSet<String>();
        schemaJsonNode.fieldNames().forEachRemaining(keys::add);
        
        var objKeys = new ArrayList<String>();
        
        jsonNode.ifPresent(x -> {
            x.fieldNames().forEachRemaining(objKeys::add);
        });
        
        for(var objKey : objKeys) {
            String field = !StringUtils.isEmpty(parentField)? parentField + "." + objKey : objKey;;
            if(!keys.contains(objKey)) {
                //errors.add(new ErrorMessage(field, ErrorMessage.Code.NOT_ALLOW_FIELD, 
                //                String.format("Not allowed fields : \"%s\"", field)));
            }
        }
        
        for(var key : keys) {
            String field = !StringUtils.isEmpty(parentField)? parentField + "." + key : key;
        
            var schemaInfo = schemaJsonNode.get(key);
            if(schemaInfo == null) {
                throw new RuntimeException("Missing type for field : " + key);
            }
            
            var element = jsonNode.map(x -> x.get(key));
            var typeNode = schemaInfo.get("type");
            
            if(typeNode != null) {
                var type = typeNode.asText("");
                
                if(!DataType.TYPE_ARRAY.equals(type)) {
                    var obj = parseJsonElement(field, element, schemaInfo, errors);
                    if(obj != null) {
                        objMap.put(key,  obj);
                    }                    
                }else {
                    var isArray = element.map(x -> x.isArray()).orElse(true);
                    
                    if(!isArray) {
                        errors.add(new ErrorMessage(field, ErrorMessage.Code.WRONG_DATA_TYPE, 
                                String.format("Wrong data type for field \"%s\" : expected data type JsonArray", field)));
                    }
                    
                    var elementSchemaInfo = schemaInfo.get("element");                    
                    if(elementSchemaInfo != null) {
                        var arr = parseJsonArray(field, element, elementSchemaInfo, errors);
                        objMap.put(key, arr);
                    }
                }
            }else {
                var obj = parseJsonNode(field, element, schemaInfo, errors);
                objMap.put(key, obj);
            }
        }
        return objMap;
    }
    
    public Map<String, Object> parseJson(String jsonSt, String jsonSchema, List<ErrorMessage> errors) {
        JsonNode jsonNode = null;
        try {
            jsonNode = mapper.readTree(jsonSt);
        }catch(Exception e) {
            logger.error("Cannot parse json object", e);
            errors.add(new ErrorMessage("", ErrorMessage.Code.INVALID_JSON, "Invalid json content"));
        }
        
        JsonNode schemaJsonNode = null;
        try {
            schemaJsonNode = mapper.readTree(jsonSchema);
        }catch(Exception e) {
            throw new RuntimeException("Cannot parse json schema", e);
        }
        
        if(jsonNode != null && schemaJsonNode != null) {
            return parseJsonNode("", Optional.of(jsonNode), schemaJsonNode, errors);
        }
        
        return new HashMap<String, Object>();
    
    }
    
    @SuppressWarnings("unchecked")
	public Map<String, Object> parseJson(String jsonSt) throws JsonParseException, JsonMappingException, IOException {
    	mapper.setDateFormat(sdf3);
    	return mapper.readValue(jsonSt, Map.class);
    }
}
