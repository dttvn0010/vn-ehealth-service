package vn.ehealth.hl7.fhir.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.hapi.rest.server.ServerCapabilityStatementProvider;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.ContactDetail;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.Extension;

import ca.uhn.fhir.rest.annotation.Metadata;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.RestfulServerConfiguration;

public class Hl7FhirServerConformanceProvider extends ServerCapabilityStatementProvider {

	private boolean myCache = true;
	private volatile CapabilityStatement myCapabilityStatement;

	@SuppressWarnings("unused")
	private RestfulServerConfiguration serverConfiguration;

	private RestfulServer restfulServer;

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(Hl7FhirServerConformanceProvider.class);

	public Hl7FhirServerConformanceProvider() {
		super();
	}

	@Override
	public void setRestfulServer(RestfulServer theRestfulServer) {

		serverConfiguration = theRestfulServer.createConfiguration();
		restfulServer = theRestfulServer;
		super.setRestfulServer(theRestfulServer);
	}

	@Override
	@Metadata
	public CapabilityStatement getServerConformance(HttpServletRequest theRequest, RequestDetails details) {
		if (myCapabilityStatement != null && myCache) {
			return myCapabilityStatement;
		}
		CapabilityStatement myCapabilityStatement = super.getServerConformance(theRequest, details);

		/*
		 * if (serverConfiguration != null) { for (ResourceBinding resourceBinding :
		 * serverConfiguration.getResourceBindings()) {
		 * log.info("resourceBinding.getResourceName() = "+resourceBinding.
		 * getResourceName());
		 * log.info("resourceBinding.getMethodBindings().size() = "+resourceBinding.
		 * getMethodBindings().size()); } }
		 */
		if (restfulServer != null) {
			log.trace("restful Server not null");
			for (CapabilityStatement.CapabilityStatementRestComponent nextRest : myCapabilityStatement.getRest()) {
				for (CapabilityStatement.CapabilityStatementRestResourceComponent restResourceComponent : nextRest
						.getResource()) {
					log.trace("restResourceComponent.getType - " + restResourceComponent.getType());
					for (IResourceProvider provider : restfulServer.getResourceProviders()) {

						log.trace("Provider Resource - " + provider.getResourceType().getSimpleName());
						if (restResourceComponent.getType().equals(provider.getResourceType().getSimpleName())
								|| (restResourceComponent.getType().contains("List")
										&& provider.getResourceType().getSimpleName().contains("List")))
							if (provider instanceof IHFResourceProvider) {
								log.trace("ICCResourceProvider - " + provider.getClass());
								IHFResourceProvider resourceProvider = (IHFResourceProvider) provider;

								Extension extension = restResourceComponent.getExtensionFirstRep();
								if (extension == null) {
									extension = restResourceComponent.addExtension();
								}
								extension.setUrl(
										"http://hl7api.sourceforge.net/hapi-fhir/res/extdefs.html#resourceCount")
										.setValue(new DecimalType(resourceProvider.count()));
							}
					}
				}
			}
		}

		myCapabilityStatement.setCopyright(
				"This implementation is belonged to eHealth Core Team - Vietnam eHealth Innovation Group (VEIG) - Bộ Y tế. "
						+ "Reproduction or distribution of this product without permission of the owner is prohibited");
		ContactDetail contact = new ContactDetail();
		ContactPoint contactPoint = new ContactPoint();
		contact.setName("Mr. Nguyen Hai Phong");
		contactPoint.setSystem(org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem.PHONE);
		contactPoint.setValue("Tel: (+84)-9133.09033 or email: haiphong.nguyen@gmail.com");
		List<ContactPoint> listContactPoint = new ArrayList<ContactPoint>();
		listContactPoint.add(contactPoint);
		contact.setTelecom(listContactPoint);
		List<ContactDetail> listContact = new ArrayList<ContactDetail>();
		listContact.add(contact);
		myCapabilityStatement.setContact(listContact);

		return myCapabilityStatement;
	}

}
