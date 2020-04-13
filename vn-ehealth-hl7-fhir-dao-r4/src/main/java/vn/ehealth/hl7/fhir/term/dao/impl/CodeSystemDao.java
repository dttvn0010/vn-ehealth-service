package vn.ehealth.hl7.fhir.term.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionDesignationComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.term.entity.CodeSystemEntity;
import vn.ehealth.hl7.fhir.term.entity.ConceptDesignationEntity;
import vn.ehealth.hl7.fhir.term.entity.ConceptEntity;
import vn.ehealth.hl7.fhir.term.entity.ConceptPropertyEntity;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Repository
public class CodeSystemDao extends BaseDao<CodeSystemEntity, CodeSystem> {

	@SuppressWarnings("deprecation")
	public List<Resource> search(FhirContext fhirContext, DateRangeParam date, TokenParam identifier, StringParam name,
			TokenParam code, TokenParam contentMode, StringParam description, TokenParam jurisdiction,
			TokenParam language, StringParam publisher, TokenParam status, UriParam system, StringParam title,
			UriParam url, TokenParam version,
			// COMMON
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, NumberParam _page, String sortParam, Integer count) {
		List<Resource> resources = new ArrayList<>();
		Criteria criteria = null;
		criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
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
			if (code != null || language != null) {
				for (CodeSystemEntity codeSystemEntity : results) {
					Criteria criteria1 = null;
					criteria1 = Criteria.where("codeSystemId").is(codeSystemEntity.id.toString());
					if (code != null) {
						criteria1.and("code").regex(code.getValue());
					}
					if (language != null) {
						criteria1.and("designation.language").regex(language.getValue());
					}
					Query qry1 = Query.query(criteria1);
					List<ConceptEntity> conceptresults = mongo.find(qry1, ConceptEntity.class);
					if (conceptresults != null && conceptresults.size() > 0) {
						CodeSystem codeSystem = transform(codeSystemEntity);
						resources.add(codeSystem);
					}
				}
			} else {
				for (CodeSystemEntity codeSystemEntity : results) {
					CodeSystem codeSystem = transform(codeSystemEntity);
					resources.add(codeSystem);
				}
			}
		}
		return resources;
	}

	public List<ConceptEntity> checkHasChild(String codeSystemId, List<ConceptDefinitionComponent> concepts,
			String parentId) {
//        if(!StringUtil.isNullOrEmpty(codeSystemId)) {
//            Query results = Query.query(Criteria.where("codeSystemId").regex(codeSystemId));
//            mongo.remove(results, ConceptEntity.class);
//        }
		List<ConceptEntity> conceptEntitys = new ArrayList<>();
		for (ConceptDefinitionComponent concept : concepts) {
			ConceptEntity conceptEntity = new ConceptEntity();
			conceptEntity.codeSystemId = (codeSystemId);
			conceptEntity.code = (concept.getCode());
			conceptEntity.display = (concept.getDisplay());
			conceptEntity.definition = (concept.getDefinition());
			conceptEntity._active = (true);
			conceptEntity.parentConceptId = (parentId);
			// designation
			if (concept.hasDesignation()) {
				List<ConceptDesignationEntity> conceptDesignationEntitys = new ArrayList<>();
				for (ConceptDefinitionDesignationComponent conceptDefinitionDesignationComponent : concept
						.getDesignation()) {
					ConceptDesignationEntity conceptDesignationEntity = new ConceptDesignationEntity();
					conceptDesignationEntity.language = (conceptDefinitionDesignationComponent.getLanguage());
					conceptDesignationEntity.use = DataConvertUtil
							.fhirToEntity(conceptDefinitionDesignationComponent.getUse(), BaseCoding.class);
					conceptDesignationEntity.value = (conceptDefinitionDesignationComponent.getValue());
					conceptDesignationEntitys.add(conceptDesignationEntity);
				}
				conceptEntity.designation = (conceptDesignationEntitys);
			}
			// property
			if (concept.hasProperty()) {
				List<ConceptPropertyEntity> conceptPropertyEntitys = new ArrayList<>();
				for (ConceptPropertyComponent conceptPropertyComponent : concept.getProperty()) {
					ConceptPropertyEntity conceptPropertyEntity = new ConceptPropertyEntity();
					conceptPropertyEntity.code = (conceptPropertyComponent.getCode());
					// TODO: conceptPropertyEntity.value = (conceptPropertyComponent.getValue());
					conceptPropertyEntitys.add(conceptPropertyEntity);
				}
				conceptEntity.property = (conceptPropertyEntitys);
			}
			mongo.save(conceptEntity);
			if (concept.hasConcept()) {
				List<ConceptEntity> conceptEntitytmps = new ArrayList<>();
				conceptEntitytmps = checkHasChild(codeSystemId, concept.getConcept(), conceptEntity.id.toString());
				conceptEntity.concept = (conceptEntitytmps);
			}
			conceptEntitys.add(conceptEntity);
		}
		return conceptEntitys;
	}

	public List<ConceptEntity> checkCode_Lange(List<ConceptEntity> concepts, String codeSystemId, TokenParam code,
			TokenParam language) {
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

	public Parameters getLookupParams(TokenParam code, UriParam system, StringParam version, Coding coding,
			DateRangeParam date, TokenParam displayLanguage, TokenParam property,
			// COMMON
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content) {
		Parameters retVal = new Parameters();
		Criteria criteria = null;
		criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
		// default
		criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
				null);
		// custom
		if (coding != null) {// post
			criteria.and("url").is(coding.getSystem());
			Query qry = Query.query(criteria);
			List<CodeSystemEntity> results = mongo.find(qry, CodeSystemEntity.class);
			for (CodeSystemEntity codeSystemEntity : results) {
				Criteria criteria1 = null;
				criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
				criteria1 = Criteria.where("codeSystemId").is(codeSystemEntity.id.toString()).and("parentConceptId")
						.is("");
				if (code != null) {
					criteria1.and("code").regex(code.getValue());
				}
				Query qry1 = Query.query(criteria1);
				List<ConceptEntity> conceptresults = mongo.find(qry1, ConceptEntity.class);
				conceptresults = concepts(conceptresults, codeSystemEntity.id.toString());
				for (ConceptEntity conceptEntity : conceptresults) {
					retVal.addParameter().setName("name").setValue(new StringType(conceptEntity.code));
					retVal.addParameter().setName("version").setValue(new IntegerType(conceptEntity._version));
					retVal.addParameter().setName("display").setValue(new StringType(conceptEntity.display));
					List<ConceptDesignationEntity> designations = conceptEntity.designation;
					if (designations != null && designations.size() > 0) {
						retVal.addParameter().setName("designation");
						for (ConceptDesignationEntity conceptDesignationEntity : designations) {
							retVal.addParameter().addPart().setName("value")
									.setValue(new StringType(conceptDesignationEntity.value));
							retVal.addParameter().addPart().setName("use")
									.setValue(DataConvertUtil.entityToFhir(conceptDesignationEntity.use, Coding.class));
							retVal.addParameter().addPart().setName("language")
									.setValue(new StringType(conceptDesignationEntity.language));
						}
					}
					List<ConceptPropertyEntity> properties = conceptEntity.property;
					if (properties != null && properties.size() > 0) {
						retVal.addParameter().setName("property");
						for (ConceptPropertyEntity conceptPropertyEntity : properties) {
							retVal.addParameter().addPart().setName("code")
									.setValue(new StringType(conceptPropertyEntity.code));
							// TODO:retVal.addParameter().addPart().setName("value").setValue(conceptPropertyEntity.value);
						}

					}
				}
			}
		} else {// get
			if (system != null) {
				criteria.and("url").is(system.getValue());
			}
			Query qry = Query.query(criteria);
			List<CodeSystemEntity> results = mongo.find(qry, CodeSystemEntity.class);
			for (CodeSystemEntity codeSystemEntity : results) {
				Criteria criteria1 = null;
				criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
				criteria1 = Criteria.where("codeSystemId").is(codeSystemEntity.id.toString()).and("parentConceptId")
						.is("");
				if (code != null) {
					criteria1.and("code").regex(code.getValue());
				}
				if (version != null) {
					criteria.and("version").regex(version.getValue());
				}
				Query qry1 = Query.query(criteria1);
				List<ConceptEntity> conceptresults = mongo.find(qry1, ConceptEntity.class);
				conceptresults = concepts(conceptresults, codeSystemEntity.id.toString());
				for (ConceptEntity conceptEntity : conceptresults) {
					retVal.addParameter().setName("name").setValue(new StringType(conceptEntity.code));
					retVal.addParameter().setName("version").setValue(new IntegerType(conceptEntity._version));
					retVal.addParameter().setName("display").setValue(new StringType(conceptEntity.display));
					List<ConceptDesignationEntity> designations = conceptEntity.designation;
					if (designations != null && designations.size() > 0) {
						retVal.addParameter().setName("designation");
						for (ConceptDesignationEntity conceptDesignationEntity : designations) {
							retVal.addParameter().addPart().setName("value")
									.setValue(new StringType(conceptDesignationEntity.value));
							retVal.addParameter().addPart().setName("use")
									.setValue(DataConvertUtil.entityToFhir(conceptDesignationEntity.use, Coding.class));
							retVal.addParameter().addPart().setName("language")
									.setValue(new StringType(conceptDesignationEntity.language));
						}
					}
					List<ConceptPropertyEntity> properties = conceptEntity.property;
					if (properties != null && properties.size() > 0) {
						retVal.addParameter().setName("property");
						for (ConceptPropertyEntity conceptPropertyEntity : properties) {
							retVal.addParameter().addPart().setName("code")
									.setValue(new StringType(conceptPropertyEntity.code));
							// TOOD:retVal.addParameter().addPart().setName("value").setValue(conceptPropertyEntity.value);
						}

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
		List<Resource> resources = new ArrayList<>();
		Criteria criteria = null;
		criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
		if (date != null) {
			criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "date", date);
		}
		// default
		criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
				identifier);
		// custom
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
		if (criteria != null) {
			Query qry = Query.query(criteria);
			List<CodeSystemEntity> results = mongo.find(qry, CodeSystemEntity.class);
			if (code != null || language != null) {
				for (CodeSystemEntity codeSystemEntity : results) {
					Criteria criteria1 = null;
					criteria1 = Criteria.where("codeSystemId").is(codeSystemEntity.id.toString());
					if (code != null) {
						criteria1.and("code").regex(code.getValue());
					}
					if (language != null) {
						criteria1.and("designation.language").regex(language.getValue());
					}
					Query qry1 = Query.query(criteria1);
					List<ConceptEntity> conceptresults = mongo.find(qry1, ConceptEntity.class);
					if (conceptresults != null && conceptresults.size() > 0) {
						CodeSystem codeSystem = transform(codeSystemEntity);
						resources.add(codeSystem);
					}
				}
			} else {
				for (CodeSystemEntity codeSystemEntity : results) {
					CodeSystem codeSystem = transform(codeSystemEntity);
					resources.add(codeSystem);
				}
			}
		}
		return resources.size();
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
