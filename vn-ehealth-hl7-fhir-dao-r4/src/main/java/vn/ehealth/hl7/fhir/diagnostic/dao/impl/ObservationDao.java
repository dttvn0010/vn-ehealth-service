package vn.ehealth.hl7.fhir.diagnostic.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Observation;
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
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.diagnostic.entity.ObservationEntity;
import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.*;

@Repository
public class ObservationDao extends BaseDao<ObservationEntity, Observation> {

	@SuppressWarnings("deprecation")
	public List<IBaseResource> search(FhirContext fhirContext, ReferenceParam basedOn, TokenParam category,
			TokenOrListParam code, TokenParam comboCode, TokenParam comboDataAbsentReason, TokenParam comboValueConcept,
			TokenParam componentCode, TokenParam componentDataAbsentReason, TokenParam componentValueConcept,
			ReferenceParam conetext, TokenParam dataAbsentReason, DateRangeParam date, ReferenceParam device,
			ReferenceParam encounter, TokenParam identifier, TokenParam method, ReferenceParam patient,
			ReferenceParam performer, ReferenceParam relatedTarget, TokenParam relatedType, ReferenceParam specimen,
			TokenParam status, ReferenceParam subject, TokenParam valueConcept, DateRangeParam valueDate,
			StringParam valueString,
			// COMMON
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, NumberParam _page, String sortParam, Integer count,
			Set<Include> includes) {
		List<IBaseResource> resources = new ArrayList<IBaseResource>();

		Criteria criteria = setParamToCriteria(basedOn, category, code, comboCode, comboDataAbsentReason,
				comboValueConcept, componentCode, componentDataAbsentReason, componentValueConcept, conetext,
				dataAbsentReason, date, device, encounter, identifier, method, patient, performer, relatedTarget,
				relatedType, specimen, status, subject, valueConcept, valueDate, valueString, resid, _lastUpdated, _tag,
				_profile, _query, _security, _content);
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

		String[] keys = { "subject", "encounter", "basedOn", "device", "hasMember", "specimen" };

		var includeMap = getIncludeMap(ResourceType.Observation, keys, includes);

		List<ObservationEntity> ObservationEntitys = mongo.find(query, ObservationEntity.class);
		if (ObservationEntitys != null) {
			for (ObservationEntity item : ObservationEntitys) {
				Observation obj = transform(item);

				if (includeMap.get("subject") && obj.hasSubject()) {
					setReferenceResource(obj.getSubject());
				}

				if (includeMap.get("encounter") && obj.hasEncounter()) {
					setReferenceResource(obj.getEncounter());
				}

				if (includeMap.get("basedOn") && obj.hasBasedOn()) {
					setReferenceResource(obj.getBasedOn());
				}

				if (includeMap.get("device") && obj.hasDevice()) {
					setReferenceResource(obj.getDevice());
				}
				if (includeMap.get("hasMember") && obj.hasHasMember()) {
					setReferenceResource(obj.getHasMember());
				}

				if (includeMap.get("specimen") && obj.hasSpecimen()) {
					setReferenceResource(obj.getSpecimen());
				}

				resources.add(obj);
			}
		}
		return resources;
	}

	public long countMatchesAdvancedTotal(FhirContext fhirContext, ReferenceParam basedOn, TokenParam category,
			TokenOrListParam code, TokenParam comboCode, TokenParam comboDataAbsentReason, TokenParam comboValueConcept,
			TokenParam componentCode, TokenParam componentDataAbsentReason, TokenParam componentValueConcept,
			ReferenceParam conetext, TokenParam dataAbsentReason, DateRangeParam date, ReferenceParam device,
			ReferenceParam encounter, TokenParam identifier, TokenParam method, ReferenceParam patient,
			ReferenceParam performer, ReferenceParam relatedTarget, TokenParam relatedType, ReferenceParam specimen,
			TokenParam status, ReferenceParam subject, TokenParam valueConcept, DateRangeParam valueDate,
			StringParam valueString, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
			TokenParam _query, TokenParam _security, StringParam _content) {
		long total = 0;
		Criteria criteria = setParamToCriteria(basedOn, category, code, comboCode, comboDataAbsentReason,
				comboValueConcept, componentCode, componentDataAbsentReason, componentValueConcept, conetext,
				dataAbsentReason, date, device, encounter, identifier, method, patient, performer, relatedTarget,
				relatedType, specimen, status, subject, valueConcept, valueDate, valueString, resid, _lastUpdated, _tag,
				_profile, _query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, ObservationEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(ReferenceParam basedOn, TokenParam category, TokenOrListParam codeList,
			TokenParam comboCode, TokenParam comboDataAbsentReason, TokenParam comboValueConcept,
			TokenParam componentCode, TokenParam componentDataAbsentReason, TokenParam componentValueConcept,
			ReferenceParam conetext, TokenParam dataAbsentReason, DateRangeParam date, ReferenceParam device,
			ReferenceParam encounter, TokenParam identifier, TokenParam method, ReferenceParam patient,
			ReferenceParam performer, ReferenceParam relatedTarget, TokenParam relatedType, ReferenceParam specimen,
			TokenParam status, ReferenceParam subject, TokenParam valueConcept, DateRangeParam valueDate,
			StringParam valueString, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
			TokenParam _query, TokenParam _security, StringParam _content) {
		Criteria criteria = null;
		// active
		criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
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
		// patient
		if (patient != null) {
			if (patient.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("subject.reference").is("Patient/" + patient.getValue()),
						Criteria.where("subject.display").is(patient.getValue()));
			} else {
				String[] ref = patient.getValue().split("\\|");
				criteria.and("subject.identifier.system").is(ref[0]).and("subject.identifier.value").is(ref[1]);
			}
		}
		// basedOn
		if (basedOn != null) {
			if (basedOn.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("basedOn.reference").is(basedOn.getValue()),
						Criteria.where("basedOn.display").is(basedOn.getValue()));
			} else {
				String[] ref = basedOn.getValue().split("\\|");
				criteria.and("basedOn.identifier.system").is(ref[0]).and("basedOn.identifier.value").is(ref[1]);
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
		// category
		if (category != null) {
			if (!StringUtils.isBlank(category.getSystem()) && !StringUtils.isBlank(category.getValue())) {
				criteria.and("category.coding.system").is(category.getSystem()).and("category.coding.code")
						.is(category.getValue());
			} else if (!StringUtils.isBlank(category.getSystem()) && StringUtils.isBlank(category.getValue())) {
				criteria.and("category.coding.system").is(category.getSystem());
			} else if (StringUtils.isBlank(category.getSystem()) && !StringUtils.isBlank(category.getValue())) {
				criteria.and("category.coding.code").is(category.getValue());
			}
		}
		if (codeList != null) {
			setCodeListToCriteria(criteria, codeList);
		}
		// set param default
		criteria = addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security, identifier);

		return criteria;
	}

	@Override
	protected String getProfile() {
		return "Observation-v1.0";
	}

	@Override
	protected Class<? extends DomainResource> getResourceClass() {
		return Observation.class;
	}

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return ObservationEntity.class;
	}

}
