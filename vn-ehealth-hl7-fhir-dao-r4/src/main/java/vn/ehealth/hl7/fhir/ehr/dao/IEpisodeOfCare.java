package vn.ehealth.hl7.fhir.ehr.dao;

import java.util.List;

import org.hl7.fhir.r4.model.EpisodeOfCare;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;

public interface IEpisodeOfCare {

    EpisodeOfCare create(FhirContext fhirContext, EpisodeOfCare object);

    EpisodeOfCare update(FhirContext fhirContext, EpisodeOfCare object, IdType idType);

    EpisodeOfCare read(FhirContext fhirContext, IdType idType);

    EpisodeOfCare remove(FhirContext fhirContext, IdType idType);

    EpisodeOfCare readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            ReferenceParam careManager,
            ReferenceParam condition,
            DateRangeParam date,
            TokenParam identifier,
            ReferenceParam incomingreferral,
            ReferenceParam organization,
            ReferenceParam patient,
            TokenParam status,
            TokenParam type,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content,
            StringParam _page, String sortParam, Integer count);

    long getTotal(FhirContext fhirContext, TokenParam active,
            ReferenceParam careManager,
            ReferenceParam condition,
            DateRangeParam date,
            TokenParam identifier,
            ReferenceParam incomingreferral,
            ReferenceParam organization,
            ReferenceParam patient,
            TokenParam status,
            TokenParam type,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content);
}
