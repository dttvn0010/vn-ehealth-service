package vn.ehealth.hl7.fhir.provider.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.DomainResource;
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
import vn.ehealth.hl7.fhir.provider.entity.DeviceEntity;

@Repository
public class DeviceDao extends BaseDao<DeviceEntity, Device> {

	@SuppressWarnings("deprecation")
	public List<Resource> search(FhirContext ctx, StringParam deviceName, TokenParam identifier,
			ReferenceParam location, StringParam manufacturer, StringParam model, ReferenceParam organization,
			ReferenceParam patient, StringParam udiCarrier, StringParam udiDi, UriParam url, TokenParam status,
			TokenParam type, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
			TokenParam _query, TokenParam _security, StringParam _content, StringParam _page, String sortParam,
			Integer count) {

		List<Resource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(deviceName, identifier, location, manufacturer, model, organization,
				patient, udiCarrier, udiDi, url, status, type, resid, _lastUpdated, _tag, _profile, _query, _security,
				_content);
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
		List<DeviceEntity> deviceEntitys = mongo.find(query, DeviceEntity.class);
		if (deviceEntitys != null) {
			for (DeviceEntity item : deviceEntitys) {
				Device device = transform(item);
				resources.add(device);
			}
		}
		return resources;
	}

	public long findMatchesAdvancedTotal(FhirContext ctx, StringParam deviceName, TokenParam identifier,
			ReferenceParam location, StringParam manufacturer, StringParam model, ReferenceParam organization,
			ReferenceParam patient, StringParam udiCarrier, StringParam udiDi, UriParam url, TokenParam status,
			TokenParam type, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
			TokenParam _query, TokenParam _security, StringParam _content) {

		long total = 0;
		Criteria criteria = setParamToCriteria(deviceName, identifier, location, manufacturer, model, organization,
				patient, udiCarrier, udiDi, url, status, type, resid, _lastUpdated, _tag, _profile, _query, _security,
				_content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, DeviceEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(StringParam deviceName, TokenParam identifier, ReferenceParam location,
			StringParam manufacturer, StringParam model, ReferenceParam organization, ReferenceParam patient,
			StringParam udiCarrier, StringParam udiDi, UriParam url, TokenParam status, TokenParam type,
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content) {
		Criteria criteria = Criteria.where("$where").is("1==1");
		// active
		criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);

		if (status != null) {
			criteria.and("status").is(status.getValue());
		}
		criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
				identifier);
		if (deviceName != null) {
			criteria.orOperator(Criteria.where("device.udi.name").regex(deviceName.getValue()),
					Criteria.where("device.type.text").regex(deviceName.getValue()));
		}
		if (location != null) {
//            criteria.orOperator(Criteria.where("location.reference").regex(location.getValue()),
//                    Criteria.where("location.display").regex(location.getValue()),
//                    Criteria.where("location.identifier.value").regex(location.getValue()),
//                    Criteria.where("location.identifier.system").regex(location.getValue()));
			if (location.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("location.reference").regex(location.getValue()),
						Criteria.where("location.display").regex(location.getValue()));
			} else {
				String[] ref = location.getValue().split("\\|");
				criteria.and("location.identifier.system").is(ref[0]).and("location.identifier.value").is(ref[1]);
			}
		}
		if (manufacturer != null) {
			criteria.and("manufacturer").regex(manufacturer.getValue());
		}
		if (model != null) {
			criteria.and("model").regex(model.getValue());
		}
		if (organization != null) {
//            criteria.orOperator(Criteria.where("owner.reference").regex(organization.getValue()),
//                    Criteria.where("owner.display").regex(organization.getValue()),
//                    Criteria.where("owner.identifier.value").regex(organization.getValue()),
//                    Criteria.where("owner.identifier.system").regex(organization.getValue()));
			if (organization.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("owner.reference").regex(organization.getValue()),
						Criteria.where("owner.display").regex(organization.getValue()));
			} else {
				String[] ref = organization.getValue().split("\\|");
				criteria.and("owner.identifier.system").is(ref[0]).and("owner.identifier.value").is(ref[1]);
			}
		}
		if (patient != null) {
			criteria.orOperator(Criteria.where("patient.reference").regex(patient.getValue()),
					Criteria.where("patient.display").regex(patient.getValue()),
					Criteria.where("patient.identifier.value").regex(patient.getValue()),
					Criteria.where("patient.identifier.system").regex(patient.getValue()));
		}
		if (type != null) {
			criteria.and("type").regex(type.getValue());
		}
		if (udiCarrier != null) {
			criteria.and("device.udi.carrierHRF").regex(udiCarrier.getValue());
		}
		if (udiDi != null) {
			criteria.and("device.udi.deviceIdentifier").regex(udiDi.getValue());
		}
		if (url != null) {
			criteria.and("device.url").regex(url.getValue());
		}
		return criteria;
	}

	@Override
	protected String getProfile() {
		return "Device-v1.0";
	}

	@Override
    protected Class<? extends DomainResource> getResourceClass() {
        return Device.class;
    }

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return DeviceEntity.class;
	}
}
