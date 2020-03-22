package vn.ehealth.hl7.fhir.providers;

import ca.uhn.fhir.rest.server.IResourceProvider;

public interface IHFResourceProvider extends IResourceProvider {

    Long count();
}
