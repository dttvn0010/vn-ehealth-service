package vn.ehealth.hl7.fhir.clinical.dao.impl;

import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.getIncludeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.clinical.entity.FamilyMemberHistoryEntity;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.*;

@Repository
public class FamilyMemberHistoryDao extends BaseDao<FamilyMemberHistoryEntity, FamilyMemberHistory> {

	@Override
	protected String getProfile() {
		return "FamilyMemberHistory-v1.0";
	}

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return FamilyMemberHistoryEntity.class;
	}

	@Override
    protected Class<? extends DomainResource> getResourceClass() {
        return FamilyMemberHistory.class;
    }

	@SuppressWarnings("deprecation")
	public List<IBaseResource> search(TokenParam active, TokenParam code, DateRangeParam date, TokenParam identifier,
			ReferenceParam instantiatesCanonical, UriParam instantiatesUri, ReferenceParam patient,
			TokenParam relationship, TokenParam gender, TokenParam status,
			// Common
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, StringParam _page, String sortParam, Integer count,
			Set<Include> includes) {
		List<IBaseResource> resources = new ArrayList<IBaseResource>();
		Criteria criteria = setParamToCriteria(active, code, date, identifier, patient, status, resid, _lastUpdated,
				_tag, _profile, _query, _security, _content);
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
			query.with(new Sort(Sort.Direction.DESC, "resUpdated"));
			query.with(new Sort(Sort.Direction.DESC, "resCreated"));
		}
		
		String[] keys = {"patient", "reasonReference"};

        var includeMap = getIncludeMap(ResourceType.FamilyMemberHistory, keys, includes);
        
		List<FamilyMemberHistoryEntity> entitys = mongo.find(query, FamilyMemberHistoryEntity.class);
		if (entitys != null && entitys.size() > 0) {
			for (FamilyMemberHistoryEntity item : entitys) {
				FamilyMemberHistory obj = transform(item);

				if(includeMap.get("patient") && obj.hasPatient()) {
				    setReferenceResource(obj.getPatient());
				}
				
				if(includeMap.get("reasonReference") && obj.hasReasonReference()) {
                    setReferenceResource(obj.getReasonReference());
                }
				resources.add(obj);
			}
		}
		return null;
	}

	public long countMatchesAdvancedTotal(TokenParam active, TokenParam code, DateRangeParam date,
			TokenParam identifier, ReferenceParam instantiatesCanonical, UriParam instantiatesUri,
			ReferenceParam patient, TokenParam relationship, TokenParam gender, TokenParam status,
			// Common
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content) {
		long total = 0;
		Criteria criteria = setParamToCriteria(active, code, date, identifier, patient, status, resid, _lastUpdated,
				_tag, _profile, _query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, FamilyMemberHistoryEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(TokenParam active, TokenParam code, DateRangeParam date, TokenParam identifier,
			ReferenceParam patient, TokenParam status, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
			UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
		Criteria criteria = null;
		// active
		if (active != null) {
			criteria = Criteria.where("active").is(active);
		} else {
			criteria = Criteria.where("active").is(true);
		}
		// set param default
		criteria = addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
				identifier);
		// code
		if (code != null) {
			criteria.and("code.coding.code.myStringValue").is(code.getValue());
		}
		// date
		if (date != null) {
			criteria = setTypeDateToCriteria(criteria, "performed", date);
		}
		// patient
		if (patient != null) {
			if (patient.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("patient.reference").is(patient.getValue()),
						Criteria.where("patient.display").is(patient.getValue()));
			} else {
				String[] ref = patient.getValue().split("\\|");
				criteria.and("patient.identifier.system").is(ref[0]).and("patient.identifier.value").is(ref[1]);
			}
		}
		// status
		if (status != null) {
			criteria.and("status").is(status.getValue());
		}
		return criteria;
	}

}
