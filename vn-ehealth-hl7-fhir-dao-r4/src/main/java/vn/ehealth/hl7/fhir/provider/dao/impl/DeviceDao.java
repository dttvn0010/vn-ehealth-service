package vn.ehealth.hl7.fhir.provider.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.provider.dao.IDevice;
import vn.ehealth.hl7.fhir.provider.dao.transform.DeviceEntityToFHIRDevice;
import vn.ehealth.hl7.fhir.provider.entity.DeviceEntity;

@Repository
public class DeviceDao implements IDevice {

    @Autowired
    MongoOperations mongo;

    @Autowired
    DeviceEntityToFHIRDevice deviceEntityToFHIRDevice;

    @Override
    public Device create(FhirContext fhirContext, Device object) {
        DeviceEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewDeviceEntity(object, version, null);
            // save DeviceEntity database
            mongo.save(entity);
            return deviceEntityToFHIRDevice.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "device", key = "#idType")
    public Device update(FhirContext fhirContext, Device object, IdType idType) {
        DeviceEntity entityOld = null;
        String fhirId = "";
        if (idType != null) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, DeviceEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove DeviceEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            entityOld.status = ("inactive");
            mongo.save(entityOld);
            // save DeviceEntity
            int version = entityOld.version + 1;
            if (object != null) {
                DeviceEntity entity = createNewDeviceEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return deviceEntityToFHIRDevice.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "device", key = "#idType")
    public Device read(FhirContext fhirContext, IdType idType) {
        if (idType != null&& idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            DeviceEntity entity = mongo.findOne(query, DeviceEntity.class);
            if (entity != null) {
                return deviceEntityToFHIRDevice.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "device", key = "#idType")
    public Device remove(FhirContext fhirContext, IdType idType) {
        if (idType != null&& idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            DeviceEntity entity = mongo.findOne(query, DeviceEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                entity.status = ("inactive");
                mongo.save(entity);
                return deviceEntityToFHIRDevice.transform(entity);
            }
        }
        return null;
    }

    @Override
    public Device readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query
                        .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                DeviceEntity entity = mongo.findOne(query, DeviceEntity.class);
                if (entity != null) {
                    return deviceEntityToFHIRDevice.transform(entity);
                }
            }
        }
        return null;
    }
    
    @Override
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
                count != null ? count : ConstantKeys.DEFAULT_PAGE_MAX_SIZE);
        query.with(pageableRequest);
        if (!sortParam.equals("")) {
            query.with(new Sort(Sort.Direction.ASC, sortParam));
        }
        List<DeviceEntity> deviceEntitys = mongo.find(query, DeviceEntity.class);
        if (deviceEntitys != null) {
            for (DeviceEntity item : deviceEntitys) {
                Device device = deviceEntityToFHIRDevice.transform(item);
                resources.add(device);
            }
        }
        return resources;
    }

    @Override
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

    private DeviceEntity createNewDeviceEntity(Device obj, int version, String fhirId) {
        var ent = DeviceEntity.fromDevice(obj);
        DataConvertUtil.setMetaExt(obj, ent);
        if (fhirId != null && !fhirId.isEmpty()) {
            ent.fhirId = (fhirId);
        } else {
            ent.fhirId = (StringUtil.generateUID());
        }
        
        ent.active = (true);
        ent.version = (version);
        ent.resCreated = (new Date());
        return ent;
    }

    private Criteria setParamToCriteria(StringParam deviceName, TokenParam identifier, ReferenceParam location,
            StringParam manufacturer, StringParam model, ReferenceParam organization, ReferenceParam patient,
            StringParam udiCarrier, StringParam udiDi, UriParam url, TokenParam status, TokenParam type,
            TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
            TokenParam _security, StringParam _content) {
        Criteria criteria = null;
        criteria = Criteria.where("$where").is("1==1");

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
            if(location.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("location.reference").regex(location.getValue()),
                        Criteria.where("location.display").regex(location.getValue()));
            }else {
                String[] ref= location.getValue().split("\\|");
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
            if(organization.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("owner.reference").regex(organization.getValue()),
                        Criteria.where("owner.display").regex(organization.getValue()));
            }else {
                String[] ref= organization.getValue().split("\\|");
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
}
