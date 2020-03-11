package vn.ehealth.emr.service;

import java.util.List;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrQuaTrinhChamSoc;
import vn.ehealth.emr.repository.EmrQuaTrinhChamSocRepository;

@Service
public class EmrQuaTrinhChamSocService {
    
    @Autowired EmrQuaTrinhChamSocRepository emrQuaTrinhChamSocRepository;
    
    public List<EmrQuaTrinhChamSoc> getByEmrChamSocId(ObjectId emrChamSocId) {
        return emrQuaTrinhChamSocRepository.findByEmrChamSocId(emrChamSocId);
    }
    
    public void deleteAllById(ObjectId emrChamSocId) {
        for(var qtcs : getByEmrChamSocId(emrChamSocId)) {
            emrQuaTrinhChamSocRepository.delete(qtcs);
        }
    }
    
    public EmrQuaTrinhChamSoc createOrUpdate(@Nonnull EmrQuaTrinhChamSoc emrQuaTrinhChamSoc) {
        return emrQuaTrinhChamSocRepository.save(emrQuaTrinhChamSoc);
    }

}
