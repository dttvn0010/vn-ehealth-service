package vn.ehealth.emr.service;

import java.util.List;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrDieuTri;
import vn.ehealth.emr.repository.EmrDieuTriRepository;

@Service
public class EmrDieuTriService {
    
    @Autowired EmrDieuTriRepository emrDieuTriRepository;
    @Autowired EmrQuaTrinhDieuTriService emrQuaTrinhDieuTriService;
    
    public List<EmrDieuTri> getByEmrVaoKhoaId(ObjectId emrVaoKhoaId) {
        return emrDieuTriRepository.findByEmrVaoKhoaId(emrVaoKhoaId);        
    }
    
    public void deleteAllByEmrVaoKhoaId(ObjectId emrVaoKhoaId) {
        for(var dieutri : getByEmrVaoKhoaId(emrVaoKhoaId)) {
            emrDieuTriRepository.delete(dieutri);
        }
    }

    public EmrDieuTri createOrUpdate(@Nonnull EmrDieuTri emrDieuTri) {
        emrDieuTri = emrDieuTriRepository.save(emrDieuTri);
        
        emrQuaTrinhDieuTriService.deleteAllByEmrDieuTriId(emrDieuTri.id);
        
        for(int i = 0; emrDieuTri.emrQuaTrinhDieuTris != null && i < emrDieuTri.emrQuaTrinhDieuTris.size(); i++) {
            var qtdt = emrDieuTri.emrQuaTrinhDieuTris.get(i);
            qtdt.emrDieuTriId = emrDieuTri.id;
            qtdt.emrVaoKhoaId = emrDieuTri.emrVaoKhoaId;
            qtdt.emrHoSoBenhAnId = emrDieuTri.emrHoSoBenhAnId;
            qtdt.emrBenhNhanId = emrDieuTri.emrBenhNhanId;
            qtdt.emrCoSoKhamBenhId = emrDieuTri.emrCoSoKhamBenhId;
            qtdt = emrQuaTrinhDieuTriService.createOrUpdate(qtdt);
            emrDieuTri.emrQuaTrinhDieuTris.set(i, qtdt);            
        }
        
        return emrDieuTriRepository.save(emrDieuTri);
    }
    
    public void delete(ObjectId id) {
        emrDieuTriRepository.deleteById(id);
    }
}
