package vn.ehealth.cdr.service;

import java.util.List;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.ChamSoc;
import vn.ehealth.cdr.model.UongThuoc;
import vn.ehealth.cdr.repository.UongThuocRepository;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;

@Service
public class UongThuocService {

    @Autowired private UongThuocRepository uongThuocRepository;
    
    public List<UongThuoc> getByChamSocId(ObjectId chamSocId) {
        return uongThuocRepository.findByChamSocRefObjectIdAndTrangThai(chamSocId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public UongThuoc createOrUpdate(@Nonnull ChamSoc chamSoc, @Nonnull UongThuoc uongThuoc) {
        uongThuoc.hoSoBenhAnRef = chamSoc.hoSoBenhAnRef;
        uongThuoc.benhNhanRef = chamSoc.benhNhanRef;
        uongThuoc.coSoKhamBenhRef = chamSoc.coSoKhamBenhRef;        
        uongThuoc.chamSocRef = ChamSoc.toEmrRef(chamSoc);
        uongThuoc.ytaChamSoc = chamSoc.ytaChamSoc;
        return uongThuocRepository.save(uongThuoc);
    }
    
    public void deleteByChamSocId(@Nonnull ObjectId chamSocId) {
        var dsUongThuoc = uongThuocRepository.findByChamSocRefObjectIdAndTrangThai(chamSocId, TRANGTHAI_DULIEU.DEFAULT);
        for(var uongThuoc : dsUongThuoc) {
            uongThuocRepository.delete(uongThuoc);
        }
    }
}
