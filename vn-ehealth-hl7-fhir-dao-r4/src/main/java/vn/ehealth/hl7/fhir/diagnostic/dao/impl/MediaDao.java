package vn.ehealth.hl7.fhir.diagnostic.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Media;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
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
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.diagnostic.entity.DiagnosticReportEntity;
import vn.ehealth.hl7.fhir.diagnostic.entity.MediaEntity;

@Repository
public class MediaDao extends BaseDao<MediaEntity, Media> {

	@Override
	protected String getProfile() {
		return "Media-v1.0";
	}

	@Override
	protected MediaEntity fromFhir(Media obj) {
		return MediaEntity.from(obj);
	}

	@Override
	protected Media toFhir(MediaEntity ent) {
		return MediaEntity.to(ent);
	}

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return MediaEntity.class;
	}

	@SuppressWarnings("deprecation")
	public List<IBaseResource> search(TokenParam active, ReferenceParam basedOn, DateRangeParam created,
			ReferenceParam device, ReferenceParam encounter, TokenParam identifier, TokenParam modality,
			ReferenceParam operator, ReferenceParam patient, TokenParam site, TokenParam status, ReferenceParam subject,
			TokenParam type, TokenParam view,
			// COMMON PARAMS
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, StringParam _page, String sortParam, Integer count,
			Set<Include> includes) {
		List<IBaseResource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(active, basedOn, created, device, encounter, identifier, modality,
				operator, patient, site, status, subject, type, view,
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
		List<MediaEntity> lst = mongo.find(query, MediaEntity.class);
		if (lst != null) {
			for (MediaEntity item : lst) {
				Media obj = transform(item);
				// add more Resource as it's references
				if (includes != null && includes.size() > 0 && includes.contains(new Include("*"))) {
					if (obj.getSubject() != null) {
						Resource nested = DatabaseUtil.getResourceFromReference(obj.getSubject());
						if (nested != null) {
							obj.getSubject().setResource(nested);
//							if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//								resources.add(nested);
						}
					}
					if (obj.getEncounter() != null) {
						Resource nested = DatabaseUtil.getResourceFromReference(obj.getEncounter());
						if (nested != null) {
							obj.getEncounter().setResource(nested);
//							if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//								resources.add(nested);
						}
					}
					if (obj.getBasedOn() != null && obj.getBasedOn().size() > 0) {
						for (Reference ref : obj.getBasedOn()) {
							Resource nested = DatabaseUtil.getResourceFromReference(ref);
							if (nested != null) {
								ref.setResource(nested);
//								if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//									resources.add(nested);
							}
						}
					}
					if (obj.getPartOf() != null && obj.getPartOf().size() > 0) {
						for (Reference ref : obj.getPartOf()) {
							Resource nested = DatabaseUtil.getResourceFromReference(ref);
							if (nested != null) {
								ref.setResource(nested);
//								if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//									resources.add(nested);
							}
						}
					}
					if (obj.hasOperator()) {
						Resource nested = DatabaseUtil.getResourceFromReference(obj.getOperator());
						if (nested != null) {
							obj.getOperator().setResource(nested);
//							if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//								resources.add(nested);
						}
					}
					if (obj.hasDevice()) {
						Resource nested = DatabaseUtil.getResourceFromReference(obj.getDevice());
						if (nested != null) {
							obj.getDevice().setResource(nested);
//							if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//								resources.add(nested);
						}
					}
				} else {
					if (includes != null && includes.size() > 0
							&& includes.contains(new Include("Media:subject")) && obj.getSubject() != null) {
						Resource nested = DatabaseUtil.getResourceFromReference(obj.getSubject());
						if (nested != null) {
							obj.getSubject().setResource(nested);
//							if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//								resources.add(nested);
						}
					}
					if (includes != null && includes.size() > 0
							&& includes.contains(new Include("Media:encounter"))
							&& obj.getEncounter() != null) {
						Resource nested = DatabaseUtil.getResourceFromReference(obj.getEncounter());
						if (nested != null) {
							obj.getEncounter().setResource(nested);
//							if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//								resources.add(nested);
						}
					}
				}
				resources.add(obj);
			}
		}
		return resources;
	}

	public long countMatchesAdvancedTotal(TokenParam active, ReferenceParam basedOn, DateRangeParam created,
			ReferenceParam device, ReferenceParam encounter, TokenParam identifier, TokenParam modality,
			ReferenceParam operator, ReferenceParam patient, TokenParam site, TokenParam status, ReferenceParam subject,
			TokenParam type, TokenParam view,
			// COMMON PARAMS
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content) {
		long total = 0;
		Criteria criteria = setParamToCriteria(active, basedOn, created, device, encounter, identifier, modality,
				operator, patient, site, status, subject, type, view,
				// COMMON PARAMS
				resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, DiagnosticReportEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(TokenParam active, ReferenceParam basedOn, DateRangeParam created,
			ReferenceParam device, ReferenceParam encounter, TokenParam identifier, TokenParam modality,
			ReferenceParam operator, ReferenceParam patient, TokenParam site, TokenParam status, ReferenceParam subject,
			TokenParam type, TokenParam view,
			// COMMON PARAMS
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content) {
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

		return criteria;
	}

}
