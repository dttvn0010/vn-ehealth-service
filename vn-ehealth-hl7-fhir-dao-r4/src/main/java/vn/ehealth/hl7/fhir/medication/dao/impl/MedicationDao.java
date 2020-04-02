package vn.ehealth.hl7.fhir.medication.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.medication.entity.MedicationEntity;

@Repository
public class MedicationDao extends BaseDao<MedicationEntity, Medication> {

	@SuppressWarnings("deprecation")
	public List<Resource> search(FhirContext fhirContext, TokenParam code, TokenParam container, TokenParam form,
			ReferenceParam ingredient, TokenParam ingredientCode, ReferenceParam manufacturer,
			TokenParam overTheCounter, ReferenceParam packageItem, TokenParam packageItemCode, TokenParam status,
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, TokenParam hospital, StringParam productName,
			StringParam medicationType, StringParam _page, String sortParam, Integer count) {
		List<Resource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(code, container, form, ingredient, ingredientCode, manufacturer,
				overTheCounter, packageItem, packageItemCode, status, resid, _lastUpdated, _tag, _profile, _query,
				_security, _content, hospital, productName, medicationType);
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
			query.with(new Sort(Sort.Direction.DESC, ConstantKeys.QP_UPDATED));
			query.with(new Sort(Sort.Direction.DESC, ConstantKeys.QP_CREATED));
		}
		List<MedicationEntity> medicationEntitys = mongo.find(query, MedicationEntity.class);
		if (medicationEntitys != null) {
			for (MedicationEntity item : medicationEntitys) {
				Medication medication = transform(item);
				resources.add(medication);
			}
		}
		return resources;
	}

	public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam code, TokenParam container,
			TokenParam form, ReferenceParam ingredient, TokenParam ingredientCode, ReferenceParam manufacturer,
			TokenParam overTheCounter, ReferenceParam packageItem, TokenParam packageItemCode, TokenParam status,
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, TokenParam hospital, StringParam productName,
			StringParam medicationType) {
		long total = 0;
		Criteria criteria = setParamToCriteria(code, container, form, ingredient, ingredientCode, manufacturer,
				overTheCounter, packageItem, packageItemCode, status, resid, _lastUpdated, _tag, _profile, _query,
				_security, _content, hospital, productName, medicationType);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, MedicationEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(TokenParam code, TokenParam container, TokenParam form,
			ReferenceParam ingredient, TokenParam ingredientCode, ReferenceParam manufacturer,
			TokenParam overTheCounter, ReferenceParam packageItem, TokenParam packageItemCode, TokenParam status,
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, TokenParam hospital, StringParam productName,
			StringParam medicationType) {
		Criteria criteria = null;
		// active
		criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
		// set param default
		criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
				null);
		// code
		if (code != null) {
			if (!StringUtils.isBlank(code.getSystem()) && !StringUtils.isBlank(code.getValue())) {
				criteria.and("code.coding.system.myStringValue").is(code.getSystem())
						.and("code.coding.code.myStringValue").regex(code.getValue(), "i");
			} else if (!StringUtils.isBlank(code.getSystem()) && StringUtils.isBlank(code.getValue())) {
				criteria.and("code.coding.system.myStringValue").is(code.getSystem());
			} else if (StringUtils.isBlank(code.getSystem()) && !StringUtils.isBlank(code.getValue())) {
				criteria.and("code.coding.code.myStringValue").regex(code.getValue(), "i");
			}
		}
		// container
		if (container != null) {
			/** not write **/
		}
		// form
		if (form != null) {
			criteria.and("form.text.myStringValue").regex(form.getValue(), "i");
		}
		// ingredient
		if (ingredient != null) {
			/** not write **/
		}
		// ingredient-code
		if (ingredientCode != null) {
			/** not write **/
		}
		// manufacturer
		if (manufacturer != null) {
			if (manufacturer.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("manufacturer.reference").is(manufacturer.getValue()),
						Criteria.where("manufacturer.display").is(manufacturer.getValue()));
			} else {
				String[] ref = manufacturer.getValue().split("\\|");
				criteria.and("manufacturer.identifier.system").is(ref[0]).and("manufacturer.identifier.value")
						.is(ref[1]);
			}
		}
		// over-the-counte
		if (overTheCounter != null) {

		}
		// package-item
		if (packageItem != null) {

		}
		// package-item-code
		if (packageItemCode != null) {

		}
		// status
		if (status != null) {
			criteria.and("status").is(status.getValue());
		}
		if (hospital != null) {
			criteria.and("extension.url.myStringValue")
					.is("https://fhir.yte360.com/StructureDefinition/Extension-ManagingOrgMedication-v1.0");
			if (hospital.getValue().indexOf("|") == -1) {
				criteria.orOperator(
						Criteria.where("extension.value.reference.myStringValue").regex(hospital.getValue()),
						Criteria.where("extension.value.display").regex(hospital.getValue()));
			} else {
				String[] ref = hospital.getValue().split("\\|");
				criteria.and("extension.value.identifier.system.myStringValue").is(ref[0])
						.and("extension.value.identifier.value.myStringValue").is(ref[1]);
			}
		}
		if (productName != null) {
			criteria.and("extension.url.myStringValue")
					.is("https://fhir.yte360.com/StructureDefinition/Extension-Medication-Production-Name-v1.0")
					.and("extension.value.myStringValue").regex("^" + productName.getValue(), "i");

		}
		if (medicationType != null) {
			criteria.and("extension.url.myStringValue")
					.is("https://fhir.yte360.com/StructureDefinition/Extension-Medication-Type-v1.0")
					.and("extension.value.coding.code.myStringValue").regex("^" + medicationType.getValue(), "i");

		}
		return criteria;
	}

	@Override
	protected String getProfile() {
		return "Medication-v1.0";
	}

	@Override
    protected Class<? extends DomainResource> getResourceClass() {
        return Medication.class;
    }

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return MedicationEntity.class;
	}
}
