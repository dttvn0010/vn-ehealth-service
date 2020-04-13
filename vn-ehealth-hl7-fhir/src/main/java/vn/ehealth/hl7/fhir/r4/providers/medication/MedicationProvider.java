package vn.ehealth.hl7.fhir.r4.providers.medication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import vn.ehealth.hl7.fhir.controller.BaseController;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.factory.OperationOutcomeException;
import vn.ehealth.hl7.fhir.factory.OperationOutcomeFactory;
import vn.ehealth.hl7.fhir.medication.dao.impl.MedicationDao;
import vn.ehealth.hl7.fhir.medication.entity.MedicationEntity;

@Component
public class MedicationProvider extends BaseController<MedicationEntity, Medication> implements IResourceProvider {
	@Autowired
	MedicationDao medicationDao;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Medication.class;
	}

	@Override
	protected List<String> getProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Search
	public IBundleProvider searchMedication(HttpServletRequest request,
			@OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
			@OptionalParam(name = ConstantKeys.SP_CONTAINER) TokenParam container,
			@OptionalParam(name = ConstantKeys.SP_FORM) TokenParam form,
			@OptionalParam(name = ConstantKeys.SP_INGREDIENT) ReferenceParam ingredient,
			@OptionalParam(name = ConstantKeys.SP_INGREDIENT_CODE) TokenParam ingredientCode,
			@OptionalParam(name = ConstantKeys.SP_MANUFACTURER) ReferenceParam manufacturer,
			@OptionalParam(name = ConstantKeys.SP_OVER_THE_COUNTER) TokenParam overTheCounter,
			@OptionalParam(name = ConstantKeys.SP_PACKAGE_ITEM) ReferenceParam packageItem,
			@OptionalParam(name = ConstantKeys.SP_PACKAGE_ITEM_CODE) TokenParam packageItemCode,
			@OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
			@OptionalParam(name = "hospital") TokenParam hospital,
			@OptionalParam(name = "productName") StringParam productName,
			@OptionalParam(name = "medicationType") StringParam medicationType,
			// COMMON
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
			@OptionalParam(name = ConstantKeys.SP_PAGE) NumberParam _page, @Sort SortSpec theSort, @Count Integer count)
			throws OperationOutcomeException {
		if (count != null && count > ConstantKeys.DEFAULT_PAGE_MAX_SIZE) {
			throw OperationOutcomeFactory.buildOperationOutcomeException(
					new ResourceNotFoundException("Can not load more than " + ConstantKeys.DEFAULT_PAGE_MAX_SIZE),
					OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTSUPPORTED);
		} else {
			List<Resource> results = new ArrayList<Resource>();
			if (theSort != null) {
				String sortParam = theSort.getParamName();
				results = medicationDao.search(fhirContext, code, container, form, ingredient, ingredientCode,
						manufacturer, overTheCounter, packageItem, packageItemCode, status, hospital, productName,
						medicationType, resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page,
						sortParam, count);
			} else
				results = medicationDao.search(fhirContext, code, container, form, ingredient, ingredientCode,
						manufacturer, overTheCounter, packageItem, packageItemCode, status, hospital, productName,
						medicationType, resid, _lastUpdated, _tag, _profile, _query, _security, _content, _page, null,
						count);
			final List<IBaseResource> finalResults = DataConvertUtil.transform(results, x -> x);

			return new IBundleProvider() {

				@Override
				public Integer size() {
					return Integer.parseInt(String.valueOf(medicationDao.countMatchesAdvancedTotal(fhirContext, code,
							container, form, ingredient, ingredientCode, manufacturer, overTheCounter, packageItem,
							packageItemCode, status, resid, _lastUpdated, _tag, _profile, _query, _security, _content,
							hospital, productName, medicationType)));
				}

				@Override
				public Integer preferredPageSize() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getUuid() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
					return finalResults;
				}

				@Override
				public IPrimitiveType<Date> getPublished() {
					// TODO Auto-generated method stub
					return null;
				}
			};
		}
	}

	@Operation(name = "$total", idempotent = true)
	public Parameters getTotal(HttpServletRequest request, @OptionalParam(name = ConstantKeys.SP_CODE) TokenParam code,
			@OptionalParam(name = ConstantKeys.SP_CONTAINER) TokenParam container,
			@OptionalParam(name = ConstantKeys.SP_FORM) TokenParam form,
			@OptionalParam(name = ConstantKeys.SP_INGREDIENT) ReferenceParam ingredient,
			@OptionalParam(name = ConstantKeys.SP_INGREDIENT_CODE) TokenParam ingredientCode,
			@OptionalParam(name = ConstantKeys.SP_MANUFACTURER) ReferenceParam manufacturer,
			@OptionalParam(name = ConstantKeys.SP_OVER_THE_COUNTER) TokenParam overTheCounter,
			@OptionalParam(name = ConstantKeys.SP_PACKAGE_ITEM) ReferenceParam packageItem,
			@OptionalParam(name = ConstantKeys.SP_PACKAGE_ITEM_CODE) TokenParam packageItemCode,
			@OptionalParam(name = ConstantKeys.SP_STATUS) TokenParam status,
			// dung chung
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT) StringParam _content,
			@OptionalParam(name = "hospital") TokenParam hospital,
			@OptionalParam(name = "productName") StringParam productName,
			@OptionalParam(name = "medicationType") StringParam medicationType) {
		Parameters retVal = new Parameters();
		long total = medicationDao.countMatchesAdvancedTotal(fhirContext, code, container, form, ingredient,
				ingredientCode, manufacturer, overTheCounter, packageItem, packageItemCode, status, resid, _lastUpdated,
				_tag, _profile, _query, _security, _content, hospital, productName, medicationType);
		retVal.addParameter().setName("total").setValue(new StringType(String.valueOf(total)));
		return retVal;
	}

	@Override
	protected BaseDao<MedicationEntity, Medication> getDao() {
		return medicationDao;
	}
}
