package vn.ehealth.emr.dto.diagnostic;

import org.hl7.fhir.r4.model.DiagnosticReport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.diagnostic.entity.DiagnosticReportEntity;

@JsonInclude(Include.NON_NULL)
public class DiagnosticReportDTO extends DiagnosticReportEntity {
	
    public static DiagnosticReportDTO fromFhir(DiagnosticReport obj) {
        return DataConvertUtil.fhirToEntity(obj, DiagnosticReportDTO.class);
    }
}
