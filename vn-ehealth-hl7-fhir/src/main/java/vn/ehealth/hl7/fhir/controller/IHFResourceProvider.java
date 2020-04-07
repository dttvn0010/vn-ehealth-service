package vn.ehealth.hl7.fhir.controller;

import ca.uhn.fhir.rest.server.IResourceProvider;

public interface IHFResourceProvider extends IResourceProvider {

    Long count();
}
