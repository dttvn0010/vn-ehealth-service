package vn.ehealth.cdr.controller.helper;

import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Specimen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.DichVuKyThuat;
import vn.ehealth.hl7.fhir.clinical.dao.impl.ProcedureDao;
import vn.ehealth.hl7.fhir.clinical.dao.impl.ServiceRequestDao;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.DiagnosticReportDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.ObservationDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.SpecimenDao;
import vn.ehealth.utils.MongoUtils;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

@Service
public class ProcedureHelper {

	@Autowired
	private ProcedureDao procedureDao;
	@Autowired
	private ServiceRequestDao serviceRequestDao;
	@Autowired
	private DiagnosticReportDao diagnosticReportDao;
	@Autowired
	private SpecimenDao specimenDao;
	@Autowired
	private ObservationDao observationDao;

	private DiagnosticReport saveDiagnosticReport(DiagnosticReport obj) {
		if (obj != null) {
			try {
				if (obj.hasId()) {
					return diagnosticReportDao.update(obj, obj.getIdElement());
				} else {
					return diagnosticReportDao.create(obj);
				}
			} catch (Exception ex) {
				// TODO: logging
			}
		}
		return null;
	}

	private ServiceRequest saveServiceRequest(ServiceRequest obj) {
		if (obj != null) {
			try {
				if (obj.hasId()) {
					return serviceRequestDao.update(obj, obj.getIdElement());
				} else {
					return serviceRequestDao.create(obj);
				}
			} catch (Exception ex) {
				// TODO: logging
			}
		}
		return null;
	}

	private Procedure saveProcedure(Procedure obj) {
		if (obj != null) {
			try {
				if (obj.hasId()) {
					return procedureDao.update(obj, obj.getIdElement());
				} else {
					return procedureDao.create(obj);
				}
			} catch (Exception ex) {
				// TODO: logging
			}
		}
		return null;
	}

	private Specimen saveSpecimen(Specimen obj) {
		if ((obj != null)) {
			try {
				if (obj.hasId()) {
					return specimenDao.update(obj, obj.getIdElement());
				} else {
					return specimenDao.create(obj);
				}
			} catch (Exception ex) {
				// TODO: logging
			}
		}
		return null;
	}

	private List<Observation> getObservationByServiceRequest(String serviceRequestId) {

		var ref = (Object) (ResourceType.ServiceRequest + "/" + serviceRequestId);
		var params = mapOf("basedOn.reference", ref);

		return observationDao.searchResource(MongoUtils.createQuery(params));
	}

	private Specimen getSpecimenByServiceRequest(String serviceRequestId) {
		var ref = (Object) (ResourceType.ServiceRequest + "/" + serviceRequestId);
		var params = mapOf("request.reference", ref);

		return specimenDao.getResource(MongoUtils.createQuery(params));
	}

	public Procedure getProcedureByTypeAndIdhis(String loaiDVKT, String idhis) {
		var ref = (Object) (loaiDVKT + "/" + idhis);
		var params = mapOf("identifier.value", ref);
		return procedureDao.getResource(MongoUtils.createQuery(params));
	}

	public void removeOldData(DichVuKyThuat dto) {
		if (dto == null || dto.getCategory() == null || !dto.getCategory().hasCoding()) {
			return;
		}

		var loaiDVKT = dto.getCategory().getCodingFirstRep().getCode();
		var procedure = getProcedureByTypeAndIdhis(loaiDVKT, dto.idhis);

		if (procedure != null) {
			DatabaseUtil.setReferenceResource(procedure.getBasedOn());
			DatabaseUtil.setReferenceResource(procedure.getReport());

			if (procedure.hasBasedOn()) {
				var serviceRequest = (ServiceRequest) procedure.getBasedOnFirstRep().getResource();
				if (serviceRequest != null) {
					// Delete specimen
					var specimen = getSpecimenByServiceRequest(serviceRequest.getId());
					if (specimen != null) {
						specimenDao.remove(specimen.getIdElement());
					}

					// Delete observations
					var observations = getObservationByServiceRequest(serviceRequest.getId());
					observations.forEach(x -> observationDao.remove(x.getIdElement()));
				}

				// Delete service request
				serviceRequestDao.remove(serviceRequest.getIdElement());
			}

			if (procedure.hasReport()) {
				// Delete Diagnostic report

				var diagnosticReport = (DiagnosticReport) procedure.getReportFirstRep().getResource();
				if (diagnosticReport != null) {
					diagnosticReportDao.remove(diagnosticReport.getIdElement());
				}
			}

			// Delete procedure
			procedureDao.remove(procedure.getIdElement());
		}
	}

	@SuppressWarnings("unchecked")
	public void saveDVKT(Encounter enc, DichVuKyThuat dto) {
		if (enc == null || dto == null)
			return;

		var resources = dto.toFhir(enc);
		if (resources == null)
			return;

		// Remove old data
		removeOldData(dto);

		var serviceRequest = (ServiceRequest) resources.get("serviceRequest");
		var procedure = (Procedure) resources.get("procedure");
		var diagnosticReport = (DiagnosticReport) resources.get("diagnosticReport");
		var specimen = (Specimen) resources.get("specimen");
		var observations = (List<Observation>) resources.get("observations");
		if (observations == null)
			observations = new ArrayList<>();

		// Save ServiceRequest
		serviceRequest = saveServiceRequest(serviceRequest);

		if (serviceRequest != null) {
			var ref = createReference(serviceRequest);
			if (procedure != null)
				procedure.setBasedOn(listOf(ref));
			if (diagnosticReport != null)
				diagnosticReport.setBasedOn(listOf(ref));
			if (specimen != null)
				specimen.setRequest(listOf(ref));
			observations.forEach(x -> x.setBasedOn(listOf(ref)));
		}

		// Save DiagnosticReport
		diagnosticReport = saveDiagnosticReport(diagnosticReport);

		if (diagnosticReport != null) {
			if (procedure != null)
				procedure.setReport(listOf(createReference(diagnosticReport)));
		}

		// Save Procedure
		procedure = saveProcedure(procedure);

		if (procedure != null) {
			var ref = createReference(procedure);
			observations.forEach(x -> x.setPartOf(listOf(ref)));
		}

		// Save Specimen
		specimen = saveSpecimen(specimen);

		if (specimen != null) {
			serviceRequest.setSpecimen(listOf(createReference(specimen)));
			serviceRequest = saveServiceRequest(serviceRequest);
		}

		// Save Observation
		observations.forEach(x -> {
			try {
				observationDao.create(x);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
}
