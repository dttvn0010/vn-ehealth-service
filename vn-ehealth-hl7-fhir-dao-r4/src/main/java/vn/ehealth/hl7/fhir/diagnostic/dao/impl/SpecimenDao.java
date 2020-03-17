package vn.ehealth.hl7.fhir.diagnostic.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Specimen;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.diagnostic.entity.SpecimenEntity;

@Repository
public class SpecimenDao extends BaseDao<SpecimenEntity, Specimen> {
    @SuppressWarnings("deprecation")
    public List<Resource> search(FhirContext fhirContext, TokenParam active, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content, StringParam _page, String sortParam, Integer count) {
        List<Resource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, resid, _lastUpdated, _tag, _profile, _query, _security,
                _content);
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
        List<SpecimenEntity> specimenEntitys = mongo.find(query, SpecimenEntity.class);
        if (specimenEntitys != null) {
            for (SpecimenEntity item : specimenEntitys) {
                Specimen specimen = transform(item);
                resources.add(specimen);
            }
        }
        return resources;
    }

    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        long total = 0;
        Criteria criteria = setParamToCriteria(active, resid, _lastUpdated, _tag, _profile, _query, _security,
                _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, SpecimenEntity.class);
        return total;
    }

    private Criteria setParamToCriteria(TokenParam active, TokenParam resid, DateRangeParam _lastUpdated,
            TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
        Criteria criteria = null;
        // active
        if (active != null) {
            criteria = Criteria.where("active").is(active);
        } else {
            criteria = Criteria.where("active").is(true);
        }
        // set param default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                null);

        return criteria;
    }

    @Override
    protected String getProfile() {
        return "Specimen-v1.0";
    }

    @Override
    protected SpecimenEntity fromFhir(Specimen obj) {
        return SpecimenEntity.fromSpecimen(obj);
    }

    @Override
    protected Specimen toFhir(SpecimenEntity ent) {
        return SpecimenEntity.toSpecimen(ent);
    }

    @Override
    protected Class<? extends BaseResource> getEntityClass() {
        return SpecimenEntity.class;
    }
}
