package vn.ehealth.hl7.fhir.clinical.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.CarePlan;
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
import vn.ehealth.hl7.fhir.clinical.entity.CarePlanEntity;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;

@Repository
public class CarePlanDao extends BaseDao<CarePlanEntity, CarePlan> {

	@SuppressWarnings("deprecation")
	public List<IBaseResource> search(FhirContext fhirContext, TokenParam active, TokenParam activityCode,
			DateRangeParam activityDate, ReferenceParam activityReference, ReferenceParam basedOn,
			ReferenceParam careTeam, TokenParam category, ReferenceParam condition, ReferenceParam context,
			DateRangeParam date, ReferenceParam definition, ReferenceParam encounter, ReferenceParam goal,
			TokenParam identifier, TokenParam intent, ReferenceParam partOf, ReferenceParam patient,
			ReferenceParam performer, ReferenceParam replaces, TokenParam status, ReferenceParam subject,
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, StringParam _page, String sortParam, Integer count,
			Set<Include> includes) {
		List<IBaseResource> resources = new ArrayList<>();
		Criteria criteria = setParamToCriteria(active, activityCode, activityDate, activityReference, basedOn, careTeam,
				category, condition, context, date, definition, encounter, goal, identifier, intent, partOf, patient,
				performer, replaces, status, subject, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		Pageable pageableRequest;
		pageableRequest = new PageRequest(_page != null ? Integer.valueOf(_page.getValue()) : ConstantKeys.PAGE,
				count != null ? count : ConstantKeys.DEFAULT_PAGE_SIZE);
		query.with(pageableRequest);
		if (sortParam != null && !sortParam.equals("")) {
			query.with(new Sort(Sort.Direction.ASC, sortParam));
		}
		List<CarePlanEntity> carePlanEntitys = mongo.find(query, CarePlanEntity.class);
		if (carePlanEntitys != null) {
			for (CarePlanEntity item : carePlanEntitys) {
				CarePlan obj = transform(item);

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
					if (obj.getEncounter() != null) {
						Resource nested = DatabaseUtil.getResourceFromReference(obj.getEncounter());
						if (nested != null) {
							obj.getEncounter().setResource(nested);
//							if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//								resources.add(nested);
						}
					}
					if (obj.getBasedOn() != null && obj.getBasedOn().size() > 0) {
						for (Reference ref : obj.getBasedOn()) {
							Resource nested = DatabaseUtil.getResourceFromReference(ref);
							if (nested != null) {
								ref.setResource(nested);
//								if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//									resources.add(nested);
							}
						}
					}
					if (obj.getAuthor() != null) {
						Resource nested = DatabaseUtil.getResourceFromReference(obj.getAuthor());
						if (nested != null) {
							obj.getAuthor().setResource(nested);
//							if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//								resources.add(nested);
						}
					}
					if (obj.getContributor() != null && obj.getContributor().size() > 0) {
						for (Reference ref : obj.getContributor()) {
							Resource nested = DatabaseUtil.getResourceFromReference(ref);
							if (nested != null) {
								ref.setResource(nested);
//								if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//									resources.add(nested);
							}
						}
					}
					if (obj.getCareTeam() != null && obj.getCareTeam().size() > 0) {
						for (Reference ref : obj.getCareTeam()) {
							Resource nested = DatabaseUtil.getResourceFromReference(ref);
							if (nested != null) {
								ref.setResource(nested);
//								if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//									resources.add(nested);
							}
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
					if (obj.getGoal() != null && obj.getGoal().size() > 0) {
						for (Reference ref : obj.getGoal()) {
							Resource nested = DatabaseUtil.getResourceFromReference(ref);
							if (nested != null) {
								ref.setResource(nested);
//								if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//									resources.add(nested);
							}
						}
					}
				} else {
					if (includes != null && includes.size() > 0 && includes.contains(new Include("CarePlan:subject"))
							&& obj.getSubject() != null) {
						Resource nested = DatabaseUtil.getResourceFromReference(obj.getSubject());
						if (nested != null) {
							obj.getSubject().setResource(nested);
//							if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//								resources.add(nested);
						}
					}
					if (includes != null && includes.size() > 0
							&& includes.contains(new Include("CarePlan:encounter")) && obj.getEncounter() != null) {
						Resource nested = DatabaseUtil.getResourceFromReference(obj.getEncounter());
						if (nested != null) {
							obj.getEncounter().setResource(nested);
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

	public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, TokenParam activityCode,
			DateRangeParam activityDate, ReferenceParam activityReference, ReferenceParam basedOn,
			ReferenceParam careTeam, TokenParam category, ReferenceParam condition, ReferenceParam context,
			DateRangeParam date, ReferenceParam definition, ReferenceParam encounter, ReferenceParam goal,
			TokenParam identifier, TokenParam intent, ReferenceParam partOf, ReferenceParam patient,
			ReferenceParam performer, ReferenceParam replaces, TokenParam status, ReferenceParam subject,
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content) {
		long total = 0;
		Criteria criteria = setParamToCriteria(active, activityCode, activityDate, activityReference, basedOn, careTeam,
				category, condition, context, date, definition, encounter, goal, identifier, intent, partOf, patient,
				performer, replaces, status, subject, resid, _lastUpdated, _tag, _profile, _query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, CarePlanEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(TokenParam active, TokenParam activityCode, DateRangeParam activityDate,
			ReferenceParam activityReference, ReferenceParam basedOn, ReferenceParam careTeam, TokenParam category,
			ReferenceParam condition, ReferenceParam context, DateRangeParam date, ReferenceParam definition,
			ReferenceParam encounter, ReferenceParam goal, TokenParam identifier, TokenParam intent,
			ReferenceParam partOf, ReferenceParam patient, ReferenceParam performer, ReferenceParam replaces,
			TokenParam status, ReferenceParam subject, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
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

		// activity-code
		if (activityCode != null) {
			/** not write **/
		}
		// activity-date
		if (activityDate != null) {
			/** not write **/
		}
		// activity-reference
		if (activityReference != null) {
			/** not write **/
		}
		// based-on
		if (basedOn != null) {
			if (basedOn.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("basedOn.reference").is(basedOn.getValue()),
						Criteria.where("basedOn.display").is(basedOn.getValue()));
			} else {
				String[] ref = basedOn.getValue().split("\\|");
				criteria.and("basedOn.identifier.system").is(ref[0]).and("basedOn.identifier.value").is(ref[1]);
			}
		}
		// care-team
		if (careTeam != null) {
			if (careTeam.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("careTeam.reference").is(careTeam.getValue()),
						Criteria.where("careTeam.display").is(careTeam.getValue()));
			} else {
				String[] ref = careTeam.getValue().split("\\|");
				criteria.and("careTeam.identifier.system").is(ref[0]).and("careTeam.identifier.value").is(ref[1]);
			}
		}
		// category
		if (category != null && !category.isEmpty()) {
			criteria.and("category.coding.system").is(category.getSystem()).and("category.coding.code")
					.is(category.getValue());
		}
		// condition
		if (condition != null) {
			if (condition.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("addresses.reference").is(condition.getValue()),
						Criteria.where("addresses.display").is(basedOn.getValue()));
			} else {
				String[] ref = condition.getValue().split("\\|");
				criteria.and("addresses.identifier.system").is(ref[0]).and("addresses.identifier.value").is(ref[1]);
			}
		}
		// context
		if (context != null) {
			if (context.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("context.reference").is(context.getValue()),
						Criteria.where("context.display").is(basedOn.getValue()));
			} else {
				String[] ref = context.getValue().split("\\|");
				criteria.and("context.identifier.system").is(ref[0]).and("context.identifier.value").is(ref[1]);
			}
		}
		// date
		if (date != null) {
			criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "date", date);
		}
		// definition
		if (definition != null) {
			if (definition.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("definition.reference").is(definition.getValue()),
						Criteria.where("definition.display").is(definition.getValue()));
			} else {
				String[] ref = definition.getValue().split("\\|");
				criteria.and("definition.identifier.system").is(ref[0]).and("definition.identifier.value").is(ref[1]);
			}
		}
		// definition
		if (encounter != null) {
			if (encounter.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("context.reference").is(encounter.getValue()),
						Criteria.where("context.display").is(encounter.getValue()));
			} else {
				String[] ref = encounter.getValue().split("\\|");
				criteria.and("context.identifier.system").is(ref[0]).and("context.identifier.value").is(ref[1]);
			}
		}
		// goal
		if (goal != null) {
			if (goal.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("goal.reference").is(goal.getValue()),
						Criteria.where("goal.display").is(goal.getValue()));
			} else {
				String[] ref = goal.getValue().split("\\|");
				criteria.and("goal.identifier.system").is(ref[0]).and("goal.identifier.value").is(ref[1]);
			}
		}
		// intent
		if (intent != null) {
			criteria.and("intent").is(intent.getValue());
		}
		// part-of
		if (partOf != null) {
			if (partOf.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("partOf.reference").is(partOf.getValue()),
						Criteria.where("partOf.display").is(partOf.getValue()));
			} else {
				String[] ref = partOf.getValue().split("\\|");
				criteria.and("partOf.identifier.system").is(ref[0]).and("partOf.identifier.value").is(ref[1]);
			}
		}
		// patient
		if (patient != null) {
			if (patient.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("subject.reference").is(patient.getValue()),
						Criteria.where("subject.display").is(patient.getValue()));
			} else {
				String[] ref = patient.getValue().split("\\|");
				criteria.and("subject.identifier.system").is(ref[0]).and("subject.identifier.value").is(ref[1]);
			}
		}
		// performer
		if (performer != null) {
			if (performer.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("performer.reference").is(performer.getValue()),
						Criteria.where("performer.display").is(performer.getValue()));
			} else {
				String[] ref = performer.getValue().split("\\|");
				criteria.and("performer.identifier.system").is(ref[0]).and("performer.identifier.value").is(ref[1]);
			}
		}
		// replaces
		if (replaces != null) {
			if (replaces.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("replaces.reference").is(replaces.getValue()),
						Criteria.where("replaces.display").is(replaces.getValue()));
			} else {
				String[] ref = replaces.getValue().split("\\|");
				criteria.and("replaces.identifier.system").is(ref[0]).and("replaces.identifier.value").is(ref[1]);
			}
		}
		// status
		if (status != null) {
			criteria.and("status").is(status.getValue());
		}
		// subject
		if (subject != null) {
			if (subject.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("subject.reference").is(subject.getValue()),
						Criteria.where("subject.display").is(subject.getValue()));
			} else {
				String[] ref = subject.getValue().split("\\|");
				criteria.and("subject.identifier.system").is(ref[0]).and("subject.identifier.value").is(ref[1]);
			}
		}
		return criteria;
	}

	@Override
	protected String getProfile() {
		return "CarePlan-v1.0";
	}

	@Override
	protected CarePlanEntity fromFhir(CarePlan obj) {
		return CarePlanEntity.fromCarePlan(obj);
	}

	@Override
	protected CarePlan toFhir(CarePlanEntity ent) {
		return CarePlanEntity.toCarePlan(ent);
	}

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return CarePlanEntity.class;
	}
}
