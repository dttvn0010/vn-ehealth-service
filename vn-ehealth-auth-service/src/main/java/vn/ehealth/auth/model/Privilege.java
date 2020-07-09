package vn.ehealth.auth.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Document(collection = "privilege")
public class Privilege {
	final public static String XEM_TAB_HSBA = "XEM_TAB_HSBA";
	final public static String XEM_TAB_PTTT = "XEM_TAB_PTTT";
	final public static String XEM_TAB_XN = "XEM_TAB_XN";
	final public static String XEM_TAB_CDHA = "XEM_TAB_CDHA";
	final public static String XEM_TAB_TDCN = "XEM_TAB_TDCN";
	final public static String XEM_TAB_GPB = "XEM_TAB_GPB";
	final public static String XEM_TAB_YL = "XEM_TAB_YL";
	final public static String XEM_TAB_DT = "XEM_TAB_DT";
	final public static String XEM_TAB_CS = "XEM_TAB_CS";
	final public static String XEM_TAB_TDCNS = "XEM_TAB_TDCNS";
	final public static String XEM_TAB_DONTHUOC = "XEM_TAB_DONTHUOC";
	final public static String XEM_TAB_GIAYTOKHAC = "XEM_TAB_GIAYTOKHAC";
	
	
    @Id public ObjectId id;
    
    public String code;
    public String name;
    public String description;
    
    public String getId() {
        return id != null? id.toHexString() : null;
    }
    
    public void setId(String id) {
        if(id != null) {
            this.id = new ObjectId(id);
        }
    }
}
