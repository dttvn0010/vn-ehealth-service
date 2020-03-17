package vn.ehealth.hl7.fhir.patient.dao;

import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.instance.model.api.IBaseResource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;

public interface IPatient {

	Patient create(FhirContext fhirContext, Patient object);

	Patient update(FhirContext fhirContext, Patient object, IdType idType);

	Patient read(FhirContext fhirContext, IdType idType);

	Patient remove(FhirContext fhirContext, IdType idType);

	Patient readOrVread(FhirContext fhirContext, IdType idType);

	List<IBaseResource> search(FhirContext ctx, TokenParam active, TokenParam addressUse, TokenParam animalBreed,
			TokenParam animalSpecies, TokenParam deceased, TokenParam email, TokenParam gender, TokenParam identifier,
			TokenParam language, TokenParam phone, TokenParam telecom, ReferenceParam generalPractitioner,
			ReferenceParam link, ReferenceParam organization, DateRangeParam birthDate, DateRangeParam deathDate,
			StringParam address, StringParam addressCity, StringParam addressCountry, StringParam addressState,
			StringParam familyName, StringParam givenName, StringParam name, StringParam phonetic, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content, StringParam _page, String sortParam, Integer count);

	long findMatchesAdvancedTotal(FhirContext ctx, TokenParam active, TokenParam addressUse, TokenParam animalBreed,
			TokenParam animalSpecies, TokenParam deceased, TokenParam email, TokenParam gender, TokenParam identifier,
			TokenParam language, TokenParam phone, TokenParam telecom, ReferenceParam generalPractitioner,
			ReferenceParam link, ReferenceParam organization, DateRangeParam birthDate, DateRangeParam deathDate,
			StringParam address, StringParam addressCity, StringParam addressCountry, StringParam addressState,
			StringParam familyName, StringParam givenName, StringParam name, StringParam phonetic, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content);

	List<Patient> getHistory(IdType theId, InstantType theSince, DateRangeParam theAt, StringParam _page,
			Integer count);
}
