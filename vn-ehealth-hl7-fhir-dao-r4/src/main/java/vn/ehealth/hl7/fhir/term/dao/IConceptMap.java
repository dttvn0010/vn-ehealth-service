package vn.ehealth.hl7.fhir.term.dao;

import java.util.List;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ConceptMap;
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
public interface IConceptMap {

    ConceptMap create(FhirContext fhirContext, ConceptMap object);

    ConceptMap update(FhirContext fhirContext, ConceptMap object, IdType idType);

    ConceptMap read(FhirContext fhirContext, IdType idType);

    ConceptMap remove(FhirContext fhirContext, IdType idType);
    
    ConceptMap readOrVread(FhirContext fhirContext, IdType idType);

    List<Resource> search(FhirContext fhirContext, TokenParam active,
            DateRangeParam date,
            UriParam dependson,
            StringParam description,
            TokenParam identifier,
            TokenParam jurisdiction,
            StringParam name,
            UriParam other,
            UriParam product,
            StringParam publisher,
            TokenParam code,
            UriParam source,
            TokenParam status,
            UriParam target,
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

    Parameters getTranslateParams(@OperationParam(name = "code") TokenParam code,
            @OperationParam(name = "system") UriParam system, @OperationParam(name = "version") StringParam version,
            @OperationParam(name = "source") UriParam source, @OperationParam(name = "coding") Coding coding,
            @OperationParam(name = "target") UriParam target, @OperationParam(name = "reverse") StringParam reverse);

    ConceptMap getClosureParams(@OperationParam(name = "name") StringParam name,
            @OperationParam(name = "version") StringParam version, @OperationParam(name = "concept") Coding concept);

    long findMatchesAdvancedTotal(FhirContext fhirContext,
            TokenParam active,
            DateRangeParam date,
            UriParam dependson,
            StringParam description,
            TokenParam identifier,
            TokenParam jurisdiction,
            StringParam name,
            UriParam other,
            UriParam product,
            StringParam publisher,
            TokenParam code,
            UriParam source,
            TokenParam status,
            UriParam target,
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
