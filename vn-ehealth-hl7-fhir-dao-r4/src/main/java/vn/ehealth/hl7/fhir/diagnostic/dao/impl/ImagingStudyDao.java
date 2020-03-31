package vn.ehealth.hl7.fhir.diagnostic.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.ImagingStudy;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.diagnostic.entity.ImagingStudyEntity;

import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.*;

@Repository
public class ImagingStudyDao extends BaseDao<ImagingStudyEntity, ImagingStudy> {

	@SuppressWarnings("deprecation")
	public List<IBaseResource> search(FhirContext fhirContext, TokenParam resid, DateRangeParam _lastUpdated,
			TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content,
			StringParam _page, String sortParam, Integer count, Set<Include> includes) {
		List<IBaseResource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(resid, _lastUpdated, _tag, _profile, _query, _security, _content);
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

		String[] keys = { "subject", "encounter", "basedOn", "referrer", "interpreter", "endpoint",
				"procedureReference", "location", "reasonReference" };

		var includeMap = getIncludeMap(ResourceType.ImagingStudy, keys, includes);

		List<ImagingStudyEntity> ImagingStudyEntitys = mongo.find(query, ImagingStudyEntity.class);
		if (ImagingStudyEntitys != null) {
			for (ImagingStudyEntity item : ImagingStudyEntitys) {
				ImagingStudy obj = transform(item);
				if (includeMap.get("subject") && obj.hasSubject()) {
					setReferenceResource(obj.getSubject());
				}

				if (includeMap.get("encounter") && obj.hasEncounter()) {
					setReferenceResource(obj.getEncounter());
				}

				if (includeMap.get("basedOn") && obj.hasBasedOn()) {
					setReferenceResource(obj.getBasedOn());
				}

				if (includeMap.get("referrer") && obj.hasReferrer()) {
					setReferenceResource(obj.getReferrer());
				}

				if (includeMap.get("interpreter") && obj.hasInterpreter()) {
					setReferenceResource(obj.getInterpreter());
				}

				if (includeMap.get("endpoint") && obj.hasEndpoint()) {
					setReferenceResource(obj.getEndpoint());
				}

				if (includeMap.get("procedureReference") && obj.hasProcedureReference()) {
					setReferenceResource(obj.getProcedureReference());
				}

				if (includeMap.get("location") && obj.hasLocation()) {
					setReferenceResource(obj.getLocation());
				}

				if (includeMap.get("reasonReference") && obj.hasReasonReference()) {
					setReferenceResource(obj.getReasonReference());
				}

				resources.add(obj);
			}
		}
		return resources;
	}

	public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam resid, DateRangeParam _lastUpdated,
			TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
		long total = 0;
		Criteria criteria = setParamToCriteria(resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, ImagingStudyEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
			UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
		Criteria criteria = null;
		// active
		criteria = Criteria.where("active").is(true);

		return criteria;
	}

	@Override
	protected String getProfile() {
		return "ImagingStudy-v1.0";
	}

	@Override
    public Class<? extends DomainResource> getResourceClass() {
        return ImagingStudy.class;
    }

	@Override
	public Class<? extends BaseResource> getEntityClass() {
		return ImagingStudyEntity.class;
	}
}
