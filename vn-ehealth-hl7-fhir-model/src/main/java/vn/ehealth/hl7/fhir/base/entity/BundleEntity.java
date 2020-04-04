package vn.ehealth.hl7.fhir.base.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseSignature;

@Document(collection = "bundle")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class BundleEntity extends BaseResource {

	@Id
	@Indexed(name = "_id_")
	@JsonIgnore public ObjectId id;
    public BaseIdentifier identifier;
    public String type;
    public Date timestamp;
    public Integer total;    
    public BaseSignature signature;
    public List<BundleLink> link;
    public List<BundleEntry> entry;
    
    public static class BundleLink {
    	public String relation;
    	public String url;
    }
    
    public static class BundleEntry {
    	public List<BundleLink> link;
    	public String fullUrl;
    	public IBaseResource resource;
    	
    	public static class BundleEntrySearch {
    		public String mode;
    		public BigDecimal score;
    	}
    	
    	public static class BundleEntryRequest {
    		public String method;
    		public String url;
    		public String ifNoneMatch;
    		public Date ifModifiedSince;
    		public String ifMatch;
    		public String ifNoneExist;
    		
    	}
    	
    	public static class BundleEntryResponse {
    		public String status;
    		public String location;
    		public String etag;
    		public String lastModified;
    		public IBaseResource outcome;    		
    	}
    }
	
}
