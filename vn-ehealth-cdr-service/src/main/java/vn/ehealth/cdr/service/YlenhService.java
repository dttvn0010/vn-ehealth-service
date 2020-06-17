package vn.ehealth.cdr.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.model.Ylenh;
import vn.ehealth.cdr.repository.YlenhRepository;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;

@Service
public class YlenhService {

    @Autowired private YlenhRepository ylenhRepository;
    
    public Optional<Ylenh> getById(ObjectId id) {
        return ylenhRepository.findById(id);
    }
    
    public List<Ylenh> getByHoSoBenhAnId(ObjectId hoSoBenhAnId) {
        return ylenhRepository.findByHoSoBenhAnRefObjectIdAndTrangThai(hoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public List<Ylenh> getByHoSoBenhAnIdAndLoaiYlenh(ObjectId hoSoBenhAnId, String maLoaiYlenh) {
        return ylenhRepository.findByHoSoBenhAnRefObjectIdAndDmLoaiYlenhMaAndTrangThai(hoSoBenhAnId, maLoaiYlenh, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public List<Ylenh> getByHoSoBenhAnIdAndLoaiYlenhAndLoaiDVKT(ObjectId hoSoBenhAnId, String maLoaiYlenh, String maLoaiDVKT) {
        return ylenhRepository.findByHoSoBenhAnRefObjectIdAndDmLoaiYlenhMaAndDmLoaiDVKTMaAndTrangThai(hoSoBenhAnId, maLoaiYlenh, maLoaiDVKT, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public Ylenh save(@Nonnull Ylenh ylenh) {
        return ylenhRepository.save(ylenh);        
    }
    
    public Ylenh createOrUpdateFromHis(@Nonnull HoSoBenhAn hsba, @Nonnull Ylenh ylenh) {
        if(ylenh.idhis != null) {
            ylenh.id = ylenhRepository.findByIdhis(ylenh.idhis).map(x -> x.id).orElse(null);
        }
        
        ylenh.hoSoBenhAnRef = HoSoBenhAn.toEmrRef(hsba);
        ylenh.benhNhanRef = hsba.benhNhanRef;
        ylenh.coSoKhamBenhRef = hsba.coSoKhamBenhRef;
        return ylenhRepository.save(ylenh);
    }
}
