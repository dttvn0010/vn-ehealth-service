package vn.ehealth.cdr.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return ylenhRepository.findByHoSoBenhAnIdAndTrangThai(hoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public Ylenh save(@Nonnull Ylenh ylenh) {
        return ylenhRepository.save(ylenh);        
    }
}
