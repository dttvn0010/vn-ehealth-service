package vn.ehealth.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

public class MongoUtils {

    public static String idToString(ObjectId id) {
        return id != null? id.toHexString() : null;
    }
    
    public static ObjectId stringToId(String id) {
        return id != null? new ObjectId(id) : null;
    }
    
    @SuppressWarnings("unchecked")
    public static Criteria createCriteria(Map<String, Object> params) {
        var criteria = new Criteria();
        var orCrList = new ArrayList<Criteria>();
        
        for(var item : params.entrySet()) {
            var key = item.getKey();
            var value = item.getValue();
            
            if("$or".equals(key)) {
                if(!(value instanceof List)) {
                    throw new RuntimeException("$or params must be a list");
                }
                var lstParams = (List<Map<String, Object>>) value;
                var lstCr = DataConvertUtil.transform(lstParams, x -> createCriteria(x));
                var orCr = new Criteria().orOperator(lstCr.toArray(new Criteria[0]));
                orCrList.add(orCr);
                
            }else if(value instanceof Map) {
                for(var opItem : ((Map<String, Object>)value).entrySet()) {
                    String op = opItem.getKey();
                    var opVal = opItem.getValue();
                    switch(op) {
                        
                        case "$regex":
                            criteria.and(key).regex((String) opVal);
                            break;
                            
                        case "$gt":
                            criteria.and(key).gt(opVal);
                            
                        case "$gte":
                            criteria.and(key).gte(opVal);
                            
                        case "$lt":
                            criteria.and(key).lt(opVal);
                        
                        case "$lte":
                            criteria.and(key).lte(opVal);
                            
                        case "$in":
                            criteria.and(key).in((List<Object>)opVal);
                            
                        default:
                            throw new RuntimeException("Unsupported operator : " + op);
                    }
                   
                }
            }else {
                criteria.and(item.getKey()).is(item.getValue());
            }
        }
        if(orCrList.size() > 0) {
            criteria.andOperator(orCrList.toArray(new Criteria[0]));
        }
        return criteria;
    }
}
