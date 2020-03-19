package vn.ehealth.hl7.fhir.term.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ConceptMap;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.term.entity.ConceptMapEntity;
import vn.ehealth.hl7.fhir.term.entity.DependOnEntity;
import vn.ehealth.hl7.fhir.term.entity.ElementEntity;
import vn.ehealth.hl7.fhir.term.entity.GroupElementEntity;
import vn.ehealth.hl7.fhir.term.entity.TargetElementEntity;
import vn.ehealth.hl7.fhir.term.entity.UnMappedEntity;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Repository
public class ConceptMapDao extends BaseDao<ConceptMapEntity, ConceptMap> {
   
    @SuppressWarnings("deprecation")
    public List<Resource> search(FhirContext ctx, TokenParam active, DateRangeParam date, UriParam dependson,
            StringParam description, TokenParam identifier, TokenParam jurisdiction, StringParam name, UriParam other,
            UriParam product, StringParam publisher, TokenParam code, UriParam source, TokenParam status,
            UriParam target, StringParam title, UriParam url, TokenParam version, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content, StringParam _page, String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, date, dependson, description, identifier, jurisdiction, name,
                other, product, publisher, code, source, status, target, title, url, version, resid, _lastUpdated, _tag,
                _profile, _query, _security, _content);

        if (criteria != null) {
            Query query = Query.query(criteria);
            Pageable pageableRequest;
            pageableRequest = new PageRequest(_page != null ? Integer.valueOf(_page.getValue()) : ConstantKeys.PAGE,
                    count != null ? count : ConstantKeys.DEFAULT_PAGE_SIZE);
            query.with(pageableRequest);
    		if (sortParam != null && !sortParam.equals("")) {
    			query.with(new Sort(Sort.Direction.DESC, sortParam));
    		} else {
    			query.with(new Sort(Sort.Direction.DESC, "resUpdated"));
    			query.with(new Sort(Sort.Direction.DESC, "resCreated"));
    		}
            List<ConceptMapEntity> results = mongo.find(query, ConceptMapEntity.class);

            for (ConceptMapEntity conceptMapEntity : results) {
                Criteria criteria1 = null;
                criteria1 = Criteria.where("conceptMapID").is(conceptMapEntity.id.toString());
                if (source != null) {
                    criteria1.and("source").regex(source.getValue());
                }
                if (target != null) {
                    criteria1.and("target").regex(target.getValue());
                }
                Query qry1 = Query.query(criteria1);
                List<GroupElementEntity> groups = mongo.find(qry1, GroupElementEntity.class);
                List<GroupElementEntity> groupTmps = new ArrayList<>();
                if (groups != null && groups.size() > 0) {
                    for (GroupElementEntity groupElementEntity : groups) {
                        List<ElementEntity> elements = getElements(groupElementEntity.id.toString());
                        groupElementEntity.element = (elements);
                        UnMappedEntity unMapped = getUnMappeds(groupElementEntity.id.toString());
                        groupElementEntity.unmapped = (unMapped);
                        groupTmps.add(groupElementEntity);
                    }
                    conceptMapEntity.group = (groupTmps);
                    ConceptMap conceptMap = transform(conceptMapEntity);
                    resources.add(conceptMap);
                }

            }
        }
        return resources;
    }

    public Parameters getTranslateParams(TokenParam code, UriParam system, StringParam version, UriParam source,
            Coding coding, UriParam target, StringParam reverse) {
        // TODO Auto-generated method stub
        return null;
    }

    public ConceptMap getClosureParams(StringParam name, StringParam version, Coding concept) {
        // TODO Auto-generated method stub
        return null;
    }

    public long findMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, DateRangeParam date,
            UriParam dependson, StringParam description, TokenParam identifier, TokenParam jurisdiction,
            StringParam name, UriParam other, UriParam product, StringParam publisher, TokenParam code, UriParam source,
            TokenParam status, UriParam target, StringParam title, UriParam url, TokenParam version, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, date, dependson, description, identifier, jurisdiction, name,
                other, product, publisher, code, source, status, target, title, url, version, resid, _lastUpdated, _tag,
                _profile, _query, _security, _content);

        Query qry = Query.query(criteria);
        List<ConceptMapEntity> results = mongo.find(qry, ConceptMapEntity.class);

        for (ConceptMapEntity conceptMapEntity : results) {
            Criteria criteria1 = null;
            criteria1 = Criteria.where("conceptMapID").is(conceptMapEntity.id.toString());
            if (source != null) {
                criteria1.and("source").regex(source.getValue());
            }
            if (target != null) {
                criteria1.and("target").regex(target.getValue());
            }
            Query qry1 = Query.query(criteria1);
            List<GroupElementEntity> groups = mongo.find(qry1, GroupElementEntity.class);
            List<GroupElementEntity> groupTmps = new ArrayList<>();
            if (groups != null && groups.size() > 0) {
                for (GroupElementEntity groupElementEntity : groups) {
                    List<ElementEntity> elements = getElements(groupElementEntity.id.toString());
                    groupElementEntity.element = (elements);
                    UnMappedEntity unMapped = getUnMappeds(groupElementEntity.id.toString());
                    groupElementEntity.unmapped = (unMapped);
                    groupTmps.add(groupElementEntity);
                }
                conceptMapEntity.group = (groupTmps);
                ConceptMap conceptMap = transform(conceptMapEntity);
                resources.add(conceptMap);
            }

        }
        return resources.size();
    }

    public List<GroupElementEntity> getGroups(String objectId) {
        List<GroupElementEntity> groupTmps = new ArrayList<>();
        if (!StringUtils.isBlank(objectId)) {
            Criteria criteria = null;
            criteria = Criteria.where("conceptMapID").regex(objectId);
            Query qry = Query.query(criteria);
            List<GroupElementEntity> groups = mongo.find(qry, GroupElementEntity.class);
            if (groups.size() > 0) {
                for (GroupElementEntity groupElementEntity : groups) {
                    List<ElementEntity> elements = getElements(groupElementEntity.id.toString());
                    groupElementEntity.element = (elements);
                    UnMappedEntity unMapped = getUnMappeds(groupElementEntity.id.toString());
                    groupElementEntity.unmapped = (unMapped);
                    groupTmps.add(groupElementEntity);
                }
            }
        }
        return groupTmps;
    }

    public List<ElementEntity> getElements(String objectId) {
        List<ElementEntity> elementTmps = new ArrayList<>();
        if (!StringUtils.isBlank(objectId)) {
            Criteria criteria = null;
            criteria = Criteria.where("groupElementID").regex(objectId);
            Query qry = Query.query(criteria);
            List<ElementEntity> elements = mongo.find(qry, ElementEntity.class);
            if (elements.size() > 0) {
                for (ElementEntity elementEntity : elements) {
                    List<TargetElementEntity> targets = getTargets(elementEntity.id.toString());
                    elementEntity.target = (targets);
                    elementTmps.add(elementEntity);
                }
            }
        }
        return elementTmps;
    }

    public List<TargetElementEntity> getTargets(String objectId) {
        List<TargetElementEntity> targetTmps = new ArrayList<>();
        if (!StringUtils.isBlank(objectId)) {
            Criteria criteria = null;
            criteria = Criteria.where("elementEntityID").regex(objectId);
            Query qry = Query.query(criteria);
            List<TargetElementEntity> targets = mongo.find(qry, TargetElementEntity.class);
            if (targets.size() > 0) {
                for (TargetElementEntity targetElementEntity : targets) {
                    List<DependOnEntity> dependOns = getDependOns(targetElementEntity.id.toString());
                    targetElementEntity.dependsOn = (dependOns);
                    targetTmps.add(targetElementEntity);
                }
            }
        }
        return targetTmps;
    }

    public List<DependOnEntity> getDependOns(String objectId) {
        List<DependOnEntity> dependOns = new ArrayList<>();
        if (!StringUtils.isBlank(objectId)) {
            Criteria criteria = null;
            criteria = Criteria.where("targetElementEntityID").regex(objectId);
            Query qry = Query.query(criteria);
            dependOns = mongo.find(qry, DependOnEntity.class);
        }
        return dependOns;
    }

    public UnMappedEntity getUnMappeds(String objectId) {
        UnMappedEntity unMapped = null;
        if (!StringUtils.isBlank(objectId)) {
            Criteria criteria = null;
            criteria = Criteria.where("groupElementEntityID").regex(objectId);
            Query qry = Query.query(criteria);
            unMapped = mongo.findOne(qry, UnMappedEntity.class);
        }
        return unMapped;
    }

    private Criteria setParamToCriteria(TokenParam active, DateRangeParam date, UriParam dependson,
            StringParam description, TokenParam identifier, TokenParam jurisdiction, StringParam name, UriParam other,
            UriParam product, StringParam publisher, TokenParam code, UriParam source, TokenParam status,
            UriParam target, StringParam title, UriParam url, TokenParam version, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        Criteria criteria = null;
        // active
        if (active != null) {
            criteria = Criteria.where("active").is(active);
        } else {
            criteria = Criteria.where("active").is(true);
        }
        // set param default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                identifier);
        // custom
        if (name != null) {
            criteria.and("name").regex(name.getValue());
        }
        // name
        if (description != null) {
            criteria.and("description").is(description.getValue());
        }
        // jurisdiction
        if (jurisdiction != null) {
            criteria.and("jurisdiction").regex(jurisdiction.getValue());
        }
        // publisher
        if (publisher != null) {
            criteria.and("publisher").regex(publisher.getValue());
        }
        // status
        if (status != null) {
            criteria.and("status").regex(status.getValue());
        }
        // title
        if (title != null) {
            criteria.and("title").regex(title.getValue());
        }
        // url
        if (url != null) {
            criteria.and("url").regex(url.getValue());
        }
        // version
        if (version != null) {
            criteria.and("version").regex(version.getValue());
        }
        return criteria;
    }

    @Override
    protected String getProfile() {
        return "ConceptMap-v1.0";
    }

    @Override
    protected ConceptMapEntity fromFhir(ConceptMap obj) {
        return ConceptMapEntity.fromConceptMap(obj);
    }

    @Override
    protected ConceptMap toFhir(ConceptMapEntity ent) {
        return ConceptMapEntity.toConceptMap(ent);
    }

    @Override
    protected Class<? extends BaseResource> getEntityClass() {
        return ConceptMapEntity.class;
    }

}
