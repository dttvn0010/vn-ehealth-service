package vn.ehealth.hl7.fhir.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.InstantType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringParam;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;

public abstract class BaseDao<ENT extends BaseResource, FHIR extends DomainResource> {
    @Autowired
    protected MongoOperations mongo;
    
    abstract protected String getProfile();
    abstract protected ENT fromFhir(FHIR obj);
    abstract protected FHIR toFhir(ENT ent);
    abstract protected Class<? extends BaseResource> getEntityClass();
    
    public FHIR transform(ENT ent) {
        var obj = toFhir(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, getProfile()));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhirId);
        return obj;        
    }
    
    private ENT createNewEntity(FHIR obj, int version, String fhirId) {
        var ent = fromFhir(obj);
        DataConvertUtil.setMetaExt(obj, ent);
        if (fhirId != null && !fhirId.isEmpty()) {
            ent.fhirId = (fhirId);
        } else {
            ent.fhirId = (StringUtil.generateUID());
        }

        ent.active = (true);
        ent.version = (version);
        ent.resCreated = (new Date());
        return ent;
    }
    
    public FHIR create(FHIR object) {
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            var entity = createNewEntity(object, version, null);
            mongo.save(entity);
            return transform(entity);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    //@CachePut(value = "patient", key = "#idType")
    public FHIR update(FHIR object, IdType idType) {
        ENT entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query.query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId)
                               .and(ConstantKeys.SP_ACTIVE).is(true))
                               .withHint("idx_by_fhirId-active");
            
            entityOld = (ENT) mongo.findOne(query, getEntityClass());
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            
            // remove old entity
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            
            // save new entity
            int version = entityOld.version + 1;
            if (object != null) {
                var entity = createNewEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return transform(entity);
            }
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    //@Cacheable(value = "patient", key = "#idType")
    public FHIR read(IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query.query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId)
                                .and(ConstantKeys.SP_ACTIVE).is(true));
            
            var entity = (ENT) mongo.findOne(query, getEntityClass());
            if (entity != null) {
                return transform(entity);
            }
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    //@CacheEvict(value = "patient", key = "#idType")
    public FHIR remove(IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query.query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId)
                                .and(ConstantKeys.SP_ACTIVE).is(true));
            
            var entity = (ENT) mongo.findOne(query, getEntityClass());
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return transform(entity);
                
            }
        }
        return null;
    }
        
    @SuppressWarnings("unchecked")
    public FHIR readOrVread(IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId)
                                    .and(ConstantKeys.SP_VERSION).is(version));
                var entity = (ENT) mongo.findOne(query, getEntityClass());
                if (entity != null) {
                    return transform(entity);
                }
            }
        }
        return null;
    }
    
    @SuppressWarnings({ "deprecation", "unchecked" })
    public List<FHIR> getHistory(IdType theId, InstantType theSince, DateRangeParam theAt, StringParam _page,
            Integer count) {
        var retVal = new ArrayList<FHIR>();
        Criteria criteria = null;
        criteria = Criteria.where("$where").is("1==1");
        if (theId != null && theId.hasIdPart()) {
            String fhirId = theId.getIdPart();
            criteria.and(ConstantKeys.SP_FHIR_ID).is(fhirId);
        }
        if (theAt != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "resUpdated", theAt);
        }
        if (theSince != null) {
            Date dateParam = theSince.getValue();
            // criteria.and("resCreated").gte(dateParam).lte(dateNow);
            criteria.and("resCreated").gte(dateParam);
        }
        if (criteria != null) {
            Query qry = Query.query(criteria);
            Pageable pageableRequest;
            pageableRequest = new PageRequest(_page != null ? Integer.valueOf(_page.getValue()) : ConstantKeys.PAGE,
                    count != null ? count : ConstantKeys.DEFAULT_PAGE_MAX_SIZE);
            qry.with(pageableRequest);
            qry.with(new Sort(Sort.Direction.DESC, "resUpdated"));
            qry.with(new Sort(Sort.Direction.DESC, "resCreated"));

            var result = mongo.find(qry, getEntityClass());
            Date cal = new Date();
            for (var ent : result) {
                retVal.add(transform((ENT)ent));
            }
            Date cal1 = new Date();
            System.out.println("-------------------history tranform end------------------"
                    + (cal1.getTime() - cal.getTime()) + " ms");
        }
        return retVal;
    }
    
    private Criteria createCriteria(Map<String, Object> params) {
        var criteria = Criteria.where(ConstantKeys.SP_ACTIVE).is(true);
        for(var item : params.entrySet()) {
            criteria.and(item.getKey()).is(item.getValue());
        }
        return criteria;
    }
    
    @SuppressWarnings("unchecked")
    public List<FHIR> getAll() {
        Query query = Query.query(Criteria.where(ConstantKeys.SP_ACTIVE).is(true));
        var lst = mongo.find(query, getEntityClass());
        return DataConvertUtil.transform(lst, x -> transform((ENT) x));
    }
    
    @SuppressWarnings("unchecked")
    public FHIR findOne(Map<String, Object> params) {        
        var query = Query.query(createCriteria(params));        
        var entity = (ENT) mongo.findOne(query, getEntityClass());
        if (entity != null) {
            return transform(entity);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public List<FHIR> find(Map<String, Object> params) {        
        var query = Query.query(createCriteria(params));        
        var lst = mongo.find(query, getEntityClass());
        return DataConvertUtil.transform(lst, x -> transform((ENT)x));
    }
}
