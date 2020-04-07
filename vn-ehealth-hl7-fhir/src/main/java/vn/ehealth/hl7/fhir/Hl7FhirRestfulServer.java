package vn.ehealth.hl7.fhir;

import java.util.Arrays;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.hl7.fhir.r4.hapi.validation.FhirInstanceValidator;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.narrative.INarrativeGenerator;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.server.ETagSupportEnum;
import ca.uhn.fhir.rest.server.FifoMemoryPagingProvider;
import ca.uhn.fhir.rest.server.HardcodedServerAddressStrategy;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;
import ca.uhn.fhir.rest.server.interceptor.RequestValidatingInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;
import ca.uhn.fhir.util.VersionUtil;
import ca.uhn.fhir.validation.ResultSeverityEnum;
//import vn.ehealth.hl7.fhir.clinical.providers.ServiceRequestProvider;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.providers.Hl7FhirServerConformanceProvider;
import vn.ehealth.hl7.fhir.r4.providers.base.BinaryProvider;
import vn.ehealth.hl7.fhir.r4.providers.base.BundleProvider;
import vn.ehealth.hl7.fhir.r4.providers.base.SubscriptionProvider;
import vn.ehealth.hl7.fhir.r4.providers.careprovision.NutritionOrderProvider;
import vn.ehealth.hl7.fhir.r4.providers.careprovision.RequestGroupProvider;
import vn.ehealth.hl7.fhir.r4.providers.careprovision.RiskAssessmentProvider;
import vn.ehealth.hl7.fhir.r4.providers.careprovision.VisionPrescriptionProvider;
import vn.ehealth.hl7.fhir.r4.providers.clinical.AllergyIntoleranceProvider;
import vn.ehealth.hl7.fhir.r4.providers.clinical.CarePlanProvider;
import vn.ehealth.hl7.fhir.r4.providers.clinical.ClinicalImpressionProvider;
import vn.ehealth.hl7.fhir.r4.providers.clinical.ConditionProvider;
import vn.ehealth.hl7.fhir.r4.providers.clinical.DetectedIssueProvider;
import vn.ehealth.hl7.fhir.r4.providers.clinical.FamilyMemberHistoryProvider;
import vn.ehealth.hl7.fhir.r4.providers.clinical.GoalProvider;
import vn.ehealth.hl7.fhir.r4.providers.clinical.ProcedureProvider;
import vn.ehealth.hl7.fhir.r4.providers.clinical.ServiceRequestProvider;
import vn.ehealth.hl7.fhir.r4.providers.definitionalartifact.ActivityDefinitionProvider;
import vn.ehealth.hl7.fhir.r4.providers.definitionalartifact.PlanDefinitionProvider;
import vn.ehealth.hl7.fhir.r4.providers.definitionalartifact.QuestionnaireProvider;
import vn.ehealth.hl7.fhir.r4.providers.diagnostic.BodyStructureProvider;
import vn.ehealth.hl7.fhir.r4.providers.diagnostic.DiagnosticReportProvider;
import vn.ehealth.hl7.fhir.r4.providers.diagnostic.ImagingStudyProvider;
import vn.ehealth.hl7.fhir.r4.providers.diagnostic.MediaProvider;
import vn.ehealth.hl7.fhir.r4.providers.diagnostic.ObservationProvider;
import vn.ehealth.hl7.fhir.r4.providers.diagnostic.QuestionnaireResponseProvider;
import vn.ehealth.hl7.fhir.r4.providers.diagnostic.SpecimenProvider;
import vn.ehealth.hl7.fhir.r4.providers.document.CompositionProvider;
import vn.ehealth.hl7.fhir.r4.providers.document.DocumentManifestProvider;
import vn.ehealth.hl7.fhir.r4.providers.document.DocumentReferenceProvider;
import vn.ehealth.hl7.fhir.r4.providers.ehr.CareTeamProvider;
import vn.ehealth.hl7.fhir.r4.providers.ehr.EncounterProvider;
import vn.ehealth.hl7.fhir.r4.providers.ehr.EpisodeOfCareProvider;
import vn.ehealth.hl7.fhir.r4.providers.individual.GroupProvider;
import vn.ehealth.hl7.fhir.r4.providers.individual.PersonProvider;
import vn.ehealth.hl7.fhir.r4.providers.medication.ImmunizationProvider;
import vn.ehealth.hl7.fhir.r4.providers.medication.MedicationAdministrationProvider;
import vn.ehealth.hl7.fhir.r4.providers.medication.MedicationDispenseProvider;
import vn.ehealth.hl7.fhir.r4.providers.medication.MedicationProvider;
import vn.ehealth.hl7.fhir.r4.providers.medication.MedicationRequestProvider;
import vn.ehealth.hl7.fhir.r4.providers.medication.MedicationStatementProvider;
import vn.ehealth.hl7.fhir.r4.providers.patient.PatientProvider;
import vn.ehealth.hl7.fhir.r4.providers.patient.RelatedPersonProvider;
import vn.ehealth.hl7.fhir.r4.providers.plain.PlainProvider;
import vn.ehealth.hl7.fhir.r4.providers.product.SubstanceProvider;
import vn.ehealth.hl7.fhir.r4.providers.provider.DeviceProvider;
import vn.ehealth.hl7.fhir.r4.providers.provider.HealthcareServiceProvider;
import vn.ehealth.hl7.fhir.r4.providers.provider.LocationProvider;
import vn.ehealth.hl7.fhir.r4.providers.provider.OrganizationProvider;
import vn.ehealth.hl7.fhir.r4.providers.provider.PractitionerProvider;
import vn.ehealth.hl7.fhir.r4.providers.provider.PractitionerRoleProvider;
import vn.ehealth.hl7.fhir.r4.providers.schedule.AppointmentProvider;
import vn.ehealth.hl7.fhir.r4.providers.schedule.AppointmentResponseProvider;
import vn.ehealth.hl7.fhir.r4.providers.schedule.ScheduleProvider;
import vn.ehealth.hl7.fhir.r4.providers.schedule.SlotProvider;
import vn.ehealth.hl7.fhir.r4.providers.security.AuditEventProvider;
import vn.ehealth.hl7.fhir.r4.providers.security.ConsentProvider;
import vn.ehealth.hl7.fhir.r4.providers.security.ProvenanceProvider;
import vn.ehealth.hl7.fhir.r4.providers.terminology.CodeSystemProvider;
import vn.ehealth.hl7.fhir.r4.providers.terminology.ConceptMapProvider;
import vn.ehealth.hl7.fhir.r4.providers.terminology.ValueSetProvider;
import vn.ehealth.hl7.fhir.r4.providers.workflow.TaskProvider;

public class Hl7FhirRestfulServer extends RestfulServer {
	private static final long serialVersionUID = 1L;
	private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(Hl7FhirRestfulServer.class);
	private ApplicationContext applicationContext;

	public Hl7FhirRestfulServer(ApplicationContext context) {
		this.applicationContext = context;
	}

	@Value("http://localhost:8080/R4")
	private String serverBase;

	@Override
	public void addHeadersToResponse(HttpServletResponse theHttpResponse) {
		theHttpResponse.addHeader("X-Powered-By", "HAPI FHIR " + VersionUtil.getVersion()
				+ " RESTful Server (Vietnam eHealth Innovation Group - FHIR API R4)");
	}

	@Override
	protected void initialize() throws ServletException {
		super.initialize();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		FhirVersionEnum fhirVersion = FhirVersionEnum.R4;
		setFhirContext(new FhirContext(fhirVersion));

		if (serverBase != null && !serverBase.isEmpty()) {
			setServerAddressStrategy(new HardcodedServerAddressStrategy(serverBase));
		}

		// registering server-level provider
		registerProvider(applicationContext.getBean(PlainProvider.class));

		setResourceProviders(Arrays.asList((IResourceProvider) applicationContext.getBean(CarePlanProvider.class),
				(IResourceProvider) applicationContext.getBean(ClinicalImpressionProvider.class),
				(IResourceProvider) applicationContext.getBean(ConditionProvider.class),
				(IResourceProvider) applicationContext.getBean(DetectedIssueProvider.class),
				(IResourceProvider) applicationContext.getBean(GoalProvider.class),
				(IResourceProvider) applicationContext.getBean(ProcedureProvider.class),
				(IResourceProvider) applicationContext.getBean(DiagnosticReportProvider.class),
				(IResourceProvider) applicationContext.getBean(ImagingStudyProvider.class),
				(IResourceProvider) applicationContext.getBean(ObservationProvider.class),
				(IResourceProvider) applicationContext.getBean(SpecimenProvider.class),
				(IResourceProvider) applicationContext.getBean(CareTeamProvider.class),
				(IResourceProvider) applicationContext.getBean(EncounterProvider.class),
				(IResourceProvider) applicationContext.getBean(EpisodeOfCareProvider.class),
				(IResourceProvider) applicationContext.getBean(ImmunizationProvider.class),
				(IResourceProvider) applicationContext.getBean(MedicationAdministrationProvider.class),
				(IResourceProvider) applicationContext.getBean(MedicationDispenseProvider.class),
				(IResourceProvider) applicationContext.getBean(MedicationProvider.class),
				(IResourceProvider) applicationContext.getBean(MedicationRequestProvider.class),
				(IResourceProvider) applicationContext.getBean(MedicationStatementProvider.class),
				(IResourceProvider) applicationContext.getBean(PatientProvider.class),
				(IResourceProvider) applicationContext.getBean(RelatedPersonProvider.class),
				(IResourceProvider) applicationContext.getBean(DeviceProvider.class),
				(IResourceProvider) applicationContext.getBean(HealthcareServiceProvider.class),
				(IResourceProvider) applicationContext.getBean(LocationProvider.class),
				(IResourceProvider) applicationContext.getBean(OrganizationProvider.class),
				(IResourceProvider) applicationContext.getBean(PractitionerProvider.class),
				(IResourceProvider) applicationContext.getBean(PractitionerRoleProvider.class),
				(IResourceProvider) applicationContext.getBean(AppointmentProvider.class),
				(IResourceProvider) applicationContext.getBean(AppointmentResponseProvider.class),
				(IResourceProvider) applicationContext.getBean(ScheduleProvider.class),
				(IResourceProvider) applicationContext.getBean(SlotProvider.class),
				(IResourceProvider) applicationContext.getBean(CodeSystemProvider.class),
				(IResourceProvider) applicationContext.getBean(ConceptMapProvider.class),
				(IResourceProvider) applicationContext.getBean(ValueSetProvider.class),
				(IResourceProvider) applicationContext.getBean(PersonProvider.class),
				(IResourceProvider) applicationContext.getBean(ServiceRequestProvider.class),
				(IResourceProvider) applicationContext.getBean(FamilyMemberHistoryProvider.class),
				(IResourceProvider) applicationContext.getBean(AllergyIntoleranceProvider.class),
				(IResourceProvider) applicationContext.getBean(MediaProvider.class),
				// Add 2020/04/05
				(IResourceProvider) applicationContext.getBean(BinaryProvider.class),
				(IResourceProvider) applicationContext.getBean(BundleProvider.class),
				(IResourceProvider) applicationContext.getBean(CompositionProvider.class),
				(IResourceProvider) applicationContext.getBean(DocumentManifestProvider.class),
				(IResourceProvider) applicationContext.getBean(DocumentReferenceProvider.class),
				(IResourceProvider) applicationContext.getBean(NutritionOrderProvider.class),
				// Add 2020/04/06
				(IResourceProvider) applicationContext.getBean(SubscriptionProvider.class),
				(IResourceProvider) applicationContext.getBean(RequestGroupProvider.class),
				(IResourceProvider) applicationContext.getBean(RiskAssessmentProvider.class),
				(IResourceProvider) applicationContext.getBean(VisionPrescriptionProvider.class),
				(IResourceProvider) applicationContext.getBean(ActivityDefinitionProvider.class),
				(IResourceProvider) applicationContext.getBean(PlanDefinitionProvider.class),
				(IResourceProvider) applicationContext.getBean(QuestionnaireProvider.class),
				(IResourceProvider) applicationContext.getBean(BodyStructureProvider.class),
				(IResourceProvider) applicationContext.getBean(QuestionnaireResponseProvider.class),
				(IResourceProvider) applicationContext.getBean(SubstanceProvider.class),
				(IResourceProvider) applicationContext.getBean(AuditEventProvider.class),
				(IResourceProvider) applicationContext.getBean(ConsentProvider.class),
				(IResourceProvider) applicationContext.getBean(ProvenanceProvider.class),
				(IResourceProvider) applicationContext.getBean(GroupProvider.class),
				(IResourceProvider) applicationContext.getBean(TaskProvider.class)));
		setServerConformanceProvider(new Hl7FhirServerConformanceProvider());

//		ServerInterceptor loggingInterceptor = new ServerInterceptor(ourLog);
//		registerInterceptor(loggingInterceptor);

		/*
		 * Use a narrative generator. This is a completely optional step, but can be
		 * useful as it causes HAPI to generate narratives for resources which don't
		 * otherwise have one.
		 */
		INarrativeGenerator narrativeGen = new DefaultThymeleafNarrativeGenerator();
		getFhirContext().setNarrativeGenerator(narrativeGen);

		registerInterceptor(new ResponseHighlighterInterceptor());
//
//		// Create an interceptor to validate incoming requests
//		RequestValidatingInterceptor requestInterceptor = new RequestValidatingInterceptor();
//
//		// Register a validator module (you could also use SchemaBaseValidator and/or
//		// SchematronBaseValidator)
//		requestInterceptor.addValidatorModule(new FhirInstanceValidator());
//		requestInterceptor.setFailOnSeverity(ResultSeverityEnum.ERROR);
//		requestInterceptor.setAddResponseHeaderOnSeverity(ResultSeverityEnum.INFORMATION);
//		requestInterceptor.setResponseHeaderValue("Validation on ${line}: ${message} ${severity}");
//		requestInterceptor.setResponseHeaderValueNoIssues("No issues detected");

//		// Now register the validating interceptor
//		registerInterceptor(requestInterceptor);

		// Create an interceptor to validate responses
		// This is configured in the same way as above

//		ResponseValidatingInterceptor responseInterceptor = new ResponseValidatingInterceptor();
//		responseInterceptor.addValidatorModule(new FhirInstanceValidator());
//		responseInterceptor.setFailOnSeverity(ResultSeverityEnum.ERROR);
//		responseInterceptor.setAddResponseHeaderOnSeverity(ResultSeverityEnum.INFORMATION);
//		responseInterceptor.setResponseHeaderValue("Validation on ${line}: ${message} ${severity}");
//		responseInterceptor.setResponseHeaderValueNoIssues("No issues detected");
//		Now register the validating interceptor
//		registerInterceptor(responseInterceptor);

		// This is the format for each line. A number of substitution variables may
		// be used here. See the JavaDoc for LoggingInterceptor for information on
		// what is available.

		// registerInterceptor(new OAuth2Interceptor()); // Add OAuth2 Security Filter

		// Define your CORS configuration. This is an example
		// showing a typical setup. You should customize this
		// to your specific needs

		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedHeader("x-fhir-starter");
		config.addAllowedHeader("Origin");
		config.addAllowedHeader("Accept");
		config.addAllowedHeader("X-Requested-With");
		config.addAllowedHeader("Content-Type");

		config.addAllowedOrigin("*");

		config.addExposedHeader("Location");
		config.addExposedHeader("Content-Location");
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HISTORY"));

		// Create the interceptor and register it
		CorsInterceptor corsInterceptor = new CorsInterceptor(config);
		registerInterceptor(corsInterceptor);

		FifoMemoryPagingProvider pp = new FifoMemoryPagingProvider(ConstantKeys.DEFAULT_PAGE_SIZE);
		pp.setDefaultPageSize(ConstantKeys.DEFAULT_PAGE_SIZE);
		pp.setMaximumPageSize(ConstantKeys.DEFAULT_PAGE_MAX_SIZE);
		setPagingProvider(pp);

		setDefaultPrettyPrint(true);
		setDefaultResponseEncoding(EncodingEnum.JSON);

		// ETag support is enabled by default
		setETagSupport(ETagSupportEnum.ENABLED);

	}

	/**
	 * This interceptor adds some pretty syntax highlighting in responses when a
	 * browser is detected
	 */
	@Bean(autowire = Autowire.BY_TYPE)
	public ResponseHighlighterInterceptor responseHighlighterInterceptor() {
		ResponseHighlighterInterceptor retVal = new ResponseHighlighterInterceptor();
		return retVal;
	}
}
