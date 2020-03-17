package vn.ehealth.hl7.fhir.term.dao;

import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ValueSet;

import ca.uhn.fhir.context.FhirContext;
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
public interface IValueSet {

    ValueSet create(FhirContext fhirContext, ValueSet object);

    ValueSet update(FhirContext fhirContext, ValueSet object, IdType idType);

    ValueSet read(FhirContext fhirContext, IdType idType);

    ValueSet remove(FhirContext fhirContext, IdType idType);

    ValueSet readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            DateRangeParam date,
            StringParam description,
            UriParam expansion,
            TokenParam identifier,
            TokenParam jurisdiction,
            StringParam name,
            StringParam publisher,
            UriParam reference,
            TokenParam status,
            StringParam title,
            UriParam url,
            TokenParam version,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content,
            StringParam _page, String sortParam, Integer count);

    long getTotal(FhirContext fhirContext, DateRangeParam date,
            StringParam description,
            UriParam expansion,
            TokenParam identifier,
            TokenParam jurisdiction,
            StringParam name,
            StringParam publisher,
            UriParam reference,
            TokenParam status,
            StringParam title,
            UriParam url,
            TokenParam version,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content);
}
