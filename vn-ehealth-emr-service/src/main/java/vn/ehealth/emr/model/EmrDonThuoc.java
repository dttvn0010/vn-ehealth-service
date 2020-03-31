package vn.ehealth.emr.model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Quantity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.utils.ObjectIdUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;


@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_don_thuoc")
public class EmrDonThuoc {
    
    @Id public ObjectId id;    
    public ObjectId emrHoSoBenhAnId;    
    public ObjectId emrBenhNhanId;
    public ObjectId emrCoSoKhamBenhId;
    public int trangThai;
    public String idhis;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaykedon;
    public EmrCanboYte bacsikedon;
    public String sodon;
    
    public List<EmrFileDinhKem> emrFileDinhKemDonThuocs = new ArrayList<>();
   
    @JsonInclude(Include.NON_NULL)
    public static class EmrDonThuocChiTiet {
        public EmrDmContent emrDmThuoc;
        public EmrDmContent emrDmDuongDungThuoc;
        public EmrDmContent emrDmTanXuatDungThuoc;
        public EmrDmContent emrDmChiDanDungThuoc;
        
        @JsonIgnore
        public Date ngaykedon;
        
        @JsonIgnore
        public EmrCanboYte bacsikedon;
        
        @JsonIgnore
        public String sodon;
        
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        public Date ngaybatdau;
        
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        public Date ngayketthuc;
        
        public String lieuluongdung;    
        public String chidandungthuoc;
        public String bietduoc;
        
        private Integer parseInt(String st) {
            try {
                return Integer.parseInt(st);
            }catch(NumberFormatException e) {
                return null;
            }
        }
        
        public MedicationRequest toFHir(Encounter enc) {
            if(enc == null) return null;
            
            var mRequest = new MedicationRequest();
            mRequest.setSubject(enc.getSubject());
            mRequest.setEncounter(createReference(enc));
            mRequest.setRequester(EmrCanboYte.toRef(bacsikedon));
            mRequest.setAuthoredOn(ngaykedon);
            mRequest.setGroupIdentifier(createIdentifier(sodon, IdentifierSystem.DON_THUOC));
            
            mRequest.setMedication(EmrDmContent.toConcept(emrDmThuoc, CodeSystemValue.DM_THUOC));
           
            var dosage = new Dosage();
            dosage.setText(chidandungthuoc);
            dosage.setRoute(EmrDmContent.toConcept(emrDmDuongDungThuoc, CodeSystemValue.DM_DUONG_DUNG_THUOC));
                
            //TODO: TanXuat
            
            mRequest.getDosageInstruction().add(dosage);
            
            var arr = lieuluongdung.split(" ");
            var soluong = parseInt(arr[0]);
            if(soluong != null) {
                var quantity = new Quantity();
                quantity.setValue(soluong);
                
                if(arr.length > 1) {
                    quantity.setUnit(arr[1]);
                }
                mRequest.getDispenseRequest().setQuantity(quantity);
            }
            
            if(ngaybatdau != null || ngayketthuc != null) {
                var period = new Period();
                period.setStart(ngaybatdau);
                period.setEnd(ngayketthuc);
                mRequest.getDispenseRequest().setValidityPeriod(period);
            }
            
            return mRequest;
        }
    }
    
    public List<EmrDonThuocChiTiet> emrDonThuocChiTiets = new ArrayList<>();
    
    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    }

    public String getEmrHoSoBenhAnId() {
        return ObjectIdUtil.idToString(emrHoSoBenhAnId);
    }
    
    public void setEmrHoSoBenhAnId(String emrHoSoBenhAnId) {
        this.emrHoSoBenhAnId = ObjectIdUtil.stringToId(emrHoSoBenhAnId);            
    }

    public String getEmrBenhNhanId() {
        return ObjectIdUtil.idToString(emrBenhNhanId);
    }

    public void setEmrBenhNhanId(String emrBenhNhanId) {
        this.emrBenhNhanId = ObjectIdUtil.stringToId(emrBenhNhanId);
    }    
    
    public String getEmrCoSoKhamBenhId() {
        return ObjectIdUtil.idToString(emrCoSoKhamBenhId);
    }
    
    public void setEmrCoSoKhamBenhId(String emrCoSoKhamBenhId) {
        this.emrCoSoKhamBenhId = ObjectIdUtil.stringToId(emrCoSoKhamBenhId);
    }
}
