package vn.ehealth.hl7.fhir.dao.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.QuantityParam;
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;

public class DatabaseUtil {
	private static final Logger log = LoggerFactory.getLogger(DatabaseUtil.class);

	public static Criteria setTypeDateToCriteria(Criteria criteria, String keySearch, DateRangeParam dateValueParam) {
		if(dateValueParam == null) return criteria;
		
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
			criteria.and(ConstantKeys.QP_FHIRID).regex(resid.getValue());
		}
		if (_tag != null) {
			criteria.and("_tag.code.myStringValue").regex(_tag.getValue());
		}
		if (_profile != null) {
			criteria.and("_profile.myStringValue").regex(_profile.getValue());
		}
		if (_security != null) {
			criteria.and("_security.code.myStringValue").regex(_security.getValue());
		}
		if (_lastUpdated != null) {
			criteria = setTypeDateToCriteria(criteria, ConstantKeys.QP_UPDATED, _lastUpdated);
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
		var resourceType = FhirUtil.getResourceType(ref);
		if(resourceType != null) {
			var dao = DaoFactory.getDaoByType(resourceType);
			if(dao != null) {
				return dao.read(FhirUtil.createIdType(ref));
			} 
		}
		return null;
	}
	
	public static void setReferenceResource(Reference ref) {
		if(ref != null) {
			ref.setResource(getResourceFromReference(ref));
		}
	}
	
	public static void setReferenceResource(List<Reference> refs) {
		if(refs != null) {
			refs.forEach(x -> setReferenceResource(x));
		}
	}
	
	public static Map<String, Boolean> getIncludeMap(ResourceType resourceType, String[] keys, Set<Include> includes) {
		var includeMap = new HashMap<String, Boolean>();
		var includeAll = new Include("*");
		for(var key : keys) {
			boolean contained = (includes != null) &&( 
									includes.contains(includeAll) || 
									includes.contains(new Include(resourceType + ":" + key))
								);
			includeMap.put(key, contained);
		}
		return includeMap;
	}
}
