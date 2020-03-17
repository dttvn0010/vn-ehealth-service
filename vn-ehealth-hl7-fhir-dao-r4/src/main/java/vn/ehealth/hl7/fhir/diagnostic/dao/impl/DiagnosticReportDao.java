package vn.ehealth.hl7.fhir.diagnostic.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.DiagnosticReport;
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
import vn.ehealth.hl7.fhir.diagnostic.dao.IDiagnosticReport;
import vn.ehealth.hl7.fhir.diagnostic.dao.transform.DiagnosticReportEntityToFHIRDiagnosticReport;
import vn.ehealth.hl7.fhir.diagnostic.entity.DiagnosticReportEntity;

@Repository
public class DiagnosticReportDao implements IDiagnosticReport {

    @Autowired
    MongoOperations mongo;

    @Autowired
    DiagnosticReportEntityToFHIRDiagnosticReport diagnosticReportEntityToFHIRDiagnosticReport;

    @Override
    public DiagnosticReport create(FhirContext fhirContext, DiagnosticReport object) {
        DiagnosticReportEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewDiagnosticReportEntity(object, version, null);
            // save DiagnosticReportEntity database
            mongo.save(entity);
            return diagnosticReportEntityToFHIRDiagnosticReport.transform(entity);
        }
        return null;
    }

    @Override
    @CachePut(value = "diagnosticReport", key = "#idType")
    public DiagnosticReport update(FhirContext fhirContext, DiagnosticReport object, IdType idType) {
        DiagnosticReportEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            entityOld = mongo.findOne(query, DiagnosticReportEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove DiagnosticReportEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save DiagnosticReportEntity
            int version = entityOld.version + 1;
            if (object != null) {
                DiagnosticReportEntity entity = createNewDiagnosticReportEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                return diagnosticReportEntityToFHIRDiagnosticReport.transform(entity);
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "diagnosticReport", key = "#idType")
    public DiagnosticReport read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            DiagnosticReportEntity entity = mongo.findOne(query, DiagnosticReportEntity.class);
            if (entity != null) {
                return diagnosticReportEntityToFHIRDiagnosticReport.transform(entity);
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "diagnosticReport", key = "#idType")
    public DiagnosticReport remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true));
            DiagnosticReportEntity entity = mongo.findOne(query, DiagnosticReportEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                return diagnosticReportEntityToFHIRDiagnosticReport.transform(entity);
            }
        }
        return null;
    }

    @Override
    public DiagnosticReport readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version));
                DiagnosticReportEntity entity = mongo.findOne(query, DiagnosticReportEntity.class);
                if (entity != null) {
                    return diagnosticReportEntityToFHIRDiagnosticReport.transform(entity);
                }
            }
        }
        return null;
    }

    @Override
    public List<Resource> search(FhirContext fhirContext, TokenParam active, ReferenceParam basedOn,
            TokenParam category, TokenParam code, ReferenceParam conetext, DateRangeParam date, TokenParam diagnosis,
            ReferenceParam encounter, TokenParam identifier, ReferenceParam image, DateRangeParam issued,
            ReferenceParam patient, ReferenceParam performer, ReferenceParam result, ReferenceParam specimen,
            TokenParam status, ReferenceParam subject, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content, StringParam _page,
            String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, basedOn, category, code, conetext, date, diagnosis, encounter,
                identifier, image, issued, patient, performer, result, specimen, status, subject, resid, _lastUpdated,
                _tag, _profile, _query, _security, _content);
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
        List<DiagnosticReportEntity> DiagnosticReportEntitys = mongo.find(query, DiagnosticReportEntity.class);
        if (DiagnosticReportEntitys != null) {
            for (DiagnosticReportEntity item : DiagnosticReportEntitys) {
                DiagnosticReport DiagnosticReport = diagnosticReportEntityToFHIRDiagnosticReport.transform(item);
                resources.add(DiagnosticReport);
            }
        }
        return resources;
    }

    @Override
    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, ReferenceParam basedOn,
            TokenParam category, TokenParam code, ReferenceParam conetext, DateRangeParam date, TokenParam diagnosis,
            ReferenceParam encounter, TokenParam identifier, ReferenceParam image, DateRangeParam issued,
            ReferenceParam patient, ReferenceParam performer, ReferenceParam result, ReferenceParam specimen,
            TokenParam status, ReferenceParam subject, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
        long total = 0;
        Criteria criteria = setParamToCriteria(active, basedOn, category, code, conetext, date, diagnosis, encounter,
                identifier, image, issued, patient, performer, result, specimen, status, subject, resid, _lastUpdated,
                _tag, _profile, _query, _security, _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, DiagnosticReportEntity.class);
        return total;
    }

    private DiagnosticReportEntity createNewDiagnosticReportEntity(DiagnosticReport obj, int version,
            String fhirId) {
        var ent = DiagnosticReportEntity.fromDiagnosticReport(obj);
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

    private Criteria setParamToCriteria(TokenParam active, ReferenceParam basedOn, TokenParam category, TokenParam code,
            ReferenceParam conetext, DateRangeParam date, TokenParam diagnosis, ReferenceParam encounter,
            TokenParam identifier, ReferenceParam image, DateRangeParam issued, ReferenceParam patient,
            ReferenceParam performer, ReferenceParam result, ReferenceParam specimen, TokenParam status,
            ReferenceParam subject, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile,
            TokenParam _query, TokenParam _security, StringParam _content) {
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

        return criteria;
    }

}
