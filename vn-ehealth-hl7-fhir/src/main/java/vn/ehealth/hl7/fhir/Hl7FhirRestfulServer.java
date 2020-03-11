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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.server.FifoMemoryPagingProvider;
import ca.uhn.fhir.rest.server.HardcodedServerAddressStrategy;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseValidatingInterceptor;
import ca.uhn.fhir.util.VersionUtil;
import ca.uhn.fhir.validation.ResultSeverityEnum;

import vn.ehealth.hl7.fhir.clinical.providers.CarePlanProvider;
import vn.ehealth.hl7.fhir.clinical.providers.ClinicalImpressionProvider;
import vn.ehealth.hl7.fhir.clinical.providers.ConditionProvider;
import vn.ehealth.hl7.fhir.clinical.providers.DetectedIssueProvider;
import vn.ehealth.hl7.fhir.clinical.providers.GoalProvider;
import vn.ehealth.hl7.fhir.clinical.providers.ProcedureProvider;
//import vn.ehealth.hl7.fhir.core.oauth2.ServerInterceptor;
import vn.ehealth.hl7.fhir.diagnostic.providers.DiagnosticReportProvider;
import vn.ehealth.hl7.fhir.diagnostic.providers.ImagingStudyProvider;
import vn.ehealth.hl7.fhir.diagnostic.providers.ObservationProvider;
import vn.ehealth.hl7.fhir.diagnostic.providers.SpecimenProvider;
import vn.ehealth.hl7.fhir.ehr.providers.CareTeamProvider;
import vn.ehealth.hl7.fhir.ehr.providers.EncounterProvider;
import vn.ehealth.hl7.fhir.ehr.providers.EpisodeOfCareProvider;
import vn.ehealth.hl7.fhir.medication.providers.ImmunizationProvider;
import vn.ehealth.hl7.fhir.medication.providers.MedicationAdministrationProvider;
import vn.ehealth.hl7.fhir.medication.providers.MedicationDispenseProvider;
import vn.ehealth.hl7.fhir.medication.providers.MedicationProvider;
import vn.ehealth.hl7.fhir.medication.providers.MedicationRequestProvider;
import vn.ehealth.hl7.fhir.medication.providers.MedicationStatementProvider;
import vn.ehealth.hl7.fhir.patient.providers.PatientProvider;
import vn.ehealth.hl7.fhir.patient.providers.RelatedPersonProvider;
import vn.ehealth.hl7.fhir.provider.providers.DeviceProvider;
import vn.ehealth.hl7.fhir.provider.providers.HealthcareServiceProvider;
import vn.ehealth.hl7.fhir.provider.providers.LocationProvider;
import vn.ehealth.hl7.fhir.provider.providers.OrganizationProvider;
import vn.ehealth.hl7.fhir.provider.providers.PractitionerProvider;
import vn.ehealth.hl7.fhir.provider.providers.PractitionerRoleProvider;
import vn.ehealth.hl7.fhir.providers.Hl7FhirServerConformanceProvider;
import vn.ehealth.hl7.fhir.schedule.providers.AppointmentProvider;
import vn.ehealth.hl7.fhir.schedule.providers.AppointmentResponseProvider;
import vn.ehealth.hl7.fhir.schedule.providers.ScheduleProvider;
import vn.ehealth.hl7.fhir.schedule.providers.SlotProvider;
import vn.ehealth.hl7.fhir.term.providers.CodeSystemProvider;
import vn.ehealth.hl7.fhir.term.providers.ConceptMapProvider;
import vn.ehealth.hl7.fhir.term.providers.ValueSetProvider;
import vn.ehealth.hl7.fhir.user.providers.PersonProvider;


public class Hl7FhirRestfulServer extends RestfulServer {
    private static final long serialVersionUID = 1L;
    //private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(Hl7FhirRestfulServer.class);
    private ApplicationContext applicationContext;

    public Hl7FhirRestfulServer(ApplicationContext context) {
        this.applicationContext = context;
    }

    @Value("http://localhost:8080/R4")
    private String serverBase;


    @Override
    public void addHeadersToResponse(HttpServletResponse theHttpResponse) {
        theHttpResponse.addHeader("X-Powered-By", "HAPI FHIR " + VersionUtil.getVersion() + " RESTful Server (Vietnam eHealth Innovation Group - FHIR API R4)");
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
        setResourceProviders(Arrays.asList(
                (IResourceProvider) applicationContext.getBean(CarePlanProvider.class),
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
                (IResourceProvider) applicationContext.getBean(PersonProvider.class)
        ));
        setServerConformanceProvider(new Hl7FhirServerConformanceProvider());
        //ServerInterceptor loggingInterceptor = new ServerInterceptor(ourLog);
        //registerInterceptor(loggingInterceptor);
        /*
         * Use a narrative generator. This is a completely optional step, 
         * but can be useful as it causes HAPI to generate narratives for
         * resources which don't otherwise have one.
         */
        //INarrativeGenerator narrativeGen = new DefaultThymeleafNarrativeGenerator();
        //getFhirContext().setNarrativeGenerator(narrativeGen);
        
        registerInterceptor(new ResponseHighlighterInterceptor());
        
        // Create an interceptor to validate responses
          // This is configured in the same way as above
          ResponseValidatingInterceptor responseInterceptor = new ResponseValidatingInterceptor();
          responseInterceptor.addValidatorModule(new FhirInstanceValidator());
          responseInterceptor.setFailOnSeverity(ResultSeverityEnum.ERROR);
          responseInterceptor.setAddResponseHeaderOnSeverity(ResultSeverityEnum.INFORMATION);
          responseInterceptor.setResponseHeaderValue("Validation on ${line}: ${message} ${severity}");
          responseInterceptor.setResponseHeaderValueNoIssues("No issues detected");
          registerInterceptor(responseInterceptor);
        
        // This is the format for each line. A number of substitution variables may
        // be used here. See the JavaDoc for LoggingInterceptor for information on
        // what is available.

        //ServerInterceptor gatewayInterceptor = new ServerInterceptor(ourLog);
        //registerInterceptor(new OAuth2Interceptor());  // Add OAuth2 Security Filter
        //registerInterceptor(gatewayInterceptor);
        FifoMemoryPagingProvider pp = new FifoMemoryPagingProvider(10);
        pp.setDefaultPageSize(10);
        pp.setMaximumPageSize(100);
        setPagingProvider(pp);

        setDefaultPrettyPrint(true);
        setDefaultResponseEncoding(EncodingEnum.JSON);

    }

    /**
     * This interceptor adds some pretty syntax highlighting in responses when a browser is detected
     */
    @Bean(autowire = Autowire.BY_TYPE)
    public ResponseHighlighterInterceptor responseHighlighterInterceptor() {
        ResponseHighlighterInterceptor retVal = new ResponseHighlighterInterceptor();
        return retVal;
    }
}
