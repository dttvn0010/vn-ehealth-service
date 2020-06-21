package vn.ehealth.cdr.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.ChamSoc;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.repository.ChamSocRepository;
import vn.ehealth.cdr.repository.UongThuocRepository;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;

@Service
public class ChamSocService {

    @Autowired private ChamSocRepository chamSocRepository;
    @Autowired private UongThuocRepository uongThuocRepository;
        
    public Optional<ChamSoc> getById(ObjectId id) {
        return chamSocRepository.findById(id);
    }
    
    public List<ChamSoc> getByHoSoBenhAnId(ObjectId hsbaId) {
        return chamSocRepository.findByHoSoBenhAnRefObjectIdAndTrangThai(hsbaId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public ChamSoc createOrUpdate(@Nonnull HoSoBenhAn hsba, @Nonnull ChamSoc chamSoc) {
        if(chamSoc.idhis != null) {
            chamSoc.id = chamSocRepository.findByIdhis(chamSoc.idhis).map(x -> x.id).orElse(null);
            if(chamSoc.id != null) {
                var dsUongThuocOld = uongThuocRepository.findByChamSocRefObjectIdAndTrangThai(chamSoc.id, TRANGTHAI_DULIEU.DEFAULT);
                for(var uongThuoc : dsUongThuocOld) {
                    uongThuocRepository.delete(uongThuoc);
                }
            }
        }
        
        chamSoc.hoSoBenhAnRef = HoSoBenhAn.toEmrRef(hsba);
        chamSoc.benhNhanRef = hsba.benhNhanRef;
        chamSoc.coSoKhamBenhRef = hsba.coSoKhamBenhRef;        
        chamSoc = chamSocRepository.save(chamSoc);
        
        if(chamSoc.dsUongThuoc != null) {
            for(var uongThuoc : chamSoc.dsUongThuoc) {
                uongThuoc.hoSoBenhAnRef = chamSoc.hoSoBenhAnRef;
                uongThuoc.benhNhanRef = chamSoc.benhNhanRef;
                uongThuoc.coSoKhamBenhRef = chamSoc.coSoKhamBenhRef;        
                uongThuoc.chamSocRef = ChamSoc.toEmrRef(chamSoc);
                uongThuoc.ytaChamSoc = chamSoc.ytaChamSoc;
                uongThuocRepository.save(uongThuoc);
            }
        }
        
        return chamSoc;
    }
}
