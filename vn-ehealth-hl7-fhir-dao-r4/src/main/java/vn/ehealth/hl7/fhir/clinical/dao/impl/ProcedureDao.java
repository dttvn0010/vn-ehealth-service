package vn.ehealth.hl7.fhir.clinical.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
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
import vn.ehealth.hl7.fhir.clinical.dao.IProcedure;
import vn.ehealth.hl7.fhir.clinical.dao.transform.ProcedureEntityToFHIRProcedure;
import vn.ehealth.hl7.fhir.clinical.entity.ProcedureEntity;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;

@Repository
public class ProcedureDao implements IProcedure {

    @Autowired
    MongoOperations mongo;

    @Autowired
    ProcedureEntityToFHIRProcedure procedureEntityToFHIRProcedure;

    @Override
    public Procedure create(FhirContext fhirContext, Procedure object) {
        ProcedureEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewProcedureEntity(object, version, null);
            // save ProcedureEntity database
            mongo.save(entity);
            return procedureEntityToFHIRProcedure.transform(entity);
        }
        return null;
    }

    @Override
    public Procedure update(FhirContext fhirContext, Procedure object, IdType idType) {
        ProcedureEntity entityOld = null;
        String fhirId = "";
        if (idType != null) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, ProcedureEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove ProcedureEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save ProcedureEntity
            int version = entityOld.version + 1;
            if (object != null) {
                ProcedureEntity entity = createNewProcedureEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return procedureEntityToFHIRProcedure.transform(entity);
            }
        }
        return null;
    }

    @Override
    public Procedure read(FhirContext fhirContext, IdType idType) {
        if (idType != null) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            ProcedureEntity entity = mongo.findOne(query, ProcedureEntity.class);
            if (entity != null) {
                return procedureEntityToFHIRProcedure.transform(entity);
            }
        }
        return null;
    }

    @Override
    public Procedure remove(FhirContext fhirContext, IdType idType) {
        if (idType != null) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            ProcedureEntity entity = mongo.findOne(query, ProcedureEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return procedureEntityToFHIRProcedure.transform(entity);
            }
        }
        return null;
    }
    
    @Override
    public Procedure readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query
                        .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                ProcedureEntity entity = mongo.findOne(query, ProcedureEntity.class);
                if (entity != null) {
                    return procedureEntityToFHIRProcedure.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, ReferenceParam bassedOn,
            TokenParam category, TokenParam code, ReferenceParam context, DateRangeParam date,
            ReferenceParam definition, ReferenceParam encounter, TokenParam identifier, ReferenceParam location,
            ReferenceParam partOf, ReferenceParam patient, ReferenceParam performer, TokenParam status,
            ReferenceParam subject, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
            TokenParam _query, TokenParam _security, StringParam _content, StringParam _page, String sortParam,
            Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, bassedOn, category, code, context, date, definition, encounter,
                identifier, location, partOf, patient, performer, status, subject, resid, _lastUpdated, _tag, _profile,
                _query, _security, _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        Pageable pageableRequest;
        pageableRequest = new PageRequest(_page != null ? Integer.valueOf(_page.getValue()) : ConstantKeys.PAGE,
                count != null ? count : ConstantKeys.DEFAULT_PAGE_MAX_SIZE);
        query.with(pageableRequest);
        if (sortParam != null && !sortParam.equals("")) {
            query.with(new Sort(Sort.Direction.ASC, sortParam));
        }
        List<ProcedureEntity> procedureEntitys = mongo.find(query, ProcedureEntity.class);
        if (procedureEntitys != null) {
            for (ProcedureEntity item : procedureEntitys) {
                Procedure procedure = procedureEntityToFHIRProcedure.transform(item);
                resources.add(procedure);
            }
        }
        return resources;
    }

    @Override
    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, ReferenceParam bassedOn,
            TokenParam category, TokenParam code, ReferenceParam context, DateRangeParam date,
            ReferenceParam definition, ReferenceParam encounter, TokenParam identifier, ReferenceParam location,
            ReferenceParam partOf, ReferenceParam patient, ReferenceParam performer, TokenParam status,
            ReferenceParam subject, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
            TokenParam _query, TokenParam _security, StringParam _content) {
        long total = 0;
        Criteria criteria = setParamToCriteria(active, bassedOn, category, code, context, date, definition, encounter,
                identifier, location, partOf, patient, performer, status, subject, resid, _lastUpdated, _tag, _profile,
                _query, _security, _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, ProcedureEntity.class);
        return total;
    }

    private ProcedureEntity createNewProcedureEntity(Procedure obj, int version, String fhirId) {
        var ent = ProcedureEntity.fromProcedure(obj);
        DataConvertUtil.setMetaExt(obj, ent);
        if (fhirId != null && !fhirId.isEmpty()) {
            ent.fhir_id = (fhirId);
        } else {
            ent.fhir_id = (StringUtil.generateUID());
        }
        
        ent.active = (true);
        ent.version = (version);
        ent.resCreated = (new Date());
        return ent;
    }

    private Criteria setParamToCriteria(TokenParam active, ReferenceParam bassedOn, TokenParam category,
            TokenParam code, ReferenceParam context, DateRangeParam date, ReferenceParam definition,
            ReferenceParam encounter, TokenParam identifier, ReferenceParam location, ReferenceParam partOf,
            ReferenceParam patient, ReferenceParam performer, TokenParam status, ReferenceParam subject,
            TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
            TokenParam _security, StringParam _content) {
        Criteria criteria = null;
        // active
        if (active != null) {
            criteria = Criteria.where("active").is(active);
        } else {
            criteria = Criteria.where("active").is(true);
        }
        // set param default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                identifier);
        // based-on
        if (bassedOn != null) {
            if(bassedOn.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("bassedOn.reference").is(bassedOn.getValue()),
                        Criteria.where("bassedOn.display").is(bassedOn.getValue()));
            }else {
                String[] ref= bassedOn.getValue().split("\\|");
                criteria.and("bassedOn.identifier.system").is(ref[0]).and("bassedOn.identifier.value").is(ref[1]);
            }
        }
        // category
        if (category != null) {
            criteria.and("category.coding.code.myStringValue").is(category.getValue());
        }
        // code
        if (code != null) {
            criteria.and("code.coding.code.myStringValue").is(code.getValue());
        }
        // context
        if (context != null) {
            if(context.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("context.reference").is(context.getValue()),
                        Criteria.where("context.display").is(context.getValue()));
            }else {
                String[] ref= context.getValue().split("\\|");
                criteria.and("context.identifier.system").is(ref[0]).and("context.identifier.value").is(ref[1]);
            }
        }
        // date
        if (date != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "performed", date);
        }
        // definition
        if (definition != null) {
            if(definition.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("definition.reference").is(definition.getValue()),
                        Criteria.where("definition.display").is(definition.getValue()));
            }else {
                String[] ref= definition.getValue().split("\\|");
                criteria.and("definition.identifier.system").is(ref[0]).and("definition.identifier.value").is(ref[1]);
            }
        }
        // encounter
        if (encounter != null) {
            if(encounter.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("context.reference").is(encounter.getValue()),
                        Criteria.where("context.display").is(encounter.getValue()));
            }else {
                String[] ref= encounter.getValue().split("\\|");
                criteria.and("context.identifier.system").is(ref[0]).and("context.identifier.value").is(ref[1]);
            }
        }
        // location
        if (location != null) {
            if(location.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("location.location.reference").is(location.getValue()),
                        Criteria.where("location.location.display").is(location.getValue()));
            }else {
                String[] ref= location.getValue().split("\\|");
                criteria.and("location.location.identifier.system").is(ref[0]).and("location.location.identifier.value").is(ref[1]);
            }
        }
        // part-of
        if (partOf != null) {
            if(partOf.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("partOf.reference").is(partOf.getValue()),
                        Criteria.where("partOf.display").is(partOf.getValue()));
            }else {
                String[] ref= partOf.getValue().split("\\|");
                criteria.and("partOf.identifier.system").is(ref[0]).and("partOf.identifier.value").is(ref[1]);
            }
        }
        // patient
        if (patient != null) {
            if(patient.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("subject.reference").is(patient.getValue()),
                        Criteria.where("subject.display").is(patient.getValue()));
            }else {
                String[] ref= patient.getValue().split("\\|");
                criteria.and("subject.identifier.system").is(ref[0]).and("subject.identifier.value").is(ref[1]);
            }
        }
        // performer
        if (performer != null) {
            if(performer.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("performer.reference").is(performer.getValue()),
                        Criteria.where("performer.display").is(performer.getValue()));
            }else {
                String[] ref= performer.getValue().split("\\|");
                criteria.and("performer.identifier.system").is(ref[0]).and("performer.identifier.value").is(ref[1]);
            }
        }
        // status
        if (status != null) {
            criteria.and("status").is(status.getValue());
        }
        // subject
        if (subject != null) {
            if(subject.getValue().indexOf("|")==-1) {
                criteria.orOperator(Criteria.where("subject.reference").is(subject.getValue()),
                        Criteria.where("subject.display").is(subject.getValue()));
            }else {
                String[] ref= subject.getValue().split("\\|");
                criteria.and("subject.identifier.system").is(ref[0]).and("subject.identifier.value").is(ref[1]);
            }
        }
        return criteria;
    }

}
