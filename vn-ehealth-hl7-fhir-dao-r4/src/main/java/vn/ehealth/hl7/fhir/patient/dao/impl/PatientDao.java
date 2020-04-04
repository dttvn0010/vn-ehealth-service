package vn.ehealth.hl7.fhir.patient.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.CareTeam;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DetectedIssue;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.EpisodeOfCare;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.ImagingStudy;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Media;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.hl7.fhir.r4.model.MedicationDispense;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.patient.entity.PatientEntity;

@Repository
public class PatientDao extends BaseDao<PatientEntity, Patient> {

	@SuppressWarnings("deprecation")
	public List<IBaseResource> search(FhirContext ctx, TokenParam addressUse, TokenParam animalBreed,
			TokenParam animalSpecies, TokenParam deceased, TokenParam email, TokenParam gender, TokenParam identifier,
			TokenParam language, TokenParam phone, TokenParam telecom, ReferenceParam generalPractitioner,
			ReferenceParam link, ReferenceParam organization, DateRangeParam birthDate, DateRangeParam deathDate,
			StringParam address, StringParam addressCity, StringParam addressCountry, StringParam addressState,
			StringParam familyName, StringParam givenName, StringParam name, StringParam phonetic,
			// COMMON PARAMs
			TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query,
			TokenParam _security, StringParam _content, NumberParam _page, String sortParam, Integer count) {

		List<IBaseResource> resources = new ArrayList<IBaseResource>();

		Criteria criteria = setParamToCriteria(addressUse, animalBreed, animalSpecies, deceased, email, gender,
				identifier, language, phone, telecom, generalPractitioner, link, organization, birthDate, deathDate,
				address, addressCity, addressCountry, addressState, familyName, givenName, name, phonetic, resid,
				_lastUpdated, _tag, _profile, _query, _security, _content);
		// custom
		if (criteria != null) {
			Query qry = Query.query(criteria);
			Pageable pageableRequest;
			pageableRequest = new PageRequest(
					_page != null ? Integer.valueOf(_page.getValue().intValue()) : ConstantKeys.PAGE,
					count != null ? count : ConstantKeys.DEFAULT_PAGE_SIZE);
			qry.with(pageableRequest);
			if (!sortParam.equals("")) {
				qry.with(new Sort(Sort.Direction.DESC, sortParam));
			} else {
				qry.with(new Sort(Sort.Direction.DESC, ConstantKeys.QP_UPDATED));
				qry.with(new Sort(Sort.Direction.DESC, ConstantKeys.QP_CREATED));
			}
			List<PatientEntity> patientResults = mongo.find(qry, PatientEntity.class);
			for (PatientEntity patientEntity : patientResults) {
				resources.add(transform(patientEntity));
			}
		}
		return resources;
	}

//    @Override
//    public List<Resource> searchAll(FhirContext ctx) {
//        List<Resource> resources = new ArrayList<>();
//        Query qry = Query.query(null);
//        List<PatientEntity> patientResults = mongo.find(qry, PatientEntity.class);
//        for (PatientEntity patientEntity : patientResults) {
//            resources.add(patientEntityToFHIRPatient.transform(patientEntity));
//        }
//        return resources;
//    }

	public long findMatchesAdvancedTotal(FhirContext ctx, TokenParam addressUse, TokenParam animalBreed,
			TokenParam animalSpecies, TokenParam deceased, TokenParam email, TokenParam gender, TokenParam identifier,
			TokenParam language, TokenParam phone, TokenParam telecom, ReferenceParam generalPractitioner,
			ReferenceParam link, ReferenceParam organization, DateRangeParam birthDate, DateRangeParam deathDate,
			StringParam address, StringParam addressCity, StringParam addressCountry, StringParam addressState,
			StringParam familyName, StringParam givenName, StringParam name, StringParam phonetic, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content) {

		long count = 0;
		Criteria criteria = setParamToCriteria(addressUse, animalBreed, animalSpecies, deceased, email, gender,
				identifier, language, phone, telecom, generalPractitioner, link, organization, birthDate, deathDate,
				address, addressCity, addressCountry, addressState, familyName, givenName, name, phonetic, resid,
				_lastUpdated, _tag, _profile, _query, _security, _content);
		Query query = new Query();
		if (criteria != null) {
			query = Query.query(criteria);
		}

		count = mongo.count(query, PatientEntity.class);
		return count;
	}

	private Criteria setParamToCriteria(TokenParam addressUse, TokenParam animalBreed, TokenParam animalSpecies,
			TokenParam deceased, TokenParam email, TokenParam gender, TokenParam identifier, TokenParam language,
			TokenParam phone, TokenParam telecom, ReferenceParam generalPractitioner, ReferenceParam link,
			ReferenceParam organization, DateRangeParam birthDate, DateRangeParam deathDate, StringParam address,
			StringParam addressCity, StringParam addressCountry, StringParam addressState, StringParam familyName,
			StringParam givenName, StringParam name, StringParam phonetic, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content) {
		Criteria criteria = null;
		criteria = Criteria.where("$where").is("1==1");
		// active
		criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
//                addressUse,addressUse
		if (addressUse != null) {
			criteria.and("address.addressUse").regex(addressUse.getValue());
		}
//                animalBreed,
		if (animalBreed != null) {
			// criteria.and("addresses.addressUse").regex(addressUse.getValue());
		}
//                animalSpecies,
		if (animalSpecies != null) {
			// criteria.and("addresses.addressUse").regex(addressUse.getValue());
		}
//                deceased,
		if (deceased != null) {
			// criteria.and("addresses.addressUse").regex(addressUse.getValue());
		}
//                email,
		if (email != null) {
			criteria.and("telecom.system").is("EMAIL").and("telecom.value").is(email.getValue());
		}
//                gender,
		if (gender != null && gender.getValue() != null) {
			criteria.and("gender").is(gender.getValue().toLowerCase());
		}
//                language,
		if (language != null) {
			criteria.and("communication.language.value").is(language.getValue());
		}
//                phone,
		if (phone != null) {
			criteria.and("telecom.system").is("PHONE").and("telecom.value").is(phone.getValue());
		}
//                telecom,
		if (telecom != null) {
			criteria.and("telecom.value").is(telecom.getValue());
		}
//                generalPractitioner,
		if (generalPractitioner != null) {
			criteria.orOperator(Criteria.where("generalPractitioner.reference").regex(generalPractitioner.getValue()),
					Criteria.where("generalPractitioner.display").regex(generalPractitioner.getValue()),
					Criteria.where("generalPractitioner.identifier.value").regex(generalPractitioner.getValue()),
					Criteria.where("generalPractitioner.identifier.system").regex(generalPractitioner.getValue()));
		}
//                link,
		if (link != null) {
			criteria.orOperator(Criteria.where("link.other.reference").regex(organization.getValue()),
					Criteria.where("link.other.display").regex(organization.getValue()),
					Criteria.where("link.other.identifier.value").regex(organization.getValue()),
					Criteria.where("link.other.identifier.system").regex(organization.getValue()));
		}
//                organization,
		if (organization != null) {
			criteria.orOperator(Criteria.where("managingOrganization.reference").regex(organization.getValue()),
					Criteria.where("managingOrganization.display").regex(organization.getValue()),
					Criteria.where("managingOrganization.identifier.value").regex(organization.getValue()),
					Criteria.where("managingOrganization.identifier.system").regex(organization.getValue()));
		}
//                birthDate,
		if (birthDate != null) {
			criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "birthDate", birthDate);
		}
//                deathDate,
		if (deathDate != null) {
			criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "deceased", deathDate);
		}
//                address,
		if (address != null) {
			criteria.orOperator(Criteria.where("addresses.addressLine1.myStringValue").regex(address.getValue()),
					Criteria.where("addresses.addressLine2.myStringValue").regex(address.getValue()));
		}
//                addressCity,
		if (addressCity != null) {
			criteria.and("address.city").regex(addressCity.getValue());
		}
//                addressCountry,
		if (addressCountry != null) {
			criteria.and("address.country").regex(addressCountry.getValue());
		}
//                addressState,    
		if (addressState != null) {
			criteria.and("address.state").regex(addressState.getValue());
		}
//                familyName,
		if (familyName != null) {
			if (criteria == null) {
				criteria = Criteria.where("name.family").regex(familyName.getValue());
			} else {
				criteria.and("name.family").regex(familyName.getValue());
			}
		}
//                givenName ,   
		if (givenName != null) {
			if (criteria == null) {
				criteria = Criteria.where("name.given").regex(givenName.getValue());
			} else {
				criteria.and("name.given").regex(givenName.getValue());
			}
		}
//                name,
		if (name != null) {
			String regexName = name.getValue(); // .toLowerCase()+".*"; // use options = i for regex
			criteria.orOperator(Criteria.where("name.family").regex(regexName),
					Criteria.where("name.given").regex(regexName));
		}
//                phonetic,
		if (phonetic != null) {
			String regexName = phonetic.getValue(); // .toLowerCase()+".*"; // use options = i for regex
			criteria.orOperator(Criteria.where("name.family").regex(regexName),
					Criteria.where("name.given").regex(regexName));
		}
		// default
		criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
				identifier);
		return criteria;
	}

	public List<IBaseResource> getEverything(@IdParam IdType thePatientId, DateParam theStart, DateParam theEnd) {

		List<IBaseResource> resources = new ArrayList<IBaseResource>();

		if (thePatientId != null) {
			Patient patient = read(thePatientId);
			if (patient != null) {
				resources.add(patient);
				// active
				Criteria criteria = Criteria.where(ConstantKeys.QP_ACTIVE).is(true);
				// criteria.and("subject.reference").is(thePatientId.asStringValue());
				criteria.andOperator(
						new Criteria().orOperator(Criteria.where("subject.reference").is(thePatientId.asStringValue()),
								Criteria.where("patient.reference").is(thePatientId.asStringValue())));
				if (theStart != null) {
					criteria.and(ConstantKeys.QP_UPDATED).gte(theStart.getValue());
				}
				if (theEnd != null) {
					criteria.and(ConstantKeys.QP_UPDATED).lte(theEnd.getValue());
				}
				// Encounter
				List<Encounter> encounters = DaoFactory.getEncounterDao().findByCriteria(criteria);
				if (encounters != null && encounters.size() > 0) {
					resources.addAll(encounters);
				}
				// EpisodeOfCare
				List<EpisodeOfCare> episodeOfCares = DaoFactory.getEpisodeOfCareDao().findByCriteria(criteria);
				if (episodeOfCares != null && episodeOfCares.size() > 0) {
					resources.addAll(episodeOfCares);
				}
				// CarePlan
				List<CarePlan> carePlans = DaoFactory.getCarePlanDao().findByCriteria(criteria);
				if (carePlans != null && carePlans.size() > 0) {
					resources.addAll(carePlans);
				}
				// Condition
				List<Condition> conditions = DaoFactory.getConditionDao().findByCriteria(criteria);
				if (conditions != null && conditions.size() > 0) {
					resources.addAll(conditions);
				}
				// DetectedIssue
				List<DetectedIssue> detectedIssues = DaoFactory.getDetectedIssueDao().findByCriteria(criteria);
				if (detectedIssues != null && detectedIssues.size() > 0) {
					resources.addAll(detectedIssues);
				}
				// Goal
				List<Goal> goals = DaoFactory.getGoalDao().findByCriteria(criteria);
				if (goals != null && goals.size() > 0) {
					resources.addAll(goals);
				}
				// Procedure
				List<Procedure> procedures = DaoFactory.getProcedureDao().findByCriteria(criteria);
				if (procedures != null && procedures.size() > 0) {
					resources.addAll(procedures);
				}
				// ServiceRequest
				List<ServiceRequest> serviceRequests = DaoFactory.getServiceRequestDao().findByCriteria(criteria);
				if (serviceRequests != null && serviceRequests.size() > 0) {
					resources.addAll(serviceRequests);
				}
				// DiagnosticReport
				List<DiagnosticReport> diagnosticReports = DaoFactory.getDiagnosticReportDao().findByCriteria(criteria);
				if (diagnosticReports != null && diagnosticReports.size() > 0) {
					resources.addAll(diagnosticReports);
				}
				// ImagingStudy
				List<ImagingStudy> imagingStudys = DaoFactory.getImagingStudyDao().findByCriteria(criteria);
				if (imagingStudys != null && imagingStudys.size() > 0) {
					resources.addAll(imagingStudys);
				}
				// Specimen
				List<Specimen> specimens = DaoFactory.getSpecimenDao().findByCriteria(criteria);
				if (specimens != null && specimens.size() > 0) {
					resources.addAll(specimens);
				}
				// CareTeam
				List<CareTeam> careTeams = DaoFactory.getCareTeamDao().findByCriteria(criteria);
				if (careTeams != null && careTeams.size() > 0) {
					resources.addAll(careTeams);
				}
				// Immunization
				List<Immunization> immunizations = DaoFactory.getImmunizationDao().findByCriteria(criteria);
				if (immunizations != null && immunizations.size() > 0) {
					resources.addAll(immunizations);
				}
				// MedicationAdministration
				List<MedicationAdministration> medicationAdministrations = DaoFactory.getMedicationAdministrationDao()
						.findByCriteria(criteria);
				if (medicationAdministrations != null && medicationAdministrations.size() > 0) {
					resources.addAll(medicationAdministrations);
				}
				// MedicationDispense
				List<MedicationDispense> medicationDispenses = DaoFactory.getMedicationDispenseDao()
						.findByCriteria(criteria);
				if (medicationDispenses != null && medicationDispenses.size() > 0) {
					resources.addAll(medicationDispenses);
				}
				// MedicationRequest
				List<MedicationRequest> medicationRequests = DaoFactory.getMedicationRequestDao()
						.findByCriteria(criteria);
				if (medicationRequests != null && medicationRequests.size() > 0) {
					resources.addAll(medicationRequests);
				}
				// MedicationStatement
				List<MedicationStatement> medicationStatements = DaoFactory.getMedicationStatementDao()
						.findByCriteria(criteria);
				if (medicationStatements != null && medicationStatements.size() > 0) {
					resources.addAll(medicationStatements);
				}
				// RelatedPerson
				List<RelatedPerson> relatedPersons = DaoFactory.getRelatedPersonDao().findByCriteria(criteria);
				if (relatedPersons != null && relatedPersons.size() > 0) {
					resources.addAll(relatedPersons);
				}
				// Observation
				List<Observation> observations = DaoFactory.getObservationDao().findByCriteria(criteria);
				if (observations != null && observations.size() > 0) {
					resources.addAll(observations);
				}
				// FamilyMemberHistory
				List<FamilyMemberHistory> familyMemberHistorys = DaoFactory.getFamilyMemberHistoryDao()
						.findByCriteria(criteria);
				if (familyMemberHistorys != null && familyMemberHistorys.size() > 0) {
					resources.addAll(familyMemberHistorys);
				}
				// AllergyIntolerance
				List<AllergyIntolerance> allergyIntolerances = DaoFactory.getAllergyIntoleranceDao()
						.findByCriteria(criteria);
				if (allergyIntolerances != null && allergyIntolerances.size() > 0) {
					resources.addAll(allergyIntolerances);
				}
				// Media
				List<Media> medias = DaoFactory.getMediaDao().findByCriteria(criteria);
				if (medias != null && medias.size() > 0) {
					resources.addAll(medias);
				}
			}
		}

		return resources;
	}

	@Override
	protected String getProfile() {
		return "Patient-v1.0";
	}

	@Override
	protected Class<? extends DomainResource> getResourceClass() {
		return Patient.class;
	}

	@Override
	protected Class<? extends BaseResource> getEntityClass() {
		return PatientEntity.class;
	}

}
