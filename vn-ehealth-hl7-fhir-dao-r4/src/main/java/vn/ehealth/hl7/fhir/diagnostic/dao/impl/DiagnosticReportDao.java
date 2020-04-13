package vn.ehealth.hl7.fhir.diagnostic.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.*;
import vn.ehealth.hl7.fhir.diagnostic.entity.DiagnosticReportEntity;

@Repository
public class DiagnosticReportDao extends BaseDao<DiagnosticReportEntity, DiagnosticReport> {

	@SuppressWarnings("deprecation")
	public List<IBaseResource> search(FhirContext fhirContext, ReferenceParam basedOn, TokenParam category,
			TokenParam code, ReferenceParam conetext, DateRangeParam date, TokenParam diagnosis,
			ReferenceParam encounter, TokenParam identifier, ReferenceParam image, DateRangeParam issued,
			ReferenceParam patient, ReferenceParam performer, ReferenceParam result, ReferenceParam specimen,
			TokenParam status, ReferenceParam subject,
			// COMMON
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, NumberParam _page, String sortParam, Integer count,
			Set<Include> includes) {
		List<IBaseResource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(basedOn, category, code, conetext, date, diagnosis, encounter,
				identifier, image, issued, patient, performer, result, specimen, status, subject, resid, _lastUpdated,
				_tag, _profile, _query, _security, _content);
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

		String[] keys = { "subject", "encounter", "basedOn", "performer", "resultsInterpreter", "specimen",
				"imagingStudy", "media:link" };

		var includeMap = getIncludeMap(ResourceType.DiagnosticReport, keys, includes);

		List<DiagnosticReportEntity> DiagnosticReportEntitys = mongo.find(query, DiagnosticReportEntity.class);
		if (DiagnosticReportEntitys != null) {
			for (DiagnosticReportEntity item : DiagnosticReportEntitys) {
				DiagnosticReport obj = transform(item);

				if (includeMap.get("subject") && obj.hasSubject()) {
					setReferenceResource(obj.getSubject());
				}

				if (includeMap.get("encounter") && obj.hasEncounter()) {
					setReferenceResource(obj.getEncounter());
				}

				if (includeMap.get("basedOn") && obj.hasBasedOn()) {
					setReferenceResource(obj.getBasedOn());
				}

				if (includeMap.get("performer") && obj.hasPerformer()) {
					setReferenceResource(obj.getPerformer());
				}

				if (includeMap.get("resultsInterpreter") && obj.hasResultsInterpreter()) {
					setReferenceResource(obj.getResultsInterpreter());
				}

				if (includeMap.get("specimen") && obj.hasSpecimen()) {
					setReferenceResource(obj.getSpecimen());
				}

				if (includeMap.get("imagingStudy") && obj.hasImagingStudy()) {
					setReferenceResource(obj.getImagingStudy());
				}

				if (includeMap.get("media:link") && obj.hasMedia()) {
					obj.getMedia().forEach(x -> setReferenceResource(x.getLink()));
				}

				resources.add(obj);
			}
		}
		return resources;
	}

	public long countMatchesAdvancedTotal(FhirContext fhirContext, ReferenceParam basedOn, TokenParam category,
			TokenParam code, ReferenceParam conetext, DateRangeParam date, TokenParam diagnosis,
			ReferenceParam encounter, TokenParam identifier, ReferenceParam image, DateRangeParam issued,
			ReferenceParam patient, ReferenceParam performer, ReferenceParam result, ReferenceParam specimen,
			TokenParam status, ReferenceParam subject, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
			UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
		long total = 0;
		Criteria criteria = setParamToCriteria(basedOn, category, code, conetext, date, diagnosis, encounter,
				identifier, image, issued, patient, performer, result, specimen, status, subject, resid, _lastUpdated,
				_tag, _profile, _query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, DiagnosticReportEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(ReferenceParam basedOn, TokenParam category, TokenParam code,
			ReferenceParam conetext, DateRangeParam date, TokenParam diagnosis, ReferenceParam encounter,
			TokenParam identifier, ReferenceParam image, DateRangeParam issued, ReferenceParam patient,
			ReferenceParam performer, ReferenceParam result, ReferenceParam specimen, TokenParam status,
			ReferenceParam subject, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
			TokenParam _query, TokenParam _security, StringParam _content) {
		Criteria criteria = null;
		// active
		criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
		// based-on
		if (basedOn != null) {
			if (basedOn.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("basedOn.reference").is(basedOn.getValue()),
						Criteria.where("basedOn.display").is(basedOn.getValue()));
			} else {
				String[] ref = basedOn.getValue().split("\\|");
				criteria.and("basedOn.identifier.system").is(ref[0]).and("basedOn.identifier.value").is(ref[1]);
			}
		}
		// set param default
		criteria = addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security, identifier);

		return criteria;
	}

	public List<IBaseResource> generate(@IdParam IdType theId) {
		List<IBaseResource> resources = new ArrayList<IBaseResource>();
		if (theId != null) {
			DiagnosticReport obj = read(theId);
			if (obj != null) {
				resources.add(obj);

				if (obj.hasSubject()) {
					setReferenceResource(resources, obj.getSubject());
				}

				if (obj.hasEncounter()) {
					setReferenceResource(resources, obj.getEncounter());
				}

				if (obj.hasBasedOn()) {
					setReferenceResource(resources, obj.getBasedOn());
				}

				if (obj.hasPerformer()) {
					setReferenceResource(resources, obj.getPerformer());
				}

				if (obj.hasResultsInterpreter()) {
					setReferenceResource(resources, obj.getResultsInterpreter());
				}

				if (obj.hasSpecimen()) {
					setReferenceResource(resources, obj.getSpecimen());
				}

				if (obj.hasImagingStudy()) {
					setReferenceResource(resources, obj.getImagingStudy());
				}

				if (obj.hasMedia()) {
					obj.getMedia().forEach(x -> setReferenceResource(x.getLink()));
				}
				return resources;
			}
		}
		return null;
	}

	@Override
	protected Class<? extends DomainResource> getResourceClass() {
		return DiagnosticReport.class;
	}

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return DiagnosticReportEntity.class;
	}

}
