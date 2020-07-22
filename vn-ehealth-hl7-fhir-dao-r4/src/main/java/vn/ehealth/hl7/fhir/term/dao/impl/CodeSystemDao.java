package vn.ehealth.hl7.fhir.term.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.UriType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.dao.CachingConfiguration;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.term.entity.CodeSystemEntity;
import vn.ehealth.hl7.fhir.term.entity.ConceptEntity;
import vn.ehealth.utils.MongoUtils;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Repository
public class CodeSystemDao extends BaseDao<CodeSystemEntity, CodeSystem> {

	private @Autowired ConceptDao conceptDao;
	
	private CodeSystemEntity createNewEntity(CodeSystem obj, int version, String fhirId) {
		CodeSystemEntity ent = fhirToEntity(obj, CodeSystemEntity.class);

		if (ent == null)
			return null;

		if (fhirId != null && !fhirId.isEmpty()) {
			ent._fhirId = (fhirId);
		} else {
			ent._fhirId = (StringUtil.generateUUID());
		}

		ent._active = (true);
		ent._version = (version);
		ent._resCreated = (new Date());
		ent._resUpdated = (new Date());
		ent._fhirVersion = FhirVersionEnum.R4.toString();
		return ent;
	}
	
	private CodeSystemEntity getEntityByUrl(String url) {
		var params = mapOf("url", (Object) url, "status", PublicationStatus.ACTIVE.toCode());
		var query = MongoUtils.createQuery(params);
		return getEntity(query);
	}
	
	public CodeSystem getByUrl(String url) {
		var ent = getEntityByUrl(url);
		return transform(ent);
	}
	
	@Override
	public CodeSystem create(CodeSystem object) throws Exception {
		if (object != null) {
			var oldEnt = getEntityByUrl(object.getUrl());
			if(oldEnt != null) {
				int oldVersion = Integer.valueOf(oldEnt.version);
				object.setVersion(String.valueOf(1 + oldVersion));
				oldEnt.status = PublicationStatus.RETIRED.toCode();
				mongo.save(oldEnt);
				
				var oldConceptEnts = conceptDao.getByCodeSystem(oldEnt._fhirId);
				for(var oldConceptEnt : oldConceptEnts) {
					oldConceptEnt.status = PublicationStatus.RETIRED.toCode();
					mongo.save(oldConceptEnt);
				}
			}else {
				object.setVersion(String.valueOf(1));
			}
						
			object.setStatus(PublicationStatus.ACTIVE);
			object.setDate(new Date());
			var entity = createNewEntity(object, ConstantKeys.VERSION_1, null);
			mongo.save(entity);
			
			if(entity.concept != null) {
				for(var conceptEntity : entity.concept) {
					conceptEntity.codeSystemId = entity._fhirId;
					conceptEntity.status = PublicationStatus.ACTIVE.toCode();
					conceptEntity.date = object.getDate();					
					conceptDao.create(conceptEntity);
				}
			}
			
			return transform(entity);
		}
		return null;
	}
	
	public CodeSystem _read(IdType idType, boolean includeConcepts) {
		if (idType != null && idType.hasIdPart()) {
			String fhirId = idType.getIdPart();
			Query query = Query
					.query(Criteria.where(ConstantKeys.QP_FHIRID).is(fhirId).and(ConstantKeys.QP_ACTIVE).is(true));

			var entity = mongo.findOne(query, CodeSystemEntity.class);
			
			if (entity != null && includeConcepts) {
				entity.concept = conceptDao.getByCodeSystem(entity._fhirId);				
			}
			
			return transform(entity);
		}
		
		return null;
	}
	
	@Cacheable(cacheResolver = CachingConfiguration.CACHE_RESOLVER_NAME, key = "#idType", condition = "#idType!=null")
	public CodeSystem read(IdType idType) {
        return _read(idType, true);
    }

	public CodeSystem readOrVread(IdType idType) {
		if (idType.hasVersionIdPart() && idType.hasIdPart()) {
			String fhirId = idType.getIdPart();
			Integer version = Integer.valueOf(idType.getVersionIdPart());
			if (version != null) {
				Query query = Query.query(
						Criteria.where(ConstantKeys.QP_FHIRID).is(fhirId).and(ConstantKeys.QP_VERSION).is(version));
				var entity = mongo.findOne(query, CodeSystemEntity.class);
				if (entity != null) {
					entity.concept = conceptDao.getByCodeSystem(entity._fhirId);
					return transform(entity);
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public List<Resource> search(FhirContext fhirContext, DateRangeParam date, TokenParam identifier, StringParam name,
			TokenParam code, TokenParam contentMode, StringParam description, TokenParam jurisdiction,
			TokenParam language, StringParam publisher, TokenParam status, UriParam system, StringParam title,
			UriParam url, TokenParam version,
			// COMMON
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, NumberParam _page, String sortParam, Integer count) {
		
		var resources = new ArrayList<Resource>();
		
		var criteria = setParamToCriteria(date, identifier, name, code, contentMode, description, 
						jurisdiction, language, publisher, status, system, title, url, version,
						resid, _lastUpdated, _tag, _profile, _query, _security, _content);

		if (criteria != null) {
			Query query = Query.query(criteria);
			Pageable pageableRequest;
			pageableRequest = new PageRequest(
					_page != null ? Integer.valueOf(_page.getValue().intValue()) : ConstantKeys.PAGE,
					count != null ? count : ConstantKeys.DEFAULT_PAGE_SIZE);
			query.with(pageableRequest);
			if (sortParam != null && !sortParam.equals("")) {
				query.with(new Sort(Sort.Direction.DESC, sortParam));
			} else {
				query.with(new Sort(Sort.Direction.DESC, ConstantKeys.QP_UPDATED));
				query.with(new Sort(Sort.Direction.DESC, ConstantKeys.QP_CREATED));
			}
			List<CodeSystemEntity> results = mongo.find(query, CodeSystemEntity.class);
			for(CodeSystemEntity codeSystemEntity : results) {
				/*
				var criteria1 = Criteria.where("codeSystemId").is(codeSystemEntity._fhirId);
				if(code != null) {
					criteria1.and("code").regex(code.getValue());
				}
				if (language != null) {
					criteria1.and("designation.language").regex(language.getValue());
				}
				codeSystemEntity.concept = mongo.find(Query.query(criteria1), ConceptEntity.class);*/
				CodeSystem codeSystem = transform(codeSystemEntity);
				resources.add(codeSystem);
			}			
		}
		return resources;
	}

	public List<ConceptEntity> concepts(List<ConceptEntity> concepts, String codeSystemId) {
		List<ConceptEntity> conceptmps = new ArrayList<>();
		if (concepts.size() > 0) {
			for (ConceptEntity conceptEntity : concepts) {
				Criteria criteria2 = null;
				criteria2 = Criteria.where("codeSystemId").regex(codeSystemId);
				criteria2.and("parentConceptId").regex(conceptEntity.id.toString());
				Query qry2 = Query.query(criteria2);
				List<ConceptEntity> conceptresultmps = mongo.find(qry2, ConceptEntity.class);
				if (conceptresultmps.size() > 0) {
					concepts(conceptresultmps, codeSystemId);
					conceptEntity.concept = (conceptresultmps);
				}
			}
			conceptmps.addAll(concepts);
		}
		return conceptmps;
	}

	public Parameters lookUp(StringParam code, UriParam system, StringParam version, TokenParam coding,
			DateRangeParam date, TokenParam displayLanguage, TokenParam property) {
		Parameters retVal = new Parameters();
				
		String codeSystemUrl = null;
		String codeString = "";
		
		if(coding != null) {
			codeSystemUrl = coding.getSystem();
			codeString = coding.getValue();
		}
		
		if(code != null) {
			codeString = code.getValue();
		}
		
		if(system != null) {
			codeSystemUrl = system.getValue();
		}
		
		if(StringUtils.isEmpty(codeString) || StringUtils.isEmpty(codeSystemUrl)) {
			return null;
		}
		
		var codeSystem = getByUrl(codeSystemUrl);
		if(codeSystem == null) {
			return null;
		}
				
		var criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true)
								.and("codeSystemId").is(codeSystem.getId())
								.and("code").regex(codeString);
		
		var conceptEntity = mongo.findOne(Query.query(criteria), ConceptEntity.class);
		
		if(conceptEntity != null) {
			retVal.addParameter().setName("name").setValue(new StringType(conceptEntity.code));
			retVal.addParameter().setName("version").setValue(new StringType(conceptEntity.version));
			retVal.addParameter().setName("display").setValue(new StringType(conceptEntity.display));
			List<ConceptEntity.ConceptDefinitionDesignation> designations = conceptEntity.designation;
			
			if (designations != null && designations.size() > 0) {
				retVal.addParameter().setName("designation");
				for (var conceptDesignationEntity : designations) {
					retVal.addParameter().addPart().setName("value")
							.setValue(new StringType(conceptDesignationEntity.value));
					retVal.addParameter().addPart().setName("use")
							.setValue(entityToFhir(conceptDesignationEntity.use, Coding.class));
					retVal.addParameter().addPart().setName("language")
							.setValue(new StringType(conceptDesignationEntity.language));
				}
			}
			
			List<ConceptEntity.ConceptProperty> properties = conceptEntity.property;
			if (properties != null && properties.size() > 0) {
				var propVal = retVal.addParameter().setName("property");
				
				for (var conceptPropertyEntity : properties) {
					propVal.addPart()
							.setName(conceptPropertyEntity.code)
							.setValue(BaseType.toFhir(conceptPropertyEntity.value));
					
				}

			}
		}		
		
		return retVal;
	}
	
	private void appendCompareCriteria(Criteria cr, String op, Object value) {
        switch(op) {
            case "$lt": cr.lt(value); return;
            case "$lte": cr.lte(value);  return;
            case "$gt": cr.gt(value);  return;
            case "$gte": cr.gte(value);  return;
        }        
    }
	
    private Criteria createFindMatchCriteria(Parameters params) {
	    var codeSystemUrlParam = FPUtil.findFirst(params.getParameter(), x -> ConstantKeys.SP_SYSTEM.equals(x.getName()));
        if(codeSystemUrlParam == null) return null;
        String codeSystemUrl = ((UriType) codeSystemUrlParam.getValue()).getValue();
        
        var codeSystem = getByUrl(codeSystemUrl);
        
        if(codeSystem == null) {
            return null;
        }
        
        boolean exact = false;
        var criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true)
                                .and("codeSystemId").is(codeSystem.getId());
        
        Map<String, Criteria> propertyCriteriaMap = new HashMap<>();
        
        for(var param : params.getParameter()) {
            if(ConstantKeys.SP_EXACT.equals(param.getName())) {
                exact = ((BooleanType) param.getValue()).getValue();
            }
            
            if(ConstantKeys.SP_PROPERTY.equals(param.getName())) {
                String propertyCode = "";
                Object propertyValue = null;
                boolean matchExact = exact;
                String op = "$eq";
                
                for(var part : param.getPart()) {
                    if(ConstantKeys.SP_CODE.equals(part.getName())) {
                        propertyCode = ((CodeType) part.getValue()).getValue();
                    }
                    
                    if(ConstantKeys.SP_VALUE.equals(part.getName())) {
                        propertyValue = part.getValue();
                        
                        if(propertyValue instanceof IntegerType) {
                            propertyValue = ((IntegerType) propertyValue).getValue();
                            matchExact = true;
                        
                        } else if(propertyValue instanceof CodeType) {
                            propertyValue = ((CodeType) propertyValue).getValue();
                            matchExact = true;
                        
                        } else if(propertyValue instanceof DateTimeType) {
                            propertyValue = new java.sql.Date(((DateTimeType) propertyValue).getValue().getTime());
                            
                            if(propertyCode.endsWith("__from")) {
                                propertyCode = propertyCode.replace("__from", "");
                                op = "$gte";
                            }else if(propertyCode.endsWith("__to")) {
                                propertyCode = propertyCode.replace("__to", "");
                                op = "$lte";
                            }
                        
                        } else if(propertyValue != null) {
                            propertyValue = ((Type) propertyValue).primitiveValue();
                        }
                    }
                }
                
                if(!StringUtils.isEmpty(propertyCode)) {
                    
                    if(op.equals("$eq")) {
                        Criteria cr = null;
                        if(matchExact) {
                            cr = Criteria.where("code").is(propertyCode)
                                         .and("value.value").is(propertyValue);
                            
                        }else {
                            cr = Criteria.where("code").is(propertyCode)
                                         .and("value.value").regex((String) propertyValue, "i");
                        }
                        propertyCriteriaMap.put(propertyCode, cr);
                    }else {
                        Criteria cr = propertyCriteriaMap.get(propertyCode);
                        if(cr == null) {
                            cr = Criteria.where("code").is(propertyCode)
                                        .and("value.value");                            
                            propertyCriteriaMap.put(propertyCode, cr);
                        }
                        appendCompareCriteria(cr, op, propertyValue);
                    }                    
                }
            }
        }
        
        var propertyCriteriaList = new ArrayList<Criteria>();
        for(String propertyCode : propertyCriteriaMap.keySet()) {
            var cr = propertyCriteriaMap.get(propertyCode);
            propertyCriteriaList.add(Criteria.where("property").elemMatch(cr));
        }
        
        if(propertyCriteriaList.size() > 0) {
            criteria.andOperator(propertyCriteriaList.toArray(new Criteria[0]));
        }
        
        return criteria;
	}
	
	public long countMatches(Parameters parameters) {
	    var criteria = createFindMatchCriteria(parameters);
	    if(criteria == null) return 0;
        
        return mongo.count(new Query(criteria), ConceptEntity.class);
        
    }
	
	public Parameters findMatches(Parameters parameters) {
	    Parameters retVal = new Parameters();
	    var match = retVal.addParameter();
        match.setName("match");
        
        var criteria = createFindMatchCriteria(parameters);
	    
	    if(criteria != null) {	    
    	    var codeSystemUrlParam = FPUtil.findFirst(parameters.getParameter(), x -> ConstantKeys.SP_SYSTEM.equals(x.getName()));        
            String codeSystemUrl = ((UriType) codeSystemUrlParam.getValue()).getValue();
            
            int page = 0;
            var pageParam = FPUtil.findFirst(parameters.getParameter(), x -> ConstantKeys.SP_PAGE.equals(x.getName()));
            if(pageParam != null) page = ((IntegerType) pageParam.getValue()).getValue();
            
            int count = ConstantKeys.DEFAULT_PAGE_SIZE;
            var countParam = FPUtil.findFirst(parameters.getParameter(), x -> ConstantKeys.SP_COUNT.equals(x.getName()));        
            if(countParam != null) count = ((IntegerType) countParam.getValue()).getValue();
                        
    		var query = new Query(criteria);
    		query.skip(page * ConstantKeys.DEFAULT_PAGE_SIZE).limit(count);
    		
    		var conceptEntityList = mongo.find(query, ConceptEntity.class);
    		    		
    		for(var conceptEntity : conceptEntityList) {
    			var part = match.addPart();
    			part.setName("code");
    			var code = new Coding();
    			code.setCode(conceptEntity.code);
    			code.setDisplay(conceptEntity.display);
    			code.setSystem(codeSystemUrl);
    			part.setValue(code);
    			if(conceptEntity.property != null) {
    			    for(var property : conceptEntity.property) {
    			        part.addPart().setName(property.code).setValue(BaseType.toFhir(property.value));
    			    }
    			}
    		}			
	    }
		
		return retVal;
	}

	public long findMatchesAdvancedTotal(FhirContext ctx, DateRangeParam date, TokenParam identifier, StringParam name,
			TokenParam code, TokenParam contentMode, StringParam description, TokenParam jurisdiction,
			TokenParam language, StringParam publisher, TokenParam status, UriParam system, StringParam title,
			UriParam url, TokenParam version, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
			UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
		
		var criteria = setParamToCriteria(date, identifier, name, code, contentMode, description, 
				jurisdiction, language, publisher, status, system, title, url, version,
				resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		
		return mongo.count(Query.query(criteria), CodeSystem.class);
		
	}

	private Criteria setParamToCriteria(DateRangeParam date, TokenParam identifier, StringParam name,
			TokenParam code, TokenParam contentMode, StringParam description, TokenParam jurisdiction,
			TokenParam language, StringParam publisher, TokenParam status, UriParam system, StringParam title,
			UriParam url, TokenParam version,
			// COMMON
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content) {
		
		Criteria criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
		// default
		criteria = DatabaseUtil.addParamDefault2Criteria(criteria, null, _lastUpdated, _tag, _profile, _security,
				identifier);
		// custom
		if (date != null) {
			criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "date", date);
		}
		if (name != null) {
			criteria.and("name").regex(name.getValue());
		}
		if (contentMode != null) {
			criteria.and("content").regex(contentMode.getValue());
		}
		if (description != null) {
			criteria.and("description").is(description.getValue());
		}
		if (jurisdiction != null) {
			criteria.and("jurisdiction").regex(jurisdiction.getValue());
		}
		if (publisher != null) {
			criteria.and("publisher").regex(publisher.getValue());
		}
		if (status != null) {
			criteria.and("status").regex(status.getValue());
		}
		if (system != null) {
			criteria.and("system").regex(system.getValue());
		}
		if (title != null) {
			criteria.and("title").regex(title.getValue());
		}
		if (url != null) {
			criteria.and("url").regex(url.getValue());
		}
		if (version != null) {
			criteria.and("version").regex(version.getValue());
		}
		return criteria;
	}
			
	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return CodeSystemEntity.class;
	}

	@Override
	protected Class<? extends DomainResource> getResourceClass() {
		return CodeSystem.class;
	}
}
