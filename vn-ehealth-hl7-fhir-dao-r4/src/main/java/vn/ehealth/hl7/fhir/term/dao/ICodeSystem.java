package vn.ehealth.hl7.fhir.term.dao;

import java.util.List;

import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
public interface ICodeSystem {

	CodeSystem create(FhirContext fhirContext, CodeSystem object);

	CodeSystem update(FhirContext fhirContext, CodeSystem object, IdType idType);

	CodeSystem read(FhirContext fhirContext, IdType idType);

	CodeSystem remove(FhirContext fhirContext, IdType idType);

	CodeSystem readOrVread(FhirContext fhirContext, IdType idType);

	List<Resource> search(FhirContext fhirContext, TokenParam active, DateRangeParam date, TokenParam identifier,
			StringParam name, TokenParam code, TokenParam contentMode, StringParam description, TokenParam jurisdiction,
			TokenParam language, StringParam publisher, TokenParam status, UriParam system, StringParam title,
			UriParam url, TokenParam version, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
			UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content, StringParam _page,
			String theSort, Integer count);

	Parameters getLookupParams(TokenParam code, UriParam system, StringParam version, Coding coding,
			DateRangeParam date, TokenParam displayLanguage, TokenParam property, TokenParam resid,
			DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
			StringParam _content, StringParam _page);

	long findMatchesAdvancedTotal(FhirContext fhirContext, DateRangeParam date, TokenParam identifier, StringParam name,
			TokenParam code, TokenParam contentMode, StringParam description, TokenParam jurisdiction,
			TokenParam language, StringParam publisher, TokenParam status, UriParam system, StringParam title,
			UriParam url, TokenParam version, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
			UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content);
}
