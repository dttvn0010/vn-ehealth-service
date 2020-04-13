package vn.ehealth.hl7.fhir.clinical.dao.impl;

import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.getIncludeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.DetectedIssue;
import org.hl7.fhir.r4.model.DomainResource;
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
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.clinical.entity.DetectedIssueEntity;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.*;

@Repository
public class DetectedIssueDao extends BaseDao<DetectedIssueEntity, DetectedIssue> {

	@SuppressWarnings("deprecation")
	public List<IBaseResource> search(FhirContext fhirContext, ReferenceParam author, TokenParam category,
			DateRangeParam date, TokenParam identifier, ReferenceParam implicated, ReferenceParam patient,
			// COMMON
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, NumberParam _page, String sortParam, Integer count,
			Set<Include> includes) {
		List<IBaseResource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(author, category, date, identifier, implicated, patient, resid,
				_lastUpdated, _tag, _profile, _query, _security, _content);
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

		String[] keys = { "patient", "author" };

		var includeMap = getIncludeMap(ResourceType.DetectedIssue, keys, includes);

		List<DetectedIssueEntity> detectedIssueEntitys = mongo.find(query, DetectedIssueEntity.class);
		if (detectedIssueEntitys != null) {
			for (DetectedIssueEntity item : detectedIssueEntitys) {
				DetectedIssue obj = transform(item);
				if (includeMap.get("patient") && obj.hasPatient()) {
					setReferenceResource(obj.getPatient());
				}

				if (includeMap.get("author") && obj.hasAuthor()) {
					setReferenceResource(obj.getAuthor());
				}
				resources.add(obj);
			}
		}
		return resources;
	}

	public long countMatchesAdvancedTotal(FhirContext fhirContext, ReferenceParam author, TokenParam category,
			DateRangeParam date, TokenParam identifier, ReferenceParam implicated, ReferenceParam patient,
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content) {
		long total = 0;
		Criteria criteria = setParamToCriteria(author, category, date, identifier, implicated, patient, resid,
				_lastUpdated, _tag, _profile, _query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, DetectedIssueEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(ReferenceParam author, TokenParam category, DateRangeParam date,
			TokenParam identifier, ReferenceParam implicated, ReferenceParam patient, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content) {
		Criteria criteria = null;
		// active
		criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
		// set param default
		criteria = addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security, identifier);
		// author
		if (author != null) {
			if (author.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("author.reference").is(author.getValue()),
						Criteria.where("author.display").is(author.getValue()));
			} else {
				String[] ref = author.getValue().split("\\|");
				criteria.and("author.identifier.system").is(ref[0]).and("author.identifier.value").is(ref[1]);
			}
		}
		// category
		if (category != null && !category.isEmpty()) {
			criteria.and("category.coding.system").is(category.getSystem()).and("category.coding.code")
					.is(category.getValue());
		}
		// date
		if (date != null) {
			criteria = setTypeDateToCriteria(criteria, "date", date);
		}
		// implicated
		if (implicated != null) {
			if (implicated.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("implicated.reference").is(implicated.getValue()),
						Criteria.where("implicated.display").is(implicated.getValue()));
			} else {
				String[] ref = implicated.getValue().split("\\|");
				criteria.and("implicated.identifier.system").is(ref[0]).and("implicated.identifier.value").is(ref[1]);
			}
		}
		// patient
		if (patient != null) {
			if (patient.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("subject.reference").is(patient.getValue()),
						Criteria.where("subject.display").is(patient.getValue()));
			} else {
				String[] ref = patient.getValue().split("\\|");
				criteria.and("subject.identifier.system").is(ref[0]).and("subject.identifier.value").is(ref[1]);
			}
		}
		return criteria;
	}

	@Override
	protected List<String> getProfile() {
		return null;
	}

	@Override
	protected Class<? extends DomainResource> getResourceClass() {
		return DetectedIssue.class;
	}

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return DetectedIssueEntity.class;
	}

}
