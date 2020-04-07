package vn.ehealth.hl7.fhir.factory;

import java.net.InetAddress;
import java.util.Date;

import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.rest.api.MethodOutcome;

public class AuditEventFactory {
	public static AuditEvent buildAuditEvent(Resource resource, MethodOutcome outcome, String typeCode,
			String subTypeCode, AuditEvent.AuditEventAction actionCode, String systemValue) {
		AuditEvent audit = new AuditEvent();

		audit.getType().setSystem("http://hl7.org/fhir/audit-event-type").setCode(typeCode);
		audit.addSubtype().setSystem("http://hl7.org/fhir/restful-interaction").setCode(subTypeCode);

		audit.setAction(actionCode);

		audit.addEntity().setWhatTarget(resource);

		Date recordedDate = new Date();
		try {
			// InstantDt instance = new InstantDt(recordedDate);
			audit.setRecorded(recordedDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			audit.getSource().getObserver().getIdentifier().setSystem(String.valueOf(InetAddress.getLocalHost()))
					.setValue(systemValue);
		} catch (Exception e) {

		}
		audit.getSource().setSite("application").addType().setSystem("http://hl7.org/fhir/security-source-type")
				.setCode("1").setDisplay("User Device");

		if (outcome != null && outcome.getOperationOutcome() instanceof OperationOutcome) {
			OperationOutcome operationOutcome = (OperationOutcome) outcome.getOperationOutcome();
			if (operationOutcome.getIssue().size() > 0) {
				System.out.println(operationOutcome.getIssue().get(0).getCode());
				switch (operationOutcome.getIssue().get(0).getCode()) {
				case INFORMATIONAL:
					audit.setOutcome(AuditEvent.AuditEventOutcome._0);
					break;
				default:
					audit.setOutcome(AuditEvent.AuditEventOutcome._4);
				}
			}
			try {

				audit.setOutcomeDesc(operationOutcome.getText().getDivAsString());
			} catch (Exception e) {

			}
			// event.set
		}

		return audit;
	}
}
