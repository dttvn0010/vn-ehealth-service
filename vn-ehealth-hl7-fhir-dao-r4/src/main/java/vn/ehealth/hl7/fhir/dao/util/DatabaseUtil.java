package vn.ehealth.hl7.fhir.dao.util;

import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;

import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.QuantityParam;
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;

public class DatabaseUtil {
	private static final Logger log = LoggerFactory.getLogger(DatabaseUtil.class);

	public static Criteria setTypeDateToCriteria(Criteria criteria, String keySearch, DateRangeParam dateValueParam) {
		DateParam dateLowerParam = dateValueParam.getLowerBound();
		DateParam dateUpperParam = dateValueParam.getUpperBound();

		if (dateLowerParam != null && dateUpperParam != null) {
			switch (dateLowerParam.getPrefix()) {
			case GREATERTHAN: {
				switch (dateUpperParam.getPrefix()) {
				case LESSTHAN_OR_EQUALS: {
					criteria.and(keySearch).gt(dateLowerParam.getValue()).lte(dateUpperParam.getValue());
					break;
				}
				case LESSTHAN: {
					criteria.and(keySearch).gt(dateLowerParam.getValue()).lt(dateUpperParam.getValue());
					break;
				}
				default:
					log.trace("DEFAULT DATE(0) Prefix = " + dateValueParam.getValuesAsQueryTokens().get(0).getPrefix());
				}
				break;
			}
			case GREATERTHAN_OR_EQUALS: {
				switch (dateUpperParam.getPrefix()) {
				case LESSTHAN_OR_EQUALS: {
					criteria.and(keySearch).gte(dateLowerParam.getValue()).lte(dateUpperParam.getValue());
					break;
				}
				case LESSTHAN: {
					criteria.and(keySearch).gte(dateLowerParam.getValue()).lt(dateUpperParam.getValue());
					break;
				}
				default:
					log.trace("DEFAULT DATE(0) Prefix = " + dateValueParam.getValuesAsQueryTokens().get(0).getPrefix());
				}
				break;
			}
			case EQUAL: {
				criteria.and(keySearch).is(dateLowerParam.getValue());
				break;
			}
			case NOT_EQUAL: {
				criteria.and(keySearch).ne(dateLowerParam.getValue());
				break;
			}
			default:
			}
		} else if (dateLowerParam != null && dateUpperParam == null) {
			switch (dateLowerParam.getPrefix()) {
			case GREATERTHAN: {
				criteria.and(keySearch).gt(dateLowerParam.getValue());
				break;
			}
			case GREATERTHAN_OR_EQUALS: {
				criteria.and(keySearch).gte(dateLowerParam.getValue());
				break;
			}
			case APPROXIMATE:
			case EQUAL: {
				criteria.and(keySearch).is(dateLowerParam.getValue());
				break;
			}
			case NOT_EQUAL: {
				criteria.and(keySearch).ne(dateLowerParam.getValue());
				break;
			}
			case STARTS_AFTER:
			default:
			}
		} else if (dateLowerParam == null && dateUpperParam != null) {
			switch (dateUpperParam.getPrefix()) {
			case APPROXIMATE:
			case EQUAL: {
				criteria.and(keySearch).is(dateUpperParam.getValue());
				break;
			}

			case LESSTHAN_OR_EQUALS: {
				criteria.and(keySearch).lte(dateUpperParam.getValue());
				break;
			}
			case ENDS_BEFORE:
			case LESSTHAN: {
				criteria.and(keySearch).lt(dateUpperParam.getValue());
				break;
			}
			default:
				log.trace("DEFAULT DATE(0) Prefix = " + dateValueParam.getValuesAsQueryTokens().get(0).getPrefix());
			}
		}
		return criteria;
	}

	public static Criteria addParamDefault2Criteria(Criteria criteria, TokenParam resid, DateRangeParam _lastUpdated,
			TokenParam _tag, UriParam _profile, TokenParam _security, TokenParam identifier) {
		if (resid != null) {
			criteria.and(ConstantKeys.SP_FHIR_ID).regex(resid.getValue());
		}
		if (_tag != null) {
			criteria.and("tag.code.myStringValue").regex(_tag.getValue());
		}
		if (_profile != null) {
			criteria.and("profile.myStringValue").regex(_profile.getValue());
		}
		if (_security != null) {
			criteria.and("security.code.myStringValue").regex(_security.getValue());
		}
		if (_lastUpdated != null) {
			criteria = setTypeDateToCriteria(criteria, "resCreated", _lastUpdated);
		}
		if (identifier != null) {
			if (!StringUtils.isBlank(identifier.getSystem()) && !StringUtils.isBlank(identifier.getValue())) {
				criteria.and("identifier.system").is(identifier.getSystem()).and("identifier.value")
						.is(identifier.getValue());
			} else if (!StringUtils.isBlank(identifier.getSystem()) && StringUtils.isBlank(identifier.getValue())) {
				criteria.and("identifier.system").is(identifier.getSystem());
			} else if (StringUtils.isBlank(identifier.getSystem()) && !StringUtils.isBlank(identifier.getValue())) {
				criteria.and("identifier.value").is(identifier.getValue());
			}
		}
		return criteria;
	}

	public static Criteria setDatetimePeriodToCriteria(Criteria criteria, String keySearch,
			DateRangeParam dateValueParam) {
		DateParam dateLowerParam = dateValueParam.getLowerBound();
		DateParam dateUpperParam = dateValueParam.getUpperBound();

		if (dateLowerParam != null && dateUpperParam != null) {
			switch (dateLowerParam.getPrefix()) {
			case GREATERTHAN: {
				switch (dateUpperParam.getPrefix()) {
				case LESSTHAN_OR_EQUALS: {
					criteria.orOperator(
							Criteria.where(keySearch + ".myStringValue").gt(dateLowerParam.getValue())
									.lte(dateUpperParam.getValue()),
							Criteria.where(keySearch + ".start.value.myStringValue").lte(dateLowerParam.getValue())
									.and(keySearch + ".end.value.myStringValue").gte(dateUpperParam.getValue()));
					break;
				}
				case LESSTHAN: {
					criteria.orOperator(
							Criteria.where(keySearch + ".myStringValue").gt(dateLowerParam.getValue())
									.lt(dateUpperParam.getValue()),
							Criteria.where(keySearch + ".start.value.myStringValue").lte(dateLowerParam.getValue())
									.and(keySearch + ".end.value.myStringValue").gte(dateUpperParam.getValue()));
					break;
				}
				default:
					log.trace("DEFAULT DATE(0) Prefix = " + dateValueParam.getValuesAsQueryTokens().get(0).getPrefix());
				}
				break;
			}
			case GREATERTHAN_OR_EQUALS: {
				switch (dateUpperParam.getPrefix()) {
				case LESSTHAN_OR_EQUALS: {
					criteria.orOperator(
							Criteria.where(keySearch + ".myStringValue").gte(dateLowerParam.getValue())
									.lte(dateUpperParam.getValue()),
							Criteria.where(keySearch + ".start.value.myStringValue").lte(dateLowerParam.getValue())
									.and(keySearch + ".end.value.myStringValue").gte(dateUpperParam.getValue()));
					break;
				}
				case LESSTHAN: {
					criteria.orOperator(
							Criteria.where(keySearch + ".myStringValue").gte(dateLowerParam.getValue())
									.lt(dateUpperParam.getValue()),
							Criteria.where(keySearch + ".start.value.myStringValue").lte(dateLowerParam.getValue())
									.and(keySearch + ".end.value.myStringValue").gte(dateUpperParam.getValue()));
					break;
				}
				default:
					log.trace("DEFAULT DATE(0) Prefix = " + dateValueParam.getValuesAsQueryTokens().get(0).getPrefix());
				}
				break;
			}
			case EQUAL: {
				criteria.orOperator(Criteria.where(keySearch + ".myStringValue").is(dateLowerParam.getValue()),
						Criteria.where(keySearch + ".start.value.myStringValue").lte(dateLowerParam.getValue())
								.and(keySearch + ".end.value.myStringValue").gte(dateLowerParam.getValue()));
				break;
			}
			case NOT_EQUAL: {
				criteria.orOperator(Criteria.where(keySearch + ".myStringValue").ne(dateLowerParam.getValue()),
						Criteria.where(keySearch + ".start.value.myStringValue").lte(dateLowerParam.getValue())
								.and(keySearch + ".end.value.myStringValue").gte(dateLowerParam.getValue()));
				break;
			}
			default:
			}
		} else if (dateLowerParam != null && dateUpperParam == null) {
			switch (dateLowerParam.getPrefix()) {
			case GREATERTHAN: {
				criteria.orOperator(Criteria.where(keySearch + ".myStringValue").gt(dateLowerParam.getValue()),
						Criteria.where(keySearch + ".start.value.myStringValue").lte(dateLowerParam.getValue())
								.and(keySearch + ".end.value.myStringValue").gte(dateLowerParam.getValue()));
				break;
			}
			case GREATERTHAN_OR_EQUALS: {
				criteria.orOperator(Criteria.where(keySearch + ".myStringValue").gte(dateLowerParam.getValue()),
						Criteria.where(keySearch + ".start.value.myStringValue").lte(dateLowerParam.getValue())
								.and(keySearch + ".end.value.myStringValue").gte(dateLowerParam.getValue()));
				break;
			}
			case APPROXIMATE:
			case EQUAL: {
				criteria.orOperator(Criteria.where(keySearch + ".myStringValue").is(dateLowerParam.getValue()),
						Criteria.where(keySearch + ".start.value.myStringValue").lte(dateLowerParam.getValue())
								.and(keySearch + ".end.value.myStringValue").gte(dateLowerParam.getValue()));
				break;
			}
			case NOT_EQUAL: {
				criteria.orOperator(Criteria.where(keySearch + ".myStringValue").ne(dateLowerParam.getValue()),
						Criteria.where(keySearch + ".start.value.myStringValue").lte(dateLowerParam.getValue())
								.and(keySearch + ".end.value.myStringValue").gte(dateLowerParam.getValue()));
				break;
			}
			case STARTS_AFTER:
			default:
			}
		} else if (dateLowerParam == null && dateUpperParam != null) {
			switch (dateUpperParam.getPrefix()) {
			case APPROXIMATE:
			case EQUAL: {
				criteria.orOperator(Criteria.where(keySearch + ".myStringValue").is(dateUpperParam.getValue()),
						Criteria.where(keySearch + ".start.value.myStringValue").lte(dateUpperParam.getValue())
								.and(keySearch + ".end.value.myStringValue").gte(dateUpperParam.getValue()));
				break;
			}

			case LESSTHAN_OR_EQUALS: {
				criteria.orOperator(Criteria.where(keySearch + ".myStringValue").lte(dateUpperParam.getValue()),
						Criteria.where(keySearch + ".start.value.myStringValue").lte(dateUpperParam.getValue())
								.and(keySearch + ".end.value.myStringValue").gte(dateUpperParam.getValue()));
				break;
			}
			case ENDS_BEFORE:
			case LESSTHAN: {
				criteria.orOperator(Criteria.where(keySearch + ".myStringValue").lt(dateUpperParam.getValue()),
						Criteria.where(keySearch + ".start.value.myStringValue").lte(dateUpperParam.getValue())
								.and(keySearch + ".end.value.myStringValue").gte(dateUpperParam.getValue()));
				break;
			}
			default:
				log.trace("DEFAULT DATE(0) Prefix = " + dateValueParam.getValuesAsQueryTokens().get(0).getPrefix());
			}
		}
		return criteria;
	}

	public static Criteria setQuantityToCriteria(Criteria criteria, String keySearch, QuantityParam quantityParam) {
		if (quantityParam.getPrefix() != null) {
			switch (quantityParam.getPrefix()) {
			case GREATERTHAN: {
				criteria.orOperator(
						Criteria.where(keySearch + ".value.myStringValue").gt(quantityParam.getValue())
								.and(keySearch + ".system.myStringValue").is(quantityParam.getSystem())
								.and(keySearch + ".unit.myStringValue").is(quantityParam.getUnits()),
						Criteria.where(keySearch + ".low.value.myStringValue").gt(quantityParam.getValue())
								.and(keySearch + ".high.value.myStringValue").lt(quantityParam.getValue()));
				break;
			}
			case GREATERTHAN_OR_EQUALS: {
				criteria.orOperator(
						Criteria.where(keySearch + ".value.myStringValue").gte(quantityParam.getValue())
								.and(keySearch + ".system.myStringValue").is(quantityParam.getSystem())
								.and(keySearch + ".unit.myStringValue").is(quantityParam.getUnits()),
						Criteria.where(keySearch + ".low.value.myStringValue").gt(quantityParam.getValue())
								.and(keySearch + ".high.value.myStringValue").lt(quantityParam.getValue()));
				break;
			}
			case EQUAL: {
				criteria.orOperator(
						Criteria.where(keySearch + ".value.myStringValue").is(quantityParam.getValue())
								.and(keySearch + ".system.myStringValue").is(quantityParam.getSystem())
								.and(keySearch + ".unit.myStringValue").is(quantityParam.getUnits()),
						Criteria.where(keySearch + ".low.value.myStringValue").gt(quantityParam.getValue())
								.and(keySearch + ".high.value.myStringValue").lt(quantityParam.getValue()));
				break;
			}
			case NOT_EQUAL: {
				criteria.orOperator(
						Criteria.where(keySearch + ".value.myStringValue").ne(quantityParam.getValue())
								.and(keySearch + ".system.myStringValue").is(quantityParam.getSystem())
								.and(keySearch + ".unit.myStringValue").is(quantityParam.getUnits()),
						Criteria.where(keySearch + ".low.value.myStringValue").gt(quantityParam.getValue())
								.and(keySearch + ".high.value.myStringValue").lt(quantityParam.getValue()));
				break;
			}
			case LESSTHAN_OR_EQUALS: {
				criteria.orOperator(
						Criteria.where(keySearch + ".value.myStringValue").lte(quantityParam.getValue())
								.and(keySearch + ".system.myStringValue").is(quantityParam.getSystem())
								.and(keySearch + ".unit.myStringValue").is(quantityParam.getUnits()),
						Criteria.where(keySearch + ".low.value.myStringValue").gt(quantityParam.getValue())
								.and(keySearch + ".high.value.myStringValue").lt(quantityParam.getValue()));
				break;
			}
			case LESSTHAN: {
				criteria.orOperator(
						Criteria.where(keySearch + ".value.myStringValue").lt(quantityParam.getValue())
								.and(keySearch + ".system.myStringValue").is(quantityParam.getSystem())
								.and(keySearch + ".unit.myStringValue").is(quantityParam.getUnits()),
						Criteria.where(keySearch + ".low.value.myStringValue").gt(quantityParam.getValue())
								.and(keySearch + ".high.value.myStringValue").lt(quantityParam.getValue()));
				break;
			}
			default:
			}
		} else {
			criteria.orOperator(
					Criteria.where(keySearch + ".value.myStringValue").is(quantityParam.getValue())
							.and(keySearch + ".system.myStringValue").is(quantityParam.getSystem())
							.and(keySearch + ".unit.myStringValue").is(quantityParam.getUnits()),
					Criteria.where(keySearch + ".low.value.myStringValue").lt(quantityParam.getValue())
							.and(keySearch + ".high.value.myStringValue").gt(quantityParam.getValue()));
		}
		return criteria;
	}

	public static Criteria setCodeListToCriteria(Criteria criteria, TokenOrListParam codelist) {
		var lst = new ArrayList<Criteria>();
		for (TokenParam codeitem : codelist.getValuesAsQueryTokens()) {
			if (!StringUtils.isBlank(codeitem.getSystem()) && !StringUtils.isBlank(codeitem.getValue())) {
				lst.add(new Criteria().andOperator(Criteria.where("code.coding.system").is(codeitem.getSystem()),
						Criteria.where("code.coding.code").is(codeitem.getValue())));
			} else if (!StringUtils.isBlank(codeitem.getSystem()) && StringUtils.isBlank(codeitem.getValue())) {
				lst.add(new Criteria().andOperator(Criteria.where("code.coding.system").is(codeitem.getSystem())));
			} else if (StringUtils.isBlank(codeitem.getSystem()) && !StringUtils.isBlank(codeitem.getValue())) {
				lst.add(new Criteria().andOperator(Criteria.where("code.coding.code").is(codeitem.getValue())));
			}
		}
		return criteria.orOperator(lst.toArray(new Criteria[0]));
	}

	public static Resource getResourceFromReference(Reference ref) {
		if (ref != null && ref.hasReference()) {
			Resource retVal = null;
			switch (StringUtil.getType(ref.getReference())) {
			case ConstantKeys.RES_CAREPLAN: {
				retVal = DaoFactory.getCarePlanDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_PATIENT: {
				retVal = DaoFactory.getPatientDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_ENCOUNTER: {
				retVal = DaoFactory.getEncounterDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_CLINICAL_IMPRESSION: {
				retVal = DaoFactory.getClinicalImpressionDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_CONDITION: {
				retVal = DaoFactory.getConditionDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_DETECTED_ISSUE: {
				retVal = DaoFactory.getDetectedIssueDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_GOAL: {
				retVal = DaoFactory.getGoalDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_PROCEDURE: {
				retVal = DaoFactory.getProcedureDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_SERVICE_REQUEST: {
				retVal = DaoFactory.getServiceRequestDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_DIAGNOSTIC_REPORT: {
				retVal = DaoFactory.getDiagnosticReportDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_IMAGING_STUDY: {
				retVal = DaoFactory.getImagingStudyDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_OBSERVATION: {
				retVal = DaoFactory.getObservationDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_SPECIMEN: {
				retVal = DaoFactory.getSpecimenDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_CARETEAM: {
				retVal = DaoFactory.getCareTeamDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_EPISODEOFCARE: {
				retVal = DaoFactory.getEpisodeOfCareDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_IMMUNIZATION: {
				retVal = DaoFactory.getImmunizationDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_MEDICATION_ADMINISTRATION: {
				retVal = DaoFactory.getMedicationAdministrationDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_MEDICATION_DISPENSE: {
				retVal = DaoFactory.getMedicationDispenseDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_MEDICATION: {
				retVal = DaoFactory.getMedicationDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_MEDICATION_REQUEST: {
				retVal = DaoFactory.getMedicationRequestDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_MEDICATION_STATEMENT: {
				retVal = DaoFactory.getMedicationStatementDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_RELATED_PERSON: {
				retVal = DaoFactory.getRelatedPersonDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_DEVICE: {
				retVal = DaoFactory.getDeviceDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_HEALTHCARE_SERVICE: {
				retVal = DaoFactory.getHealthcareServiceDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_LOCATION: {
				retVal = DaoFactory.getLocationDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_ORGANIZATION: {
				retVal = DaoFactory.getOrganizationDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_PRACTITIONER: {
				retVal = DaoFactory.getPractitionerDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_PRACTITIONER_ROLE: {
				retVal = DaoFactory.getPractitionerRoleDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_APPOINTMENT: {
				retVal = DaoFactory.getAppointmentDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_APPOINMENT_RESPONSE: {
				retVal = DaoFactory.getAppointmentResponseDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_SCHEDULE: {
				retVal = DaoFactory.getScheduleDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_SLOT: {
				retVal = DaoFactory.getSlotDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_CODESYSTEM: {
				retVal = DaoFactory.getCodeSystemDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_CONCEPTMAP: {
				retVal = DaoFactory.getConceptMapDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_VALUESET: {
				retVal = DaoFactory.getValueSetDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_PERSON: {
				retVal = DaoFactory.getPersonDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_ALLERGY_INTORANCE: {
				retVal = DaoFactory.getAllergyIntoleranceDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_FAMILY_MEMBER_HISTORY: {
				retVal = DaoFactory.getFamilyMemberHistoryDao().read(FhirUtil.createIdType(ref));
				break;
			}
			case ConstantKeys.RES_MEDIA: {
				retVal = DaoFactory.getMediaDao().read(FhirUtil.createIdType(ref));
				break;
			}
			default:
				return null;

			}
			return retVal;
		}
		return null;
	}
}
