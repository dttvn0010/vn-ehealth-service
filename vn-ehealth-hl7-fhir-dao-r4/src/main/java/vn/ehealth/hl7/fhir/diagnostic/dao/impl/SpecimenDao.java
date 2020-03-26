package vn.ehealth.hl7.fhir.diagnostic.dao.impl;

import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.getIncludeMap;
import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.setReferenceResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.Specimen;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.diagnostic.entity.SpecimenEntity;
import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.*;


@Repository
public class SpecimenDao extends BaseDao<SpecimenEntity, Specimen> {
	@SuppressWarnings("deprecation")
	public List<IBaseResource> search(FhirContext fhirContext, TokenParam active, ReferenceParam request, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content, StringParam _page, String sortParam, Integer count, Set<Include> includes) {
		List<IBaseResource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(active, request, resid, _lastUpdated, _tag, _profile, _query, _security,
				_content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
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
		
		String[] keys = {"subject", "request", "parent", "processing:additive"};

        var includeMap = getIncludeMap(ResourceType.Specimen, keys, includes);
        
		List<SpecimenEntity> specimenEntitys = mongo.find(query, SpecimenEntity.class);
		if (specimenEntitys != null) {
			for (SpecimenEntity item : specimenEntitys) {
				Specimen obj = transform(item);
				
				if(includeMap.get("subject") && obj.hasSubject()) {
                    setReferenceResource(obj.getSubject());
                }
                
                if(includeMap.get("request") && obj.hasRequest()) {
                    setReferenceResource(obj.getRequest());
                }
                
                if(includeMap.get("parent") && obj.hasParent()) {
                    setReferenceResource(obj.getParent());
                }
                
                if(includeMap.get("processing:additive") && obj.hasProcessing()) {
                    obj.getProcessing().forEach(x -> setReferenceResource(x.getAdditive()));
                }
                
				resources.add(obj);
			}
		}
		return resources;
	}

	public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, ReferenceParam request, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content) {
		long total = 0;
		Criteria criteria = setParamToCriteria(active, request, resid, _lastUpdated, _tag, _profile, _query, _security,
				_content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, SpecimenEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(TokenParam active, ReferenceParam request, TokenParam resid, DateRangeParam _lastUpdated,
			TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
		Criteria criteria = null;
		// active
		if (active != null) {
			criteria = Criteria.where("active").is(active);
		} else {
			criteria = Criteria.where("active").is(true);
		}
		if(request != null) {
		    criteria.and("request.reference").is(request.getValue());
		}
		// set param default
		criteria = addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
				null);

		return criteria;
	}
	
	@Override
	protected String getProfile() {
		return "Specimen-v1.0";
	}

	@Override
	protected SpecimenEntity fromFhir(Specimen obj) {
		return SpecimenEntity.fromSpecimen(obj);
	}

	@Override
	protected Specimen toFhir(SpecimenEntity ent) {
		return SpecimenEntity.toSpecimen(ent);
	}

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return SpecimenEntity.class;
	}
}
