package vn.ehealth.utils;

import java.util.Map;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MongoUtils {
	
	private static Logger log = LoggerFactory.getLogger(MongoUtils.class);

    public static String idToString(ObjectId id) {
        return id != null? id.toHexString() : null;
    }
    
    public static ObjectId stringToId(String id) {
        return id != null? new ObjectId(id) : null;
    }
    
    public static Query createQuery(Map<String, Object> params) {
    	var objectMapper = new ObjectMapper();
    	try {
			String json = objectMapper.writeValueAsString(params);
			return new BasicQuery(json);
		} catch (JsonProcessingException e) {
			log.error("Json error:", e);
		}
    	return null;
    }
}
