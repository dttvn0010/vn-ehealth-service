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
    
    @Autowired LogService logService;
    
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
        return donThuocRepository.save(donThuoc);
    }
}
