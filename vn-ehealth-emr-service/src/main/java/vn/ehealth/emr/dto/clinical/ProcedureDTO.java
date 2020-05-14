package vn.ehealth.emr.dto.clinical;

import org.hl7.fhir.r4.model.Procedure;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.clinical.entity.ProcedureEntity;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

@JsonInclude(Include.NON_NULL)
public class ProcedureDTO extends ProcedureEntity{
    
    public static ProcedureDTO fromFhir(Procedure obj) {
        return DataConvertUtil.fhirToEntity(obj, ProcedureDTO.class);
    }
}
