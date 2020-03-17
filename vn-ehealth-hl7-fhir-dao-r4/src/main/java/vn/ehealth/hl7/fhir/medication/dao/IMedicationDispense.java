package vn.ehealth.hl7.fhir.medication.dao;

import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationDispense;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;

/**
 * 
 * @author sonvt
 * @since 2019
 */
public interface IMedicationDispense {

    MedicationDispense create(FhirContext fhirContext, MedicationDispense object);

    MedicationDispense update(FhirContext fhirContext, MedicationDispense object, IdType idType);

    MedicationDispense read(FhirContext fhirContext, IdType idType);

    MedicationDispense remove(FhirContext fhirContext, IdType idType);

    MedicationDispense readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            TokenParam code,
            TokenParam type,
            TokenParam status,
            TokenParam identifier,
            ReferenceParam context,
            ReferenceParam destination,
            ReferenceParam medication,
            ReferenceParam patient,
            ReferenceParam performer,
            ReferenceParam prescription,
            ReferenceParam receiver,
            ReferenceParam responsibleparty,
            ReferenceParam subject,
            ReferenceParam whenhandedover,
            DateRangeParam whenprepared,
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
            TokenParam code,
            TokenParam type,
            TokenParam status,
            TokenParam identifier,
            ReferenceParam context,
            ReferenceParam destination,
            ReferenceParam medication,
            ReferenceParam patient,
            ReferenceParam performer,
            ReferenceParam prescription,
            ReferenceParam receiver,
            ReferenceParam responsibleparty,
            ReferenceParam subject,
            ReferenceParam whenhandedover,
            DateRangeParam whenprepared,
            TokenParam resid,
            DateRangeParam _lastUpdated,
            TokenParam _tag,
            UriParam _profile,
            TokenParam _query,
            TokenParam _security,
            StringParam _content);
}
