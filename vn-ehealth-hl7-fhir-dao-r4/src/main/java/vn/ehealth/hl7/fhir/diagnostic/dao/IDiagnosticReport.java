package vn.ehealth.hl7.fhir.diagnostic.dao;

import java.util.List;

import org.hl7.fhir.r4.model.DiagnosticReport;
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

public interface IDiagnosticReport {
    DiagnosticReport create(FhirContext fhirContext, DiagnosticReport object);

    DiagnosticReport update(FhirContext fhirContext, DiagnosticReport object, IdType idType);

    DiagnosticReport read(FhirContext fhirContext, IdType idType);

    DiagnosticReport remove(FhirContext fhirContext, IdType idType);

    DiagnosticReport readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            ReferenceParam basedOn,
            TokenParam category,
            TokenParam code,
            ReferenceParam conetext,
            DateRangeParam date,
            TokenParam diagnosis,
            ReferenceParam encounter,
            TokenParam identifier,
            ReferenceParam image,
            DateRangeParam issued,
            ReferenceParam patient,
            ReferenceParam performer,
            ReferenceParam result,
            ReferenceParam specimen,
            TokenParam status,
            ReferenceParam subject,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content,
            StringParam _page, String sortParam, Integer count);

    long countMatchesAdvancedTotal(FhirContext fhirContext,
            TokenParam active,
            ReferenceParam basedOn,
            TokenParam category,
            TokenParam code,
            ReferenceParam conetext,
            DateRangeParam date,
            TokenParam diagnosis,
            ReferenceParam encounter,
            TokenParam identifier,
            ReferenceParam image,
            DateRangeParam issued,
            ReferenceParam patient,
            ReferenceParam performer,
            ReferenceParam result,
            ReferenceParam specimen,
            TokenParam status,
            ReferenceParam subject,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content);
}
