package vn.ehealth.hl7.fhir.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.InstantType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

public abstract class BaseDao<ENT extends BaseResource, FHIR extends DomainResource> {
    @Autowired
    protected MongoOperations mongo;
    
    abstract protected String getProfile();
    abstract protected Class<? extends BaseResource> getEntityClass();
    abstract protected Class<? extends DomainResource> getResourceClass();
    
    @SuppressWarnings("unchecked")
    public FHIR transform(ENT ent) {
        if(ent == null) return null;
        var obj = DataConvertUtil.entityToFhir(ent, getResourceClass());
        obj.setMeta(DataConvertUtil.getMeta(ent, getProfile()));
        obj.setId(ent.fhirId);
        return (FHIR) obj;        
    }
    
    @SuppressWarnings("unchecked")
    private ENT createNewEntity(FHIR obj, int version, String fhirId) {
        var ent = DataConvertUtil.fhirToEntity(obj, getEntityClass());
        if (fhirId != null && !fhirId.isEmpty()) {
            ent.fhirId = (fhirId);
        } else {
            ent.fhirId = (StringUtil.generateUUID());
        }

        ent.active = (true);
        ent.version = (version);
        ent.resCreated = (new Date());
        ent.fhirVersion = "R4";
        return (ENT) ent;
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
    @CachePut(cacheResolver = CachingConfiguration.CACHE_RESOLVER_NAME, key = "#idType", condition="#idType!=null")
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
    @Cacheable(cacheResolver = CachingConfiguration.CACHE_RESOLVER_NAME, key = "#idType", condition="#idType!=null")
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
    @CacheEvict(cacheResolver = CachingConfiguration.CACHE_RESOLVER_NAME, key = "#idType", condition="#idType!=null")
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
    
    @SuppressWarnings({ "unchecked", "deprecation" })
    public List<IBaseResource> getHistory(IdType theId, InstantType theSince, DateRangeParam theAt, StringParam _page,
            Integer count) {
    	List<IBaseResource> retVal = new ArrayList<>();
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
                    count != null ? count : ConstantKeys.DEFAULT_PAGE_SIZE);
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
    
    public int countHistory(IdType theId, InstantType theSince, DateRangeParam theAt, StringParam _page,
            Integer count) {
    	int retVal = 0;
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

            retVal = (int) mongo.count(qry, getEntityClass());
        }
        return retVal;
    }
    
    @SuppressWarnings("unchecked")
    public List<FHIR> findByCriteria(Criteria criteria) {
        var query = Query.query(criteria);
        var lst = mongo.find(query, getEntityClass());
        return DataConvertUtil.transform(lst, x -> transform((ENT)x));
    }
    
    @SuppressWarnings("unchecked")
    public List<FHIR> searchResource(Criteria criteria, Boolean active, int start, int count, Sort sort) {
        if(active != null) {
            criteria.and("active").is(active);
        }else {
            criteria.and("active").is(true);
        }
                
        var query = Query.query(criteria);
        
        if(sort != null) {
            query.with(sort);
        }else {
            query.with(new Sort(Sort.Direction.DESC, "resUpdated"));
            query.with(new Sort(Sort.Direction.DESC, "resCreated"));
        }
        
        if(start >= 0) query.skip(start);
        if(count >= 0) query.limit(count);
        var lst = (List<ENT>) mongo.find(query, getEntityClass());
        return DataConvertUtil.transform(lst, x -> transform((ENT)x));
    }
    
    public List<FHIR> searchResource(Criteria criteria, int start, int count, Sort sort) {
        return searchResource(criteria, null, start, count, sort);
    }
    
    public List<FHIR> searchResource(Criteria criteria, int start, int count) {
        return searchResource(criteria, start, count, null);
    }
    
    public List<FHIR> searchResource(Criteria criteria) {
        return searchResource(criteria, -1, -1);
    }
    
    @SuppressWarnings("unchecked")
    public FHIR getResource(Criteria criteria) {
        criteria.and("active").is(true);
        var query = Query.query(criteria);        
        var ent = (ENT) mongo.findOne(query, getEntityClass());
        return transform(ent);
    }
    
    public int countByCriteria(Criteria criteria) {
        var query = Query.query(criteria);
        int count = (int) mongo.count(query, getEntityClass());
        return count;
    }
}
