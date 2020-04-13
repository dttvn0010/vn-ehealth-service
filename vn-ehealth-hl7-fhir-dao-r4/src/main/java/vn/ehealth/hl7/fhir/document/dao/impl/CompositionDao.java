package vn.ehealth.hl7.fhir.document.dao.impl;

import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.addParamDefault2Criteria;
import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.setReferenceResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Composition.SectionComponent;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.document.entity.CompositionEntity;

@Repository
public class CompositionDao extends BaseDao<CompositionEntity, Composition> {

	@Override
	protected List<String> getProfile() {
		return null;
	}

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return CompositionEntity.class;
	}

	@Override
	protected Class<? extends DomainResource> getResourceClass() {
		return Composition.class;
	}

	@SuppressWarnings("deprecation")
	public List<IBaseResource> search(
			// COMMON PARAMS
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, NumberParam _page, String sortParam, Integer count,
			Set<Include> includes) {
		List<IBaseResource> resources = new ArrayList<IBaseResource>();
		// TODO: add params for search
		Criteria criteria = setParamToCriteria(
				// COMMON PARAMS
				resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
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

//		String[] keys = { "" };
//
//		var includeMap = getIncludeMap(ResourceType.Bundle, keys, includes);

		List<CompositionEntity> entitys = mongo.find(query, CompositionEntity.class);
		if (entitys != null && entitys.size() > 0) {
			for (CompositionEntity item : entitys) {
				Composition obj = transform(item);

				resources.add(obj);
			}
		}
		return resources;
	}

	public long countMatchesAdvancedTotal(
			// COMMON PARAMS
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content) {
		long total = 0;
		Criteria criteria = setParamToCriteria(
				// COMMON PARAMS
				resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, CompositionEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(
			// COMMON PARAMS
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content) {
		Criteria criteria = null;
		// active
		criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
		// set param default
		criteria = addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security, null);

		return criteria;
	}

	public List<IBaseResource> generate(@IdParam IdType theId) {
		List<IBaseResource> resources = new ArrayList<IBaseResource>();
		if (theId != null) {
			Composition obj = read(theId);
			if (obj != null) {
				resources.add(obj);
				// Patient
				if (obj.hasSubject()) {
					setReferenceResource(resources, obj.getSubject());
				}
				// Encounter
				if (obj.hasEncounter()) {
					setReferenceResource(resources, obj.getEncounter());
				}
				// Author
				if (obj.hasAuthor()) {
					setReferenceResource(resources, obj.getAuthor());
				}
				// Attest.party
				if (obj.hasAttester()) {
					obj.getAttester().forEach(x -> setReferenceResource(x.getParty()));
				}
				// Custodian
				if (obj.hasCustodian()) {
					setReferenceResource(resources, obj.getCustodian());
				}
				// RelatedTo.target
				if (obj.hasRelatesTo()) {
					obj.getRelatesTo().forEach(x -> setReferenceResource(x.getTargetReference()));
				}
				// Event.detail
				if (obj.hasEvent()) {
					obj.getEvent().forEach(x -> setReferenceResource(x.getDetail()));
				}
				// Section
				if (obj.hasSection()) {
					for (SectionComponent item : obj.getSection()) {
						mapSection(resources, item);
					}
				}

				return resources;
			}
		}
		return null;
	}

	private static void mapSection(List<IBaseResource> lst, SectionComponent section) {
		if (section != null) {
			if (section.hasAuthor()) {
				setReferenceResource(lst, section.getAuthor());
			}
			if (section.hasFocus()) {
				setReferenceResource(lst, section.getFocus());
			}
			if (section.hasEntry()) {
				setReferenceResource(lst, section.getEntry());
			}
			if (section.hasSection()) {
				for (SectionComponent item : section.getSection()) {
					mapSection(lst, item);
				}
			}
		}
	}
}
