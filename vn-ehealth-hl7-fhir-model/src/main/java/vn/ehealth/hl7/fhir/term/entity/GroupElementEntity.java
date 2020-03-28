package vn.ehealth.hl7.fhir.term.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;



import vn.ehealth.hl7.fhir.core.entity.BaseResource;
/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 * */
@Document(collection = "groupElement")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class GroupElementEntity extends BaseResource{
    @Id
    public ObjectId id;
    public String conceptMapID;
    public String source;
    public String sourceVersion;
    public String target;
    public String targetVersion;
    public List<ElementEntity> element;//SourceElementComponent
    public UnMappedEntity unmapped;//ConceptMapGroupUnmappedComponent
}
