package vn.ehealth.cdr.model;


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
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.FPUtil;

import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;


@JsonInclude(Include.NON_NULL)
@Document(collection = "don_thuoc")
public class DonThuoc {
    
    @Id public ObjectId id;    
    public EmrRef hoSoBenhAnRef;
    public EmrRef benhNhanRef;
    public EmrRef coSoKhamBenhRef;
    public EmrRef ylenhRef;
    
    public int trangThai;
    public String idhis;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayKeDon;
    public CanboYteDTO bacSiKeDon;
    public String soDon;
    
    public List<FileDinhKem> dsFileDinhKemDonThuoc = new ArrayList<>();
   
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
    
    @JsonInclude(Include.NON_NULL)
    public static class DonThuocChiTiet {               
        
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
    }
    
    public List<DonThuocChiTiet> dsDonThuocChiTiet = new ArrayList<>();
    
    public String getId() {
        return ObjectIdUtil.idToString(id);
    }
    
    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    }
    
    public List<MedicationRequest> toFHir(Encounter enc) {
        var lst = new ArrayList<MedicationRequest>();
        
        if(enc != null && dsDonThuocChiTiet != null) {
            for(var dtct : dsDonThuocChiTiet) {
            
                var mRequest = new MedicationRequest();
                mRequest.setSubject(enc.getSubject());
                mRequest.setEncounter(createReference(enc));
                mRequest.setRequester(CanboYteDTO.toRef(bacSiKeDon));
                mRequest.setAuthoredOn(ngayKeDon);
                mRequest.setGroupIdentifier(createIdentifier(soDon, IdentifierSystem.MEDICATION_REQUEST));
                
                mRequest.setMedication(DanhMuc.toConcept(dtct.dmThuoc, CodeSystemValue.DM_THUOC));
                
                var dosage = new Dosage();
                dosage.setText(dtct.chiDanDungThuoc);
                dosage.setRoute(DanhMuc.toConcept(dtct.dmDuongDungThuoc, CodeSystemValue.DM_DUONG_DUNG_THUOC));
                mRequest.getDosageInstruction().add(dosage);
                
                if(dtct.lieuLuongThuoc != null && dtct.lieuLuongThuoc.soLuong != null) {
                    var quantity = new Quantity();
                    quantity.setValue(dtct.lieuLuongThuoc.soLuong);
                    quantity.setUnit(dtct.lieuLuongThuoc.donVi);                
                    
                    mRequest.getDispenseRequest().setQuantity(quantity);
                }
                
                var repeat = dosage.getTiming().getRepeat();
                
                if(dtct.dsTanSuatDungThuoc != null) {
                    for(var tanSuatDungThuoc : dtct.dsTanSuatDungThuoc) {
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
                                
                if(dtct.ngayBatDau != null || dtct.ngayKetThuc != null) {
                    var period = new Period();
                    period.setStart(dtct.ngayBatDau);
                    period.setEnd(dtct.ngayKetThuc);
                    mRequest.getDispenseRequest().setValidityPeriod(period);
                }
                
                lst.add(mRequest);
            }
        }
        
        return lst;
    }
}
