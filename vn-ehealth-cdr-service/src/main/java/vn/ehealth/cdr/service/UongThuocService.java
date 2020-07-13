package vn.ehealth.cdr.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.ChamSoc;
import vn.ehealth.cdr.model.DonThuoc;
import vn.ehealth.cdr.model.UongThuoc;
import vn.ehealth.cdr.model.component.EmrRef;
import vn.ehealth.cdr.repository.UongThuocRepository;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;

@Service
public class UongThuocService {

    @Autowired private UongThuocRepository uongThuocRepository;
    @Autowired private DonThuocChiTietService donThuocChiTietService;
    @Autowired private DonThuocService donThuocService;
    @Autowired private MongoTemplate mongoTemplate;
    
    public List<UongThuoc> getByChamSocId(ObjectId chamSocId) {
        var criteria = Criteria.where("chamSocRef.objectId").is(chamSocId)
                                .and("trangThai").ne(TRANGTHAI_DULIEU.DA_XOA);
        
        return mongoTemplate.find(new Query(criteria), UongThuoc.class);
    }
    
    public UongThuoc createOrUpdate(@Nonnull ChamSoc chamSoc, @Nonnull UongThuoc uongThuoc) {
        uongThuoc.hoSoBenhAnRef = chamSoc.hoSoBenhAnRef;
        uongThuoc.benhNhanRef = chamSoc.benhNhanRef;
        uongThuoc.coSoKhamBenhRef = chamSoc.coSoKhamBenhRef;        
        uongThuoc.chamSocRef = ChamSoc.toEmrRef(chamSoc);
        uongThuoc.ytaChamSoc = chamSoc.ytaChamSoc;
        uongThuoc.ngayUongThuoc = chamSoc.ngayChamSoc;
        
        var dtct = donThuocChiTietService.getById(EmrRef.toObjectId(uongThuoc.donThuocChiTietRef));
        
        if(dtct != null) {
            uongThuoc.dmThuoc = dtct.dmThuoc;
            uongThuoc.dmDuongDungThuoc = dtct.dmDuongDungThuoc;
            uongThuoc.bacSiChiDinh = dtct.bacSiKeDon;
            uongThuoc.ngayChiDinh = dtct.ngayKeDon;
        }
            
        
        for(var tanSuatDungThuoc :dtct.dsTanSuatDungThuoc) {
            String maThoiDiemDungThuoc = Optional.ofNullable(tanSuatDungThuoc.dmThoiDiemDungThuoc)
                                                .map(x -> x.ma).orElse("");
            
            if(maThoiDiemDungThuoc.equals(uongThuoc.maThoiDiemUongThuoc)) {
                uongThuoc.dmThoiDiemDungThuoc = tanSuatDungThuoc.dmThoiDiemDungThuoc;
                uongThuoc.soLuong = tanSuatDungThuoc.soLuong;
                uongThuoc.donVi = tanSuatDungThuoc.donVi;
                break;
            }
        }
        
        var donThuoc = donThuocService.getById(EmrRef.toObjectId(dtct.donThuocRef));        
        uongThuoc.donThuocRef = DonThuoc.toEmrRef(donThuoc);
        return uongThuocRepository.save(uongThuoc);
    }
    
    public void deleteByChamSocId(@Nonnull ObjectId chamSocId) {
        var dsUongThuoc = getByChamSocId(chamSocId);
        for(var uongThuoc : dsUongThuoc) {
            uongThuoc.trangThai = TRANGTHAI_DULIEU.DA_XOA;
            uongThuocRepository.save(uongThuoc);
        }
    }
}
