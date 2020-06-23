package vn.ehealth.cdr.model;

import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createIdentifier;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createReference;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Quantity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.cdr.model.component.CanboYteDTO;
import vn.ehealth.cdr.model.component.DanhMuc;
import vn.ehealth.cdr.model.component.EmrRef;
import vn.ehealth.cdr.utils.ObjectIdUtil;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;

@JsonInclude(Include.NON_NULL)
@Document(collection = "don_thuoc_chi_tiet")
public class DonThuocChiTiet {

    public static class TanSuatDungThuoc {
        public Integer soLuong;
        public String donVi;
        public DanhMuc dmThoiDiemDungThuoc;
    }
    
    public static class LieuLuongDungThuoc {
        public Integer soLuong;
        public String donVi;
        
        
        public LieuLuongDungThuoc() {
            
        }
        
        public LieuLuongDungThuoc(String lieuLuong) {
            if(lieuLuong == null) return;
            
            var items = List.of(lieuLuong.split(" "));
            items = FPUtil.filter(items, x -> !StringUtils.isBlank(x));
            
            try {
                this.soLuong = Integer.parseInt(items.get(0));
            }catch(NumberFormatException e) {
                
            }
            
            if(this.soLuong != null && items.size() > 0) {
                this.donVi = DataConvertUtil.joinString(items.subList(1, items.size()), " ");
            }                
        }
    }
    
    @Id public ObjectId id;    
    public EmrRef hoSoBenhAnRef;
    public EmrRef benhNhanRef;
    public EmrRef coSoKhamBenhRef;
    public EmrRef ylenhRef;
    public EmrRef donThuocRef;
    public int trangThai;
    
    public Date ngayKeDon;
    public CanboYteDTO bacSiKeDon;
    public String soDon;
    
    public DanhMuc dmThuoc;
    public DanhMuc dmDuongDungThuoc;
    public DanhMuc dmChiDanDungThuoc;
    
    @JsonFormat(pattern="yyyy-MM-dd")
    public Date ngayBatDau;
    
    @JsonFormat(pattern="yyyy-MM-dd")
    public Date ngayKetThuc;
    
    public LieuLuongDungThuoc lieuLuongThuoc;
    
    public List<TanSuatDungThuoc> dsTanSuatDungThuoc = new ArrayList<>();
    
    public String chiDanDungThuoc;
    public String bietDuoc; 
    
    public String getId() { 
        return ObjectIdUtil.idToString(id); 
    }
    
    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    } 
    
    public long getSoNgayKe() {
        if(ngayBatDau != null && ngayKetThuc != null) {
            var d = Duration.between(ngayBatDau.toInstant(), ngayKetThuc.toInstant());
            return d.toDays() + 1;
        }
        return 0;
    }
    
    public long getTongLieu() {
        long soNgayKe = getSoNgayKe();
        long lieuLuongNgay = 0;
        
        if(dsTanSuatDungThuoc != null) {
            for(var tanSuatDungThuoc : dsTanSuatDungThuoc) {
                if(tanSuatDungThuoc.soLuong != null) {
                    lieuLuongNgay += tanSuatDungThuoc.soLuong;
                }
            }
        }
        
        return soNgayKe * lieuLuongNgay;
    }    
    
    
    public MedicationRequest toFHir(Encounter enc) {
    
        var mRequest = new MedicationRequest();
        mRequest.setSubject(enc.getSubject());
        mRequest.setEncounter(createReference(enc));
        mRequest.setRequester(CanboYteDTO.toRef(bacSiKeDon));
        mRequest.setAuthoredOn(ngayKeDon);
        mRequest.setGroupIdentifier(createIdentifier(soDon, IdentifierSystem.MEDICATION_REQUEST));
        
        mRequest.setMedication(DanhMuc.toConcept(dmThuoc, CodeSystemValue.DM_THUOC));
        
        var dosage = new Dosage();
        dosage.setText(chiDanDungThuoc);
        dosage.setRoute(DanhMuc.toConcept(dmDuongDungThuoc, CodeSystemValue.DM_DUONG_DUNG_THUOC));
        mRequest.getDosageInstruction().add(dosage);
        
        if(lieuLuongThuoc != null && lieuLuongThuoc.soLuong != null) {
            var quantity = new Quantity();
            quantity.setValue(lieuLuongThuoc.soLuong);
            quantity.setUnit(lieuLuongThuoc.donVi);                
            
            mRequest.getDispenseRequest().setQuantity(quantity);
        }
        
        var repeat = dosage.getTiming().getRepeat();
        
        if(dsTanSuatDungThuoc != null) {
            for(var tanSuatDungThuoc : dsTanSuatDungThuoc) {
                if(tanSuatDungThuoc.soLuong != null && tanSuatDungThuoc.soLuong > 0 && tanSuatDungThuoc.dmThoiDiemDungThuoc != null) {                            
                    repeat.addTimeOfDay(tanSuatDungThuoc.dmThoiDiemDungThuoc.code);
                    var doseAndRate = dosage.addDoseAndRate();
                    var rateQty = new Quantity();
                    rateQty.setValue(tanSuatDungThuoc.soLuong);
                    rateQty.setUnit(tanSuatDungThuoc.donVi);
                    doseAndRate.setRate(rateQty);
                }
               
            }
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
