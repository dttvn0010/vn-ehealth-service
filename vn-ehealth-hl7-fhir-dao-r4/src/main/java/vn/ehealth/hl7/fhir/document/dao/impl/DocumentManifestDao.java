package vn.ehealth.hl7.fhir.document.dao.impl;

import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.addParamDefault2Criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.DocumentManifest;
import org.hl7.fhir.r4.model.DomainResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.document.entity.DocumentManifestEntity;

@Repository
public class DocumentManifestDao extends BaseDao<DocumentManifestEntity, DocumentManifest> {

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return DocumentManifestEntity.class;
	}

	@Override
	protected Class<? extends DomainResource> getResourceClass() {
		return DocumentManifest.class;
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

		List<DocumentManifestEntity> entitys = mongo.find(query, DocumentManifestEntity.class);
		if (entitys != null && entitys.size() > 0) {
			for (DocumentManifestEntity item : entitys) {
				DocumentManifest obj = transform(item);

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
		total = mongo.count(query, DocumentManifestEntity.class);
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
}
