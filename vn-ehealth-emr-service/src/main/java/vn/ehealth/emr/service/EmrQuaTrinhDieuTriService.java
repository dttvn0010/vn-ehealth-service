package vn.ehealth.emr.service;

import java.util.List;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrQuaTrinhDieuTri;
import vn.ehealth.emr.repository.EmrQuaTrinhDieuTriRepository;

@Service
public class EmrQuaTrinhDieuTriService {

    @Autowired EmrQuaTrinhDieuTriRepository emrQuaTrinhDieuTriRepository;
    
    public List<EmrQuaTrinhDieuTri> getByEmrDieuTriId(ObjectId emrDieuTriId) {
        return emrQuaTrinhDieuTriRepository.findByEmrDieuTriId(emrDieuTriId);
    }
    
    public void deleteAllByEmrDieuTriId(ObjectId emrDieuTriId) {
        for(var qtdt : getByEmrDieuTriId(emrDieuTriId)) {
            emrQuaTrinhDieuTriRepository.delete(qtdt);
        }
    }
    
    public EmrQuaTrinhDieuTri createOrUpdate(@Nonnull EmrQuaTrinhDieuTri emrQuaTrinhDieuTri) {
        return emrQuaTrinhDieuTriRepository.save(emrQuaTrinhDieuTri);
    }
}
