package vn.ehealth.emr.service;

import java.util.List;
import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrChamSoc;
import vn.ehealth.emr.repository.EmrChamSocRepository;

@Service
public class EmrChamSocService {

    @Autowired EmrChamSocRepository emrChamSocRepository;
    @Autowired EmrQuaTrinhChamSocService emrQuaTrinhChamSocService;
    
    public List<EmrChamSoc> getByEmrVaoKhoaId(ObjectId emrVaoKhoaId) {
        return emrChamSocRepository.findByEmrVaoKhoaId(emrVaoKhoaId);
    }
    
    public void deleteAllByEmrVaoKhoaId(ObjectId emrVaoKhoaId) {
        for(var emrChamSoc : getByEmrVaoKhoaId(emrVaoKhoaId)) {
            emrChamSocRepository.delete(emrChamSoc);
        }
    }
    
    public EmrChamSoc createOrUpdate(@Nonnull EmrChamSoc emrChamSoc) {        
        emrChamSoc = emrChamSocRepository.save(emrChamSoc);
        
        emrQuaTrinhChamSocService.deleteAllById(emrChamSoc.id);
        
        for(int i = 0; emrChamSoc.emrQuaTrinhChamSocs != null && i < emrChamSoc.emrQuaTrinhChamSocs.size(); i++) {
            var qtcs = emrChamSoc.emrQuaTrinhChamSocs.get(i);
            qtcs.emrChamSocId = emrChamSoc.id;
            qtcs.emrVaoKhoaId = emrChamSoc.emrVaoKhoaId;
            qtcs.emrHoSoBenhAnId = emrChamSoc.emrHoSoBenhAnId;
            qtcs.emrBenhNhanId = emrChamSoc.emrBenhNhanId;
            qtcs.emrCoSoKhamBenhId = emrChamSoc.emrCoSoKhamBenhId;
            qtcs = emrQuaTrinhChamSocService.createOrUpdate(qtcs);
            emrChamSoc.emrQuaTrinhChamSocs.set(i, qtcs);
        }
       
        return emrChamSoc;
    }
    
    public void delete(ObjectId id) {
        emrChamSocRepository.deleteById(id);
    }
}
