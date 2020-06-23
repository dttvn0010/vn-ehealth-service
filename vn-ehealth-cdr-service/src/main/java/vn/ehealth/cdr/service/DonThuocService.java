package vn.ehealth.cdr.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.DonThuoc;
import vn.ehealth.cdr.model.Ylenh;
import vn.ehealth.cdr.repository.DonThuocRepository;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;

@Service
public class DonThuocService {

    @Autowired private DonThuocRepository donThuocRepository;
    @Autowired private DonThuocChiTietService donThuocChiTietService;
    
    public Optional<DonThuoc> getById(ObjectId id) {
        return donThuocRepository.findById(id);
    }
    
    public List<DonThuoc> getByHoSoBenhAnId(ObjectId hoSoBenhAnId) {
        return donThuocRepository.findByHoSoBenhAnRefObjectIdAndTrangThai(hoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public List<DonThuoc> getByYlenhId(ObjectId ylenhId) {
        return donThuocRepository.findByYlenhRefObjectIdAndTrangThai(ylenhId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public DonThuoc save(@Nonnull DonThuoc donThuoc) {
        return donThuocRepository.save(donThuoc);
    }
    
    public DonThuoc createOrUpdate(@Nonnull Ylenh ylenh, @Nonnull DonThuoc donThuoc) {
        if(donThuoc.idhis != null) {
            donThuoc.id = donThuocRepository.findByIdhis(donThuoc.idhis).map(x -> x.id).orElse(null);
        }
        donThuoc.ylenhRef = Ylenh.toEmrRef(ylenh);
        donThuoc.hoSoBenhAnRef = ylenh.hoSoBenhAnRef;
        donThuoc.benhNhanRef = ylenh.benhNhanRef;
        donThuoc.coSoKhamBenhRef = ylenh.coSoKhamBenhRef;
        donThuoc.bacSiKeDon = ylenh.bacSiRaYlenh;
        donThuoc.ngayKeDon = ylenh.ngayRaYlenh;
        donThuoc =  donThuocRepository.save(donThuoc);
        
        if(donThuoc.dsDonThuocChiTiet != null) {
            for(var dtct : donThuoc.dsDonThuocChiTiet) {
                dtct.soDon = donThuoc.soDon;
                dtct.ylenhRef = donThuoc.ylenhRef;
                dtct.hoSoBenhAnRef = donThuoc.hoSoBenhAnRef;
                dtct.benhNhanRef = donThuoc.benhNhanRef;
                dtct.coSoKhamBenhRef = donThuoc.coSoKhamBenhRef;
                dtct.donThuocRef = DonThuoc.toEmrRef(donThuoc);
                dtct.bacSiKeDon = donThuoc.bacSiKeDon;
                dtct.ngayKeDon = donThuoc.ngayKeDon;
                donThuocChiTietService.save(dtct);
            }
        }
        
        return donThuoc;
    }
    
    public void deleteByYlenhId(@Nonnull ObjectId ylenhId) {
        var dsDonThuoc = donThuocRepository.findByYlenhRefObjectIdAndTrangThai(ylenhId, TRANGTHAI_DULIEU.DEFAULT);
        for(var donThuoc : dsDonThuoc) {
            donThuocChiTietService.deleteByDonThuoc(donThuoc.id);
            donThuocRepository.delete(donThuoc);
        }
    }
}
