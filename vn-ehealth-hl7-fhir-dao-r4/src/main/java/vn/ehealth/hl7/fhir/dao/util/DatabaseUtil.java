package vn.ehealth.hl7.fhir.dao.util;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.IdType;
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
				retVal = DaoFactory.getCarePlanDao().read(createIdTypeFromReference(ref));
				break;
			}
			case ConstantKeys.RES_PATIENT: {
				retVal = DaoFactory.getPatientDao().read(createIdTypeFromReference(ref));
				break;
			}
			case ConstantKeys.RES_ENCOUNTER: {
				retVal = DaoFactory.getEncounterDao().read(createIdTypeFromReference(ref));
				break;
			}
			default:
				return null;

			}
			return retVal;
		}

		return null;
	}

	public static IdType createIdTypeFromReference(Reference ref) {
		if ((ref != null && ref.hasReference())) {
			return new IdType(StringUtil.getId(ref.getReference()));
		}
		return null;
	}
}
