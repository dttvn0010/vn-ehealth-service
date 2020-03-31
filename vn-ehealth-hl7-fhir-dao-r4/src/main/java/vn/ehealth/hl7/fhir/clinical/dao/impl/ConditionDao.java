package vn.ehealth.hl7.fhir.clinical.dao.impl;

import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.getIncludeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Condition;
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
import ca.uhn.fhir.rest.param.QuantityParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.clinical.entity.ConditionEntity;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.*;

@Repository
public class ConditionDao extends BaseDao<ConditionEntity, Condition> {

	@SuppressWarnings("deprecation")
	public List<IBaseResource> search(FhirContext fhirContext, QuantityParam abatementAge, TokenParam abatementBoolean,
			DateRangeParam abatementDate, TokenParam abatementString, DateRangeParam assertedDate,
			ReferenceParam asserter, TokenParam bodySite, TokenParam category, TokenParam clinicalStatus,
			TokenParam code, ReferenceParam context, ReferenceParam Condition, TokenParam evidence,
			ReferenceParam evidenceDetail, TokenParam identifier, QuantityParam onsetAge, DateRangeParam onsetDate,
			StringParam onsetInfo, ReferenceParam patient, TokenParam severity, TokenParam stage,
			ReferenceParam subject, TokenParam verificationStatus, TokenParam resid, DateRangeParam _lastUpdated,
			TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content,
			StringParam _page, String sortParam, Integer count, Set<Include> includes) {
		List<IBaseResource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(abatementAge, abatementBoolean, abatementDate, abatementString,
				assertedDate, asserter, bodySite, category, clinicalStatus, code, context, Condition, evidence,
				evidenceDetail, identifier, onsetAge, onsetDate, onsetInfo, patient, severity, stage, subject,
				verificationStatus, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
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

		String[] keys = { "subject", "encounter", "asserter", "recorder" };

		var includeMap = getIncludeMap(ResourceType.Condition, keys, includes);

		List<ConditionEntity> conditionEntitys = mongo.find(query, ConditionEntity.class);
		if (conditionEntitys != null) {
			for (ConditionEntity item : conditionEntitys) {
				Condition obj = transform(item);

				if (includeMap.get("subject") && obj.hasSubject()) {
					setReferenceResource(obj.getSubject());
				}

				if (includeMap.get("encounter") && obj.hasEncounter()) {
					setReferenceResource(obj.getEncounter());
				}

				if (includeMap.get("asserter") && obj.hasAsserter()) {
					setReferenceResource(obj.getAsserter());
				}

				if (includeMap.get("recorder") && obj.hasRecorder()) {
					setReferenceResource(obj.getRecorder());
				}
				resources.add(obj);
			}
		}
		return resources;
	}

	public long countMatchesAdvancedTotal(FhirContext fhirContext, QuantityParam abatementAge,
			TokenParam abatementBoolean, DateRangeParam abatementDate, TokenParam abatementString,
			DateRangeParam assertedDate, ReferenceParam asserter, TokenParam bodySite, TokenParam category,
			TokenParam clinicalStatus, TokenParam code, ReferenceParam context, ReferenceParam Condition,
			TokenParam evidence, ReferenceParam evidenceDetail, TokenParam identifier, QuantityParam onsetAge,
			DateRangeParam onsetDate, StringParam onsetInfo, ReferenceParam patient, TokenParam severity,
			TokenParam stage, ReferenceParam subject, TokenParam verificationStatus, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content) {
		long total = 0;
		Criteria criteria = setParamToCriteria(abatementAge, abatementBoolean, abatementDate, abatementString,
				assertedDate, asserter, bodySite, category, clinicalStatus, code, context, Condition, evidence,
				evidenceDetail, identifier, onsetAge, onsetDate, onsetInfo, patient, severity, stage, subject,
				verificationStatus, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, ConditionEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(QuantityParam abatementAge, TokenParam abatementBoolean,
			DateRangeParam abatementDate, TokenParam abatementString, DateRangeParam assertedDate,
			ReferenceParam asserter, TokenParam bodySite, TokenParam category, TokenParam clinicalStatus,
			TokenParam code, ReferenceParam context, ReferenceParam encounter, TokenParam evidence,
			ReferenceParam evidenceDetail, TokenParam identifier, QuantityParam onsetAge, DateRangeParam onsetDate,
			StringParam onsetInfo, ReferenceParam patient, TokenParam severity, TokenParam stage,
			ReferenceParam subject, TokenParam verificationStatus, TokenParam resid, DateRangeParam _lastUpdated,
			TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
		Criteria criteria = null;
		// active
		criteria = Criteria.where("active").is(true);
		// set param default
		criteria = addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security, identifier);
		// abatementAge
		if (abatementAge != null) {
			criteria = setQuantityToCriteria(criteria, "abatement", abatementAge);
		}
		// abatement-boolean
		if (abatementBoolean != null) {
			criteria.and("abatement.myStringValue").is(abatementBoolean.getValue());
		}
		// abatement-date
		if (abatementDate != null) {
			criteria = setDatetimePeriodToCriteria(criteria, "abatement", abatementDate);
		}
		// abatement-string
		if (abatementString != null) {
			criteria.and("abatement.myStringValue").is(abatementString.getValue());
		}
		// asserted-date
		if (assertedDate != null) {
			criteria = setTypeDateToCriteria(criteria, "assertedDate", assertedDate);
		}
		// asserter
		if (asserter != null) {
			if (asserter.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("asserter.reference").is(asserter.getValue()),
						Criteria.where("asserter.display").is(asserter.getValue()));
			} else {
				String[] ref = asserter.getValue().split("\\|");
				criteria.and("asserter.identifier.system").is(ref[0]).and("asserter.identifier.value").is(ref[1]);
			}
		}
		// body-site
		if (bodySite != null) {
			criteria.and("bodySite.coding.code.myStringValue").is(bodySite.getValue());
		}
		// category
		if (category != null) {
			criteria.and("category.coding.system").is(category.getSystem()).and("category.coding.code")
					.is(category.getValue());
		}
		// clinical-status
		if (clinicalStatus != null && !clinicalStatus.isEmpty()) {
			criteria.and("clinicalStatus").is(category.getValue());
		}
		// code
		if (code != null) {
			criteria.and("code.coding.code.myStringValue").is(code.getValue());
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
		// encounter
		if (encounter != null) {
			if (encounter.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("context.reference").is(encounter.getValue()),
						Criteria.where("context.display").is(encounter.getValue()));
			} else {
				String[] ref = encounter.getValue().split("\\|");
				criteria.and("context.identifier.system").is(ref[0]).and("context.identifier.value").is(ref[1]);
			}
		}
		// evidence
		if (evidence != null) {
			criteria.and("evidence.code.coding.code.myStringValue").is(evidence.getValue());
		}
		// evidence-detail
		if (evidenceDetail != null) {
			if (evidenceDetail.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("evidence.detail.reference").is(evidenceDetail.getValue()),
						Criteria.where("evidence.detail.display").is(evidenceDetail.getValue()));
			} else {
				String[] ref = evidenceDetail.getValue().split("\\|");
				criteria.and("evidence.detail.identifier.system").is(ref[0]).and("evidence.detail.identifier.value")
						.is(ref[1]);
			}
		}
		// onset-age
		if (onsetAge != null) {
			criteria = setQuantityToCriteria(criteria, "onset", onsetAge);
		}
		// onset-date
		if (onsetDate != null) {
			criteria = setDatetimePeriodToCriteria(criteria, "onset", onsetDate);
		}
		// onset-info
		if (onsetInfo != null) {
			criteria.and("onset.myStringValue").is(onsetInfo.getValue());
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
		// severity
		if (severity != null) {
			criteria.and("severity.coding.code.myStringValue").is(severity.getValue());
		}
		// stage
		if (stage != null) {
			criteria.and("stage.summary.coding.code.myStringValue").is(severity.getValue());
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
		// verification-status
		if (verificationStatus != null) {
			criteria.and("verificationStatus").is(verificationStatus.getValue());
		}
		return criteria;
	}

	@Override
	protected String getProfile() {
		return "Condition-v1.0";
	}

	@Override
    public Class<? extends DomainResource> getResourceClass() {
        return Condition.class;
    }
	
	@Override
	public Class<? extends BaseResource> getEntityClass() {
		return ConditionEntity.class;
	}
}
