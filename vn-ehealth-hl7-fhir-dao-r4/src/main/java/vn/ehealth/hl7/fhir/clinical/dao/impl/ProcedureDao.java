package vn.ehealth.hl7.fhir.clinical.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ResourceType;
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
import vn.ehealth.hl7.fhir.clinical.entity.ProcedureEntity;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.*;

@Repository
public class ProcedureDao extends BaseDao<ProcedureEntity, Procedure> {

	@SuppressWarnings("deprecation")
	public List<IBaseResource> search(ReferenceParam basedOn, TokenParam category, TokenParam code,
			ReferenceParam context, DateRangeParam date, ReferenceParam definition, ReferenceParam encounter,
			TokenParam identifier, ReferenceParam location, ReferenceParam partOf, ReferenceParam patient,
			ReferenceParam performer, TokenParam status, ReferenceParam subject, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content, StringParam _page, String sortParam, Integer count, Set<Include> includes) {
		List<IBaseResource> resources = new ArrayList<IBaseResource>();
		Criteria criteria = setParamToCriteria(basedOn, category, code, context, date, definition, encounter,
				identifier, location, partOf, patient, performer, status, subject, resid, _lastUpdated, _tag, _profile,
				_query, _security, _content);
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

		String[] keys = { "subject", "encounter", "encounter:serviceProvider", "encounter:appointment", "basedOn",
				"asserter", "recorder", "report", "location", "reasonReference", "performer:actor",
				"performer:onBehalfOf", "complicationDetail", "usedReference" };

		var includeMap = getIncludeMap(ResourceType.Procedure, keys, includes);

		List<ProcedureEntity> procedureEntitys = mongo.find(query, ProcedureEntity.class);
		if (procedureEntitys != null) {
			for (ProcedureEntity item : procedureEntitys) {
				Procedure obj = transform(item);

				if (includeMap.get("subject") && obj.hasSubject()) {
					setReferenceResource(obj.getSubject());
				}

				if (includeMap.get("encounter") && obj.hasEncounter()) {
					setReferenceResource(obj.getEncounter());

					var enc = (Encounter) obj.getEncounter().getResource();

					if (includeMap.get("encounter:serviceProvider") && enc != null) {
						setReferenceResource(enc.getServiceProvider());
					}

					if (includeMap.get("encounter:appointment") && enc != null) {
						setReferenceResource(enc.getAppointment());
					}
				}

				if (includeMap.get("basedOn") && obj.hasBasedOn()) {
					setReferenceResource(obj.getBasedOn());
				}

				if (includeMap.get("asserter") && obj.hasAsserter()) {
					setReferenceResource(obj.getAsserter());
				}

				if (includeMap.get("recorder") && obj.hasRecorder()) {
					setReferenceResource(obj.getRecorder());
				}

				if (includeMap.get("report") && obj.hasReport()) {
					setReferenceResource(obj.getReport());
				}

				if (includeMap.get("location") && obj.hasLocation()) {
					setReferenceResource(obj.getLocation());
				}

				if (includeMap.get("reasonReference") && obj.hasReasonReference()) {
					setReferenceResource(obj.getReasonReference());
				}

				if (includeMap.get("performer:actor") && obj.hasPerformer()) {
					obj.getPerformer().forEach(x -> setReferenceResource(x.getActor()));
				}

				if (includeMap.get("performer:onBehalfOf") && obj.hasPerformer()) {
					obj.getPerformer().forEach(x -> setReferenceResource(x.getOnBehalfOf()));
				}

				if (includeMap.get("complicationDetail") && obj.hasComplicationDetail()) {
					setReferenceResource(obj.getComplicationDetail());
				}

				if (includeMap.get("usedReference") && obj.hasUsedReference()) {
					setReferenceResource(obj.getUsedReference());
				}
				resources.add(obj);
			}
		}
		return resources;
	}

	public long countMatchesAdvancedTotal(FhirContext fhirContext, ReferenceParam basedOn, TokenParam category,
			TokenParam code, ReferenceParam context, DateRangeParam date, ReferenceParam definition,
			ReferenceParam encounter, TokenParam identifier, ReferenceParam location, ReferenceParam partOf,
			ReferenceParam patient, ReferenceParam performer, TokenParam status, ReferenceParam subject,
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content) {
		long total = 0;
		Criteria criteria = setParamToCriteria(basedOn, category, code, context, date, definition, encounter,
				identifier, location, partOf, patient, performer, status, subject, resid, _lastUpdated, _tag, _profile,
				_query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}
		total = mongo.count(query, ProcedureEntity.class);
		return total;
	}

	private Criteria setParamToCriteria(ReferenceParam basedOn, TokenParam category, TokenParam code,
			ReferenceParam context, DateRangeParam date, ReferenceParam definition, ReferenceParam encounter,
			TokenParam identifier, ReferenceParam location, ReferenceParam partOf, ReferenceParam patient,
			ReferenceParam performer, TokenParam status, ReferenceParam subject, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content) {
		Criteria criteria = null;
		// active
		criteria = Criteria.where("active").is(true);
		// set param default
		criteria = addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security, identifier);
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
		// category
		if (category != null) {
			criteria.and("category.coding.code").is(category.getValue()).and("category.coding.system")
					.is(category.getSystem());
		}
		// code
		if (code != null) {
			criteria.and("code.coding.code.myStringValue").is(code.getValue());
		}
		// context
		if (context != null) {
			if (context.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("context.reference").is(context.getValue()),
						Criteria.where("context.display").is(context.getValue()));
			} else {
				String[] ref = context.getValue().split("\\|");
				criteria.and("context.identifier.system").is(ref[0]).and("context.identifier.value").is(ref[1]);
			}
		}
		// date
		if (date != null) {
			criteria = setTypeDateToCriteria(criteria, "performed", date);
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
		// encounter
		if (encounter != null) {
			if (encounter.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("encounter.reference").is(encounter.getValue()),
						Criteria.where("encounter.display").is(encounter.getValue()));
			} else {
				String[] ref = encounter.getValue().split("\\|");
				criteria.and("encounter.identifier.system").is(ref[0]).and("encounter.identifier.value").is(ref[1]);
			}
		}
		// location
		if (location != null) {
			if (location.getValue().indexOf("|") == -1) {
				criteria.orOperator(Criteria.where("location.location.reference").is(location.getValue()),
						Criteria.where("location.location.display").is(location.getValue()));
			} else {
				String[] ref = location.getValue().split("\\|");
				criteria.and("location.location.identifier.system").is(ref[0]).and("location.location.identifier.value")
						.is(ref[1]);
			}
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

	protected String getProfile() {
		return "Procedure-v1.0";
	}

	@Override
    public Class<? extends DomainResource> getResourceClass() {
        return Procedure.class;
    }

	@Override
	public Class<? extends BaseResource> getEntityClass() {
		return ProcedureEntity.class;
	}

}
