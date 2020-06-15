package vn.ehealth.cdr.service;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.ChamSoc;
import vn.ehealth.cdr.repository.ChamSocRepository;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;

@Service
public class ChamSocService {

    @Autowired private ChamSocRepository chamSocRepository;
        
    public Optional<ChamSoc> getById(ObjectId id) {
        return chamSocRepository.findById(id);
    }
    
    public List<ChamSoc> getByHoSoBenhAnId(ObjectId hoSoBenhAnId) {
        return chamSocRepository.findByHoSoBenhAnIdAndTrangThai(hoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
}
