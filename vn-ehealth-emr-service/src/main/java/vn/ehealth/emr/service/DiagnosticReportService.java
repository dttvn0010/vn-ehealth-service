package vn.ehealth.emr.service;

import org.hl7.fhir.r4.model.DiagnosticReport;
import org.springframework.stereotype.Service;

import vn.ehealth.hl7.fhir.diagnostic.entity.DiagnosticReportEntity;

@Service
public class DiagnosticReportService extends ResourceService<DiagnosticReportEntity, DiagnosticReport> {

    @Override
    DiagnosticReportEntity fromFhir(DiagnosticReport obj) {
        return DiagnosticReportEntity.fromDiagnosticReport(obj);
    }

    @Override
    DiagnosticReport toFhir(DiagnosticReportEntity ent) {
        return DiagnosticReportEntity.toDiagnosticReport(ent);
    }

}
