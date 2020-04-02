package vn.ehealth.cdr.model;


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

import vn.ehealth.cdr.utils.ObjectIdUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;


@JsonInclude(Include.NON_NULL)
@Document(collection = "don_thuoc")
public class DonThuoc {
    
    @Id public ObjectId id;    
    public ObjectId hoSoBenhAnId;    
    public ObjectId benhNhanId;
    public ObjectId coSoKhamBenhId;
    public int trangThai;
    public String idhis;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayKeDon;
    public CanboYte bacSiKeDon;
    public String soDon;
    
    public List<FileDinhKem> dsFileDinhKemDonThuoc = new ArrayList<>();
   
    @JsonInclude(Include.NON_NULL)
    public static class DonThuocChiTiet {
        public DanhMuc dmThuoc;
        public DanhMuc dmDuongDungThuoc;
        public DanhMuc dmTanSuatDungThuoc;
        public DanhMuc dmChiDanDungThuoc;
        
        @JsonIgnore
        public Date ngayKeDon;
        
        @JsonIgnore
        public CanboYte bacSiKeDon;
        
        @JsonIgnore
        public String soDon;
        
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        public Date ngayBatDau;
        
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        public Date ngayKetThuc;
        
        public String lieuLuongThuoc;    
        public String chiDanDungThuoc;
        public String bietDuoc;
        
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
            mRequest.setRequester(CanboYte.toRef(bacSiKeDon));
            mRequest.setAuthoredOn(ngayKeDon);
            mRequest.setGroupIdentifier(createIdentifier(soDon, IdentifierSystem.DON_THUOC));
            
            mRequest.setMedication(DanhMuc.toConcept(dmThuoc, CodeSystemValue.DM_THUOC));
           
            var dosage = new Dosage();
            dosage.setText(chiDanDungThuoc);
            dosage.setRoute(DanhMuc.toConcept(dmDuongDungThuoc, CodeSystemValue.DM_DUONG_DUNG_THUOC));
                
            //TODO: TanXuat
            
            mRequest.getDosageInstruction().add(dosage);
            
            var arr = lieuLuongThuoc.split(" ");
            var soLuong = parseInt(arr[0]);
            if(soLuong != null) {
                var quantity = new Quantity();
                quantity.setValue(soLuong);
                
                if(arr.length > 1) {
                    quantity.setUnit(arr[1]);
                }
                mRequest.getDispenseRequest().setQuantity(quantity);
            }
            
            if(ngayBatDau != null || ngayKetThuc != null) {
                var period = new Period();
                period.setStart(ngayBatDau);
                period.setEnd(ngayKetThuc);
                mRequest.getDispenseRequest().setValidityPeriod(period);
            }
            
            return mRequest;
        }
    }
    
    public List<DonThuocChiTiet> dsDonThuocChiTiet = new ArrayList<>();
    
    public String getId() {
        return ObjectIdUtil.idToString(id);
    }
    
    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    }

    public String getHoSoBenhAnId() {
        return ObjectIdUtil.idToString(hoSoBenhAnId);
    }
    
    public void setHoSoBenhAnId(String hoSoBenhAnId) {
        this.hoSoBenhAnId = ObjectIdUtil.stringToId(hoSoBenhAnId);            
    }

    public String getBenhNhanId() {
        return ObjectIdUtil.idToString(benhNhanId);
    }

    public void setBenhNhanId(String benhNhanId) {
        this.benhNhanId = ObjectIdUtil.stringToId(benhNhanId);
    }    
    
    public String getCoSoKhamBenhId() {
        return ObjectIdUtil.idToString(coSoKhamBenhId);
    }
    
    public void setCoSoKhamBenhId(String coSoKhamBenhId) {
        this.coSoKhamBenhId = ObjectIdUtil.stringToId(coSoKhamBenhId);
    }
}
