package vn.ehealth.cdr.service;

import java.util.List;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.DieuTri;
import vn.ehealth.cdr.model.Ylenh;
import vn.ehealth.cdr.repository.DieuTriRepository;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;

@Service
public class DieuTriService {

    @Autowired private DieuTriRepository dieuTriRepository;
    
    public List<DieuTri> getByYlenhId(ObjectId ylenhId) {
        return dieuTriRepository.findByYlenhRefObjectIdAndTrangThai(ylenhId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public DieuTri save(DieuTri dieuTri) {
        return dieuTriRepository.save(dieuTri);
    }
    
    public DieuTri createOrUpdateDieuTri(@Nonnull Ylenh ylenh, @Nonnull DieuTri dieuTri) {
        
        dieuTri.ylenhRef = Ylenh.toEmrRef(ylenh);
        dieuTri.hoSoBenhAnRef = ylenh.hoSoBenhAnRef;
        dieuTri.benhNhanRef = ylenh.benhNhanRef;
        dieuTri.coSoKhamBenhRef = ylenh.coSoKhamBenhRef;
        dieuTri.bacSiDieuTri = ylenh.bacSiRaYlenh;
        dieuTri.ngayDieuTri = ylenh.ngayRaYlenh;
        return dieuTriRepository.save(dieuTri);
    }
}
