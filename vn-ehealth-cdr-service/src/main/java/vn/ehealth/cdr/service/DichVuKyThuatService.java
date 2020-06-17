package vn.ehealth.cdr.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.DichVuKyThuat;
import vn.ehealth.cdr.model.Ylenh;
import vn.ehealth.cdr.repository.DichVuKyThuatRepository;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;

@Service
public class DichVuKyThuatService {

    @Autowired private DichVuKyThuatRepository dichVuKyThuatRepository;
    
    public Optional<DichVuKyThuat> getById(ObjectId id) {
        return dichVuKyThuatRepository.findById(id);
    }
    
    public List<DichVuKyThuat> getByHsbaIdAndLoaiDVKT(ObjectId hsbaId, String maLoaiDVKT) {
        return dichVuKyThuatRepository.findByHoSoBenhAnRefObjectIdAndDmLoaiDVKTMaAndTrangThai(hsbaId, maLoaiDVKT, TRANGTHAI_DULIEU.DEFAULT);
    }    
    
    public List<DichVuKyThuat> getByYlenhId(ObjectId ylenhId) {
        return dichVuKyThuatRepository.findByYlenhRefObjectIdAndTrangThai(ylenhId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public DichVuKyThuat save(@Nonnull DichVuKyThuat dvkt) {
        return dichVuKyThuatRepository.save(dvkt);
    }
   
    public DichVuKyThuat createOrUpdate(@Nonnull Ylenh ylenh, @Nonnull DichVuKyThuat dvkt) {
        
        if(dvkt.idhis != null) {
            dvkt.id = dichVuKyThuatRepository.findByIdhis(dvkt.idhis).map(x -> x.id).orElse(null);
        }
        
        dvkt.hoSoBenhAnRef = ylenh.hoSoBenhAnRef;
        dvkt.benhNhanRef = ylenh.benhNhanRef;
        dvkt.coSoKhamBenhRef = ylenh.coSoKhamBenhRef;
        dvkt.ylenhRef = Ylenh.toEmrRef(ylenh);
        return dichVuKyThuatRepository.save(dvkt);
               
    }
}
