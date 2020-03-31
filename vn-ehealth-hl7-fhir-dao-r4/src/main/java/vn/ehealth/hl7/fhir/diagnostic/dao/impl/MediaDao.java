package vn.ehealth.hl7.fhir.diagnostic.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Media;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.*;
import vn.ehealth.hl7.fhir.diagnostic.entity.MediaEntity;

@Repository
public class MediaDao extends BaseDao<MediaEntity, Media> {

	@Override
	protected String getProfile() {
		return "Media-v1.0";
	}

	@Override
    public Class<? extends DomainResource> getResourceClass() {
        return Media.class;
    }

	@Override
	public Class<? extends BaseResource> getEntityClass() {
		return MediaEntity.class;
	}

	@SuppressWarnings("deprecation")
	public List<IBaseResource> search(ReferenceParam basedOn, DateRangeParam created, ReferenceParam device,
			ReferenceParam encounter, TokenParam identifier, TokenParam modality, ReferenceParam operator,
			ReferenceParam patient, TokenParam site, TokenParam status, ReferenceParam subject, TokenParam type,
			TokenParam view,
			// COMMON PARAMS
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, StringParam _page, String sortParam, Integer count,
			Set<Include> includes) {
		List<IBaseResource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(basedOn, created, device, encounter, identifier, modality, operator,
				patient, site, status, subject, type, view,
				// COMMON PARAMS
				resid, _lastUpdated, _tag, _profile, _query, _security, _content);
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

		String[] keys = { "subject", "encounter", "basedOn", "partOf", "operator", "device" };

		var includeMap = getIncludeMap(ResourceType.Media, keys, includes);

		List<MediaEntity> lst = mongo.find(query, MediaEntity.class);
		if (lst != null && lst.size() > 0) {
			for (MediaEntity item : lst) {
				Media obj = transform(item);
				if (includeMap.get("subject") && obj.hasSubject()) {
					setReferenceResource(obj.getSubject());
				}

				if (includeMap.get("encounter") && obj.hasEncounter()) {
					setReferenceResource(obj.getEncounter());
				}

				if (includeMap.get("basedOn") && obj.hasBasedOn()) {
					setReferenceResource(obj.getBasedOn());
				}

				if (includeMap.get("partOf") && obj.hasPartOf()) {
					setReferenceResource(obj.getPartOf());
				}

				if (includeMap.get("operator") && obj.hasOperator()) {
					setReferenceResource(obj.getOperator());
				}

				if (includeMap.get("device") && obj.hasDevice()) {
					setReferenceResource(obj.getDevice());
				}

				resources.add(obj);
			}
		}
		return null;
	}

	public long countMatchesAdvancedTotal(ReferenceParam basedOn, DateRangeParam created, ReferenceParam device,
			ReferenceParam encounter, TokenParam identifier, TokenParam modality, ReferenceParam operator,
			ReferenceParam patient, TokenParam site, TokenParam status, ReferenceParam subject, TokenParam type,
			TokenParam view,
			// COMMON PARAMS
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content) {
		long total = 0;
		Criteria criteria = setParamToCriteria(basedOn, created, device, encounter, identifier, modality, operator,
				patient, site, status, subject, type, view,
				// COMMON PARAMS
				resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, MediaEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(ReferenceParam basedOn, DateRangeParam created, ReferenceParam device,
			ReferenceParam encounter, TokenParam identifier, TokenParam modality, ReferenceParam operator,
			ReferenceParam patient, TokenParam site, TokenParam status, ReferenceParam subject, TokenParam type,
			TokenParam view,
			// COMMON PARAMS
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content) {
		Criteria criteria = null;
		// active
		criteria = Criteria.where("active").is(true);
		// set param default
		criteria = addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security, identifier);

		return criteria;
	}

}
