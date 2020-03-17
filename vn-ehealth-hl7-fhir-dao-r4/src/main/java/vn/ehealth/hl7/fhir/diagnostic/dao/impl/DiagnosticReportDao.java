package vn.ehealth.hl7.fhir.diagnostic.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.DiagnosticReport;
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
import vn.ehealth.hl7.fhir.diagnostic.entity.DiagnosticReportEntity;

@Repository
public class DiagnosticReportDao extends BaseDao<DiagnosticReportEntity, DiagnosticReport> {

    @SuppressWarnings("deprecation")
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
                DiagnosticReport DiagnosticReport = transform(item);
                resources.add(DiagnosticReport);
            }
        }
        return resources;
    }

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

    @Override
    protected String getProfile() {
        return "DiagnosticReport-v1.0";
    }

    @Override
    protected DiagnosticReportEntity fromFhir(DiagnosticReport obj) {
        return DiagnosticReportEntity.fromDiagnosticReport(obj);
    }

    @Override
    protected DiagnosticReport toFhir(DiagnosticReportEntity ent) {
        return DiagnosticReportEntity.toDiagnosticReport(ent);
    }

    @Override
    protected Class<? extends BaseResource> getEntityClass() {
        return DiagnosticReportEntity.class;
    }

}
