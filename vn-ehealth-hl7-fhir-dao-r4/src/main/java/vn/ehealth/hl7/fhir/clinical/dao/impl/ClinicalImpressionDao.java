package vn.ehealth.hl7.fhir.clinical.dao.impl;

import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.getIncludeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.ClinicalImpression;
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
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.clinical.entity.ClinicalImpressionEntity;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.*;

@Repository
public class ClinicalImpressionDao extends BaseDao<ClinicalImpressionEntity, ClinicalImpression> {

	@SuppressWarnings("deprecation")
	public List<IBaseResource> search(FhirContext fhirContext, ReferenceParam action, ReferenceParam assessor,
			ReferenceParam context, DateRangeParam date, TokenParam findingCode, ReferenceParam findingRef,
			TokenParam identifier, ReferenceParam investigation, ReferenceParam patient, ReferenceParam previous,
			ReferenceParam problem, TokenParam status, ReferenceParam subject,
			// COMMON
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, NumberParam _page, String sortParam, Integer count,
			Set<Include> includes) {
		List<IBaseResource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(action, assessor, context, date, findingCode, findingRef, identifier,
				investigation, patient, previous, problem, status, subject, resid, _lastUpdated, _tag, _profile, _query,
				_security, _content);
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

		String[] keys = { "subject", "encounter", "assessor", "problem", "prognosisReference" };

		var includeMap = getIncludeMap(ResourceType.ClinicalImpression, keys, includes);

		List<ClinicalImpressionEntity> clinicalImpressionEntitys = mongo.find(query, ClinicalImpressionEntity.class);
		if (clinicalImpressionEntitys != null) {
			for (ClinicalImpressionEntity item : clinicalImpressionEntitys) {
				ClinicalImpression obj = transform(item);

				if (includeMap.get("subject") && obj.hasSubject()) {
					setReferenceResource(obj.getSubject());
				}

				if (includeMap.get("encounter") && obj.hasEncounter()) {
					setReferenceResource(obj.getEncounter());
				}

				if (includeMap.get("assessor") && obj.hasAssessor()) {
					setReferenceResource(obj.getAssessor());
				}

				if (includeMap.get("problem") && obj.hasProblem()) {
					setReferenceResource(obj.getProblem());
				}

				if (includeMap.get("prognosisReference") && obj.hasPrognosisReference()) {
					setReferenceResource(obj.getPrognosisReference());
				}
				resources.add(obj);
			}
		}
		return resources;
	}

	public long countMatchesAdvancedTotal(FhirContext fhirContext, ReferenceParam action, ReferenceParam assessor,
			ReferenceParam context, DateRangeParam date, TokenParam findingCode, ReferenceParam findingRef,
			TokenParam identifier, ReferenceParam investigation, ReferenceParam patient, ReferenceParam previous,
			ReferenceParam problem, TokenParam status, ReferenceParam subject, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content) {
		long total = 0;
		Criteria criteria = setParamToCriteria(action, assessor, context, date, findingCode, findingRef, identifier,
				investigation, patient, previous, problem, status, subject, resid, _lastUpdated, _tag, _profile, _query,
				_security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, ClinicalImpressionEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(ReferenceParam action, ReferenceParam assessor, ReferenceParam context,
			DateRangeParam date, TokenParam findingCode, ReferenceParam findingRef, TokenParam identifier,
			ReferenceParam investigation, ReferenceParam patient, ReferenceParam previous, ReferenceParam problem,
			TokenParam status, ReferenceParam subject, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
			UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
		Criteria criteria = null;
		// active
		criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
		// set param default
		criteria = addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security, identifier);
		// action
		if (action != null) {
			if (action.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("action.reference").is(action.getValue()),
						Criteria.where("action.display").is(action.getValue()));
			} else {
				String[] ref = action.getValue().split("\\|");
				criteria.and("action.identifier.system").is(ref[0]).and("action.identifier.value").is(ref[1]);
			}
		}
		// assessor
		if (assessor != null) {
			if (assessor.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("assessor.reference").is(assessor.getValue()),
						Criteria.where("assessor.display").is(assessor.getValue()));
			} else {
				String[] ref = assessor.getValue().split("\\|");
				criteria.and("assessor.identifier.system").is(ref[0]).and("assessor.identifier.value").is(ref[1]);
			}
		}
		// context
		if (context != null) {
			if (context.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("context.reference").is(context.getValue()),
						Criteria.where("context.display").is(context.getValue()));
			} else {
				String[] ref = context.getValue().split("\\|");
				criteria.and("context.identifier.system").is(ref[0]).and("context.identifier.value").is(ref[1]);
			}
		}
		// date
		if (date != null) {
			criteria = setTypeDateToCriteria(criteria, "date", date);
		}
		// finding-code
		if (findingCode != null) {
			criteria.and("finding.item.coding.system").is(findingCode.getSystem()).and("finding.item.coding.code")
					.is(findingCode.getValue());
		}
		// finding-ref
		if (findingRef != null) {
			if (findingRef.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("finding.reference").is(findingRef.getValue()),
						Criteria.where("finding.display").is(findingRef.getValue()));
			} else {
				String[] ref = findingRef.getValue().split("\\|");
				criteria.and("finding.identifier.system").is(ref[0]).and("finding.identifier.value").is(ref[1]);
			}
		}
		// investigation
		if (investigation != null) {
			if (investigation.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("investigation.reference").is(investigation.getValue()),
						Criteria.where("investigation.display").is(investigation.getValue()));
			} else {
				String[] ref = investigation.getValue().split("\\|");
				criteria.and("investigation.identifier.system").is(ref[0]).and("investigation.identifier.value")
						.is(ref[1]);
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
		// previous
		if (previous != null) {
			if (previous.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("previous.reference").is(previous.getValue()),
						Criteria.where("previous.display").is(previous.getValue()));
			} else {
				String[] ref = previous.getValue().split("\\|");
				criteria.and("previous.identifier.system").is(ref[0]).and("previous.identifier.value").is(ref[1]);
			}
		}
		// problem
		if (problem != null) {
			if (problem.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("problem.reference").is(problem.getValue()),
						Criteria.where("problem.display").is(problem.getValue()));
			} else {
				String[] ref = problem.getValue().split("\\|");
				criteria.and("problem.identifier.system").is(ref[0]).and("problem.identifier.value").is(ref[1]);
			}
		}
		// status
		if (status != null) {
			criteria.and("status").is(status.getValue());
		}
		// subject
		if (subject != null) {
			if (subject.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("subject.reference").is(subject.getValue()),
						Criteria.where("subject.display").is(subject.getValue()));
			} else {
				String[] ref = subject.getValue().split("\\|");
				criteria.and("subject.identifier.system").is(ref[0]).and("subject.identifier.value").is(ref[1]);
			}
		}

		return criteria;
	}

	public ClinicalImpression findNotCache(FhirContext fhirContext, IdType idType) {
		if (idType != null) {
			ObjectId objectId = new ObjectId(idType.getIdPart());
			Query query = Query.query(Criteria.where(ConstantKeys.QP_FHIRID).is(objectId));
			ClinicalImpressionEntity entity = mongo.findOne(query, ClinicalImpressionEntity.class);
			if (entity != null) {
				return transform(entity);
			}
		}
		return null;
	}

	@Override
	protected Class<? extends DomainResource> getResourceClass() {
		return ClinicalImpression.class;
	}

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return ClinicalImpressionEntity.class;
	}

}
