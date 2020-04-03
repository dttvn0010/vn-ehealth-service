package vn.ehealth.hl7.fhir;

import org.hl7.fhir.r4.model.*;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.clinical.entity.*;
import vn.ehealth.hl7.fhir.core.entity.*;
import vn.ehealth.hl7.fhir.diagnostic.entity.*;
import vn.ehealth.hl7.fhir.ehr.entity.*;
import vn.ehealth.hl7.fhir.medication.entity.*;
import vn.ehealth.hl7.fhir.patient.entity.*;
import vn.ehealth.hl7.fhir.provider.entity.*;
import vn.ehealth.hl7.fhir.schedule.entity.*;
import vn.ehealth.hl7.fhir.user.entity.*;
import vn.ehealth.hl7.fhir.term.entity.*;

public class ModelVerification {

    static void check(Class<?> entType, Class<?> fhirType) {
        System.out.println("Validating class " + entType.getName() + " agaist class " + fhirType.getName());
        var fields = DataConvertUtil.getAnnotedFields(fhirType);
        for(var field : fields) {
            if(DataConvertUtil.getEntField(entType, field.getName()) == null) {
                System.err.println(String.format("Missing field \"%s\" for class : %s",
                        field.getName(), entType.getName()));
            }
        }
    }
    
    public static void main(String[] args) {
        
        // base
        check(BaseAddress.class, Address.class);
        check(BaseAnnotation.class, Annotation.class);
        check(BaseAttachment.class, Attachment.class);
        check(BaseCodeableConcept.class, CodeableConcept.class);
        check(BaseCoding.class, Coding.class);
        check(BaseContactPoint.class, ContactPoint.class);
        check(BaseDosage.class, Dosage.class);
        check(BaseDosage.BaseDosageDoseAndRate.class, Dosage.DosageDoseAndRateComponent.class);
        check(BaseDuration.class, Duration.class);
        check(BaseHumanName.class, HumanName.class);
        check(BaseIdentifier.class, Identifier.class);
        check(BasePeriod.class, Period.class);
        check(BaseQuantity.class, Quantity.class);
        check(BaseRange.class, Range.class);
        check(BaseRatio.class, Ratio.class);
        check(vn.ehealth.hl7.fhir.core.entity.BaseReference.class, Reference.class);
        check(BaseTiming.class, Timing.class);
        check(BaseTiming.BaseTimingRepeat.class, Timing.TimingRepeatComponent.class);
        check(BaseUsageContext.class, UsageContext.class);
        
        // clinical
        check(AllergyIntoleranceEntity.class, AllergyIntolerance.class);
        check(AllergyIntoleranceEntity.AllergyIntoleranceReaction.class, AllergyIntolerance.AllergyIntoleranceReactionComponent.class);
        
        check(CarePlanEntity.class, CarePlan.class);
        check(CarePlanEntity.CarePlanActivity.class, CarePlan.CarePlanActivityComponent.class);
        check(CarePlanEntity.CarePlanActivityDetail.class, CarePlan.CarePlanActivityDetailComponent.class);
        
        check(ClinicalImpressionEntity.class, ClinicalImpression.class);
        check(ClinicalImpressionEntity.ClinicalImpressionFinding.class, ClinicalImpression.ClinicalImpressionFindingComponent.class);
        check(ClinicalImpressionEntity.ClinicalImpressionInvestigation.class, ClinicalImpression.ClinicalImpressionInvestigationComponent.class);
        
        check(ConditionEntity.class, Condition.class);
        check(ConditionEntity.ConditionStage.class, Condition.ConditionStageComponent.class);
        check(ConditionEntity.ConditionEvidence.class, Condition.ConditionEvidenceComponent.class);
        
        check(DetectedIssueEntity.class, DetectedIssue.class);
        check(DetectedIssueEntity.DetectedIssueMitigation.class, DetectedIssue.DetectedIssueMitigationComponent.class);
        check(DetectedIssueEntity.DetectedIssueEvidence.class, DetectedIssue.DetectedIssueEvidenceComponent.class);
        
        check(FamilyMemberHistoryEntity.class, FamilyMemberHistory.class);
        check(FamilyMemberHistoryEntity.FamilyMemberHistoryCondition.class, FamilyMemberHistory.FamilyMemberHistoryConditionComponent.class);
        
        check(GoalEntity.class, Goal.class);
        
        check(ProcedureEntity.class, Procedure.class);
        check(ProcedureEntity.ProcedureFocalDevice.class, Procedure.ProcedureFocalDeviceComponent.class);
        check(ProcedureEntity.ProcedurePerformer.class, Procedure.ProcedurePerformerComponent.class);
        
        // diagnostic
        check(DiagnosticReportEntity.class, DiagnosticReport.class);
        check(DiagnosticReportEntity.DiagnosticReportMedia.class, DiagnosticReport.DiagnosticReportMediaComponent.class);
        
        check(ImagingStudyEntity.class, ImagingStudy.class);
        check(ImagingStudyEntity.ImagingStudySeriesPerformer.class, ImagingStudy.ImagingStudySeriesPerformerComponent.class);
        check(ImagingStudyEntity.ImagingStudySeriesInstance.class, ImagingStudy.ImagingStudySeriesInstanceComponent.class);
        check(ImagingStudyEntity.ImagingStudySeries.class, ImagingStudy.ImagingStudySeriesComponent.class);
        
        check(MediaEntity.class, Media.class);
        check(ObservationEntity.class, Observation.class);
        check(ObservationEntity.ObservationReferenceRange.class, Observation.ObservationReferenceRangeComponent.class);
        check(ObservationEntity.ObservationComponent.class, Observation.ObservationComponentComponent.class);
        
        check(ServiceRequestEntity.class, ServiceRequest.class);
        
        check(SpecimenEntity.class, Specimen.class);
        check(SpecimenEntity.SpecimenCollection.class, Specimen.SpecimenCollectionComponent.class);
        check(SpecimenEntity.SpecimenProcessing.class, Specimen.SpecimenProcessingComponent.class);
        check(SpecimenEntity.SpecimenContainer.class, Specimen.SpecimenContainerComponent.class);
        
        // ehr
        check(CareTeamEntity.class, CareTeam.class);
        check(CareTeamEntity.CareTeamParticipant.class, CareTeam.CareTeamParticipantComponent.class);
        
        check(EncounterEntity.class, Encounter.class);
        check(EncounterEntity.EncounterLocation.class, Encounter.EncounterLocationComponent.class);
        check(EncounterEntity.StatusHistory.class, Encounter.StatusHistoryComponent.class);
        check(EncounterEntity.ClassHistory.class, Encounter.ClassHistoryComponent.class);
        check(EncounterEntity.EncounterParticipant.class, Encounter.EncounterParticipantComponent.class);
        check(EncounterEntity.Diagnosis.class, Encounter.DiagnosisComponent.class);
        
        check(EpisodeOfCareEntity.class, EpisodeOfCare.class);
        check(EpisodeOfCareEntity.EpisodeOfCareStatusHistory.class, EpisodeOfCare.EpisodeOfCareStatusHistoryComponent.class);
        check(EpisodeOfCareEntity.Diagnosis.class, EpisodeOfCare.DiagnosisComponent.class);
        
        // medication
        check(MedicationEntity.class, Medication.class);
        check(ImmunizationEntity.class, Immunization.class);
        check(ImmunizationEntity.ImmunizationPerformer.class, Immunization.ImmunizationPerformerComponent.class);
        check(ImmunizationEntity.ImmunizationReaction.class, Immunization.ImmunizationReactionComponent.class);
        check(ImmunizationEntity.ImmunizationEducation.class, Immunization.ImmunizationEducationComponent.class);
        
        check(MedicationAdministrationEntity.class, MedicationAdministration.class);
        check(MedicationAdministrationEntity.MedicationAdministrationDosage.class, MedicationAdministration.MedicationAdministrationDosageComponent.class);
        check(MedicationAdministrationEntity.MedicationAdministrationPerformer.class, MedicationAdministration.MedicationAdministrationPerformerComponent.class);
        
        check(MedicationDispenseEntity.class, MedicationDispense.class);
        check(MedicationDispenseEntity.MedicationDispensePerformer.class, MedicationDispense.MedicationDispensePerformerComponent.class);
        check(MedicationDispenseEntity.MedicationDispenseSubstitution.class, MedicationDispense.MedicationDispenseSubstitutionComponent.class);
        
        check(MedicationRequestEntity.class, MedicationRequest.class);
        check(MedicationRequestEntity.MedicationRequestDispenseRequest.class, MedicationRequest.MedicationRequestDispenseRequestComponent.class);
        check(MedicationRequestEntity.MedicationRequestSubstitution.class, MedicationRequest.MedicationRequestSubstitutionComponent.class);
        
        // patient
        
        check(PatientEntity.class, Patient.class);
        check(PatientEntity.PatientCommunication.class, Patient.PatientCommunicationComponent.class);
        check(PatientEntity.PatientLink.class, Patient.PatientLinkComponent.class);
        check(PatientEntity.Contact.class, Patient.ContactComponent.class);
        
        check(RelatedPersonEntity.class, RelatedPerson.class);
        check(RelatedPersonEntity.RelatedPersonCommunication.class, RelatedPerson.RelatedPersonCommunicationComponent.class);
        
        // provider
        check(DeviceEntity.class, Device.class);
        check(DeviceEntity.DeviceUdiCarrier.class, Device.DeviceUdiCarrierComponent.class);
        
        check(HealthcareServiceEntity.class, HealthcareService.class);
        check(HealthcareServiceEntity.HealthcareServiceNotAvailable.class, HealthcareService.HealthcareServiceNotAvailableComponent.class);
        check(HealthcareServiceEntity.HealthcareServiceAvailableTime.class, HealthcareService.HealthcareServiceAvailableTimeComponent.class);
        check(HealthcareServiceEntity.HealthcareServiceEligibility.class, HealthcareService.HealthcareServiceEligibilityComponent.class);
        
        
        check(LocationEntity.class, Location.class);
        check(LocationEntity.LocationPosition.class, Location.LocationPositionComponent.class);
        
        check(OrganizationEntity.class, Organization.class);
        check(OrganizationEntity.OrganizationContact.class, Organization.OrganizationContactComponent.class);
        
        check(PractitionerEntity.class, Practitioner.class);
        check(PractitionerEntity.PractitionerQualification.class, Practitioner.PractitionerQualificationComponent.class);
        
        check(PractitionerRoleEntity.class, PractitionerRole.class);
        check(PractitionerRoleEntity.PractitionerRoleAvailableTime.class, PractitionerRole.PractitionerRoleAvailableTimeComponent.class);
        check(PractitionerRoleEntity.PractitionerRoleNotAvailable.class, PractitionerRole.PractitionerRoleNotAvailableComponent.class);
        
        
        // schedule
        check(AppointmentEntity.class, Appointment.class);
        check(AppointmentEntity.AppointmentParticipant.class, Appointment.AppointmentParticipantComponent.class);
        check(AppointmentResponseEntity.class, AppointmentResponse.class);
        check(AppointmentResponseEntity.class, AppointmentResponse.class);
        check(ScheduleEntity.class, Schedule.class);
        check(SlotEntity.class, Slot.class);
        
        // term
        check(CodeSystemEntity.class, CodeSystem.class);
        check(CodeSystemEntity.CodeSystemFilter.class, CodeSystem.CodeSystemFilterComponent.class);
        check(CodeSystemEntity.Property.class, CodeSystem.PropertyComponent.class);
        check(CodeSystemEntity.ConceptDefinitionDesignation.class, CodeSystem.ConceptDefinitionDesignationComponent.class);
        check(CodeSystemEntity.ConceptProperty.class, CodeSystem.ConceptPropertyComponent.class);
        check(CodeSystemEntity.ConceptDefinition.class, CodeSystem.ConceptDefinitionComponent.class);
        
        
        check(ConceptMapEntity.OtherElement.class, ConceptMap.OtherElementComponent.class);
        check(ConceptMapEntity.TargetElement.class, ConceptMap.TargetElementComponent.class);
        check(ConceptMapEntity.SourceElement.class, ConceptMap.SourceElementComponent.class);
        check(ConceptMapEntity.ConceptMapGroupUnmapped.class, ConceptMap.ConceptMapGroupUnmappedComponent.class);
        check(ConceptMapEntity.ConceptMapGroup.class, ConceptMap.ConceptMapGroupComponent.class);
        
        check(ValueSetEntity.class, ValueSet.class);
        check(ValueSetEntity.ConceptReferenceDesignation.class, ValueSet.ConceptReferenceDesignationComponent.class);        
        check(ValueSetEntity.ConceptReference.class, ValueSet.ConceptReferenceComponent.class);
        check(ValueSetEntity.ConceptSet.class, ValueSet.ConceptSetComponent.class);
        check(ValueSetEntity.ValueSetExpansionContains.class, ValueSet.ValueSetExpansionContainsComponent.class);
        check(ValueSetEntity.ValueSetCompose.class, ValueSet.ValueSetComposeComponent.class);
        check(ValueSetEntity.ValueSetExpansionParameter.class, ValueSet.ValueSetExpansionParameterComponent.class);
        check(ValueSetEntity.ValueSetExpansion.class, ValueSet.ValueSetExpansionComponent.class);
               
        
        // person
        check(PersonEntity.class, Person.class);
        check(PersonEntity.PersonLink.class, Person.PersonLinkComponent.class);
        System.out.println("Done");
    }
}
