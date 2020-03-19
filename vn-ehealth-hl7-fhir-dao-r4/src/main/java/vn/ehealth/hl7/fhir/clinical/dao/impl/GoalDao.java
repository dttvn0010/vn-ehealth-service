package vn.ehealth.hl7.fhir.clinical.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.clinical.entity.GoalEntity;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;

@Repository
public class GoalDao extends BaseDao<GoalEntity, Goal> {

    @SuppressWarnings("deprecation")
    public List<IBaseResource> search(FhirContext fhirContext, TokenParam active, TokenParam category, TokenParam identifier,
            ReferenceParam patient, DateRangeParam startDate, TokenParam status, ReferenceParam subject,
            DateRangeParam targetDate, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content, StringParam _page,
            String sortParam, Integer count, Set<Include> includes) {
        List<IBaseResource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, category, identifier, patient, startDate, status, subject,
                targetDate, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
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
        List<GoalEntity> goalEntitys = mongo.find(query, GoalEntity.class);
        if (goalEntitys != null) {
            for (GoalEntity item : goalEntitys) {
                Goal obj = transform(item);
				// add more Resource as it's references
				if (includes != null && includes.size() > 0 && includes.contains(new Include("*"))) {
					if (obj.getSubject() != null) {
						Resource nested = DatabaseUtil.getResourceFromReference(obj.getSubject());
						if (nested != null) {
							obj.getSubject().setResource(nested);
//							if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//								resources.add(nested);
						}
					}
					if (obj.getExpressedBy() != null) {
						Resource nested = DatabaseUtil.getResourceFromReference(obj.getExpressedBy());
						if (nested != null) {
							obj.getExpressedBy().setResource(nested);
//							if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//								resources.add(nested);
						}
					}
					if (obj.getAddresses() != null && obj.getAddresses().size() > 0) {
						for (Reference ref : obj.getAddresses()) {
							Resource nested = DatabaseUtil.getResourceFromReference(ref);
							if (nested != null) {
								ref.setResource(nested);
//								if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//									resources.add(nested);
							}
						}
					}

				} else {
					if (includes != null && includes.size() > 0 && includes.contains(new Include("Goal:subject"))
							&& obj.getSubject() != null) {
						Resource nested = DatabaseUtil.getResourceFromReference(obj.getSubject());
						if (nested != null) {
							obj.getSubject().setResource(nested);
//							if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//								resources.add(nested);
						}
					}
				}
                resources.add(obj);
            }
        }
        return resources;
    }

    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, TokenParam category,
            TokenParam identifier, ReferenceParam patient, DateRangeParam startDate, TokenParam status,
            ReferenceParam subject, DateRangeParam targetDate, TokenParam resid, DateRangeParam _lastUpdated,
            TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
        long total = 0;
        Criteria criteria = setParamToCriteria(active, category, identifier, patient, startDate, status, subject,
                targetDate, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, GoalEntity.class);
        return total;
    }

    private Criteria setParamToCriteria(TokenParam active, TokenParam category, TokenParam identifier,
            ReferenceParam patient, DateRangeParam startDate, TokenParam status, ReferenceParam subject,
            DateRangeParam targetDate, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
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

        // category
        if (category != null && !category.isEmpty()) {
            criteria.and("category.coding.system").is(category.getSystem()).and("category.coding.code")
                    .is(category.getValue());
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
        // start-date
        if (startDate != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "start", startDate);
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
        // target-date
        if (targetDate != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "target.due", targetDate);
        }
        return criteria;
    }

    @Override
    protected String getProfile() {
        return "Goal-v1.0";
    }

    @Override
    protected GoalEntity fromFhir(Goal obj) {
        return GoalEntity.fromGoalEntity(obj);
    }

    @Override
    protected Goal toFhir(GoalEntity ent) {
        return GoalEntity.toGoal(ent);
    }

    @Override
    protected Class<? extends BaseResource> getEntityClass() {
        return GoalEntity.class;
    }

}
