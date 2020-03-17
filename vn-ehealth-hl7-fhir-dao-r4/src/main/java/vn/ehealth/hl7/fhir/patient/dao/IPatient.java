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

	List<IBaseResource> search(FhirContext ctx, @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
			@OptionalParam(name = ConstantKeys.SP_ADDRESS_USE) TokenParam addressUse,
			@OptionalParam(name = ConstantKeys.SP_ANIMAL_BREED) TokenParam animalBreed,
			@OptionalParam(name = ConstantKeys.SP_ANIMAL_SPECIES) TokenParam animalSpecies,
			@OptionalParam(name = ConstantKeys.SP_DECEASED) TokenParam deceased,
			@OptionalParam(name = ConstantKeys.SP_EMAIL) TokenParam email,
			@OptionalParam(name = ConstantKeys.SP_GENDER) TokenParam gender,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ConstantKeys.SP_LANGUAGE) TokenParam language,
			@OptionalParam(name = ConstantKeys.SP_PHONE) TokenParam phone,
			@OptionalParam(name = ConstantKeys.SP_TELECOM) TokenParam telecom,
			@OptionalParam(name = ConstantKeys.SP_GENERAL_PRACTITIONER) ReferenceParam generalPractitioner,
			@OptionalParam(name = ConstantKeys.SP_LINK) ReferenceParam link,
			@OptionalParam(name = ConstantKeys.SP_ORGANIZATION) ReferenceParam organization,
			@OptionalParam(name = ConstantKeys.SP_BIRTHDATE) DateRangeParam birthDate,
			@OptionalParam(name = ConstantKeys.SP_DEATH_DATE) DateRangeParam deathDate,
			@OptionalParam(name = ConstantKeys.SP_ADDRESS) StringParam address,
			@OptionalParam(name = ConstantKeys.SP_ADDDRESSCITY) StringParam addressCity,
			@OptionalParam(name = ConstantKeys.SP_ADDRESSCOUNTRY) StringParam addressCountry,
			@OptionalParam(name = ConstantKeys.SP_ADDRESSSTATE) StringParam addressState,
			@OptionalParam(name = ConstantKeys.SP_FAMILY) StringParam familyName,
			@OptionalParam(name = ConstantKeys.SP_GIVEN) StringParam givenName,
			@OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
			@OptionalParam(name = ConstantKeys.SP_PHONETIC) StringParam phonetic,
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content,
			@OptionalParam(name = ConstantKeys.SP_PAGE) StringParam _page, String sortParam, Integer count);

	// List<Resource> searchAll(FhirContext ctx);

	long findMatchesAdvancedTotal(FhirContext ctx, @OptionalParam(name = ConstantKeys.SP_ACTIVE) TokenParam active,
			@OptionalParam(name = ConstantKeys.SP_ADDRESS_USE) TokenParam addressUse,
			@OptionalParam(name = ConstantKeys.SP_ANIMAL_BREED) TokenParam animalBreed,
			@OptionalParam(name = ConstantKeys.SP_ANIMAL_SPECIES) TokenParam animalSpecies,
			@OptionalParam(name = ConstantKeys.SP_DECEASED) TokenParam deceased,
			@OptionalParam(name = ConstantKeys.SP_EMAIL) TokenParam email,
			@OptionalParam(name = ConstantKeys.SP_GENDER) TokenParam gender,
			@OptionalParam(name = ConstantKeys.SP_IDENTIFIER) TokenParam identifier,
			@OptionalParam(name = ConstantKeys.SP_LANGUAGE) TokenParam language,
			@OptionalParam(name = ConstantKeys.SP_PHONE) TokenParam phone,
			@OptionalParam(name = ConstantKeys.SP_TELECOM) TokenParam telecom,
			@OptionalParam(name = ConstantKeys.SP_GENERAL_PRACTITIONER) ReferenceParam generalPractitioner,
			@OptionalParam(name = ConstantKeys.SP_LINK) ReferenceParam link,
			@OptionalParam(name = ConstantKeys.SP_ORGANIZATION) ReferenceParam organization,
			@OptionalParam(name = ConstantKeys.SP_BIRTHDATE) DateRangeParam birthDate,
			@OptionalParam(name = ConstantKeys.SP_DEATH_DATE) DateRangeParam deathDate,
			@OptionalParam(name = ConstantKeys.SP_ADDRESS) StringParam address,
			@OptionalParam(name = ConstantKeys.SP_ADDDRESSCITY) StringParam addressCity,
			@OptionalParam(name = ConstantKeys.SP_ADDRESSCOUNTRY) StringParam addressCountry,
			@OptionalParam(name = ConstantKeys.SP_ADDRESSSTATE) StringParam addressState,
			@OptionalParam(name = ConstantKeys.SP_FAMILY) StringParam familyName,
			@OptionalParam(name = ConstantKeys.SP_GIVEN) StringParam givenName,
			@OptionalParam(name = ConstantKeys.SP_NAME) StringParam name,
			@OptionalParam(name = ConstantKeys.SP_PHONETIC) StringParam phonetic,
			@OptionalParam(name = ConstantKeys.SP_RES_ID) TokenParam resid,
			@OptionalParam(name = ConstantKeys.SP_LAST_UPDATE) DateRangeParam _lastUpdated,
			@OptionalParam(name = ConstantKeys.SP_TAG) TokenParam _tag,
			@OptionalParam(name = ConstantKeys.SP_PROFILE) UriParam _profile,
			@OptionalParam(name = ConstantKeys.SP_QUERY) TokenParam _query,
			@OptionalParam(name = ConstantKeys.SP_SECURITY) TokenParam _security,
			@OptionalParam(name = ConstantKeys.SP_CONTENT_DEFAULT) StringParam _content);

	List<Patient> getHistory(IdType theId, InstantType theSince, DateRangeParam theAt, StringParam _page,
			Integer count);
}
