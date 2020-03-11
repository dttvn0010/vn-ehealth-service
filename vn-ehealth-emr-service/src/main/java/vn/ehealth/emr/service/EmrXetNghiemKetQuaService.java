package vn.ehealth.emr.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrXetNghiemKetQua;
import vn.ehealth.emr.repository.EmrXetNghiemKetQuaRepository;

@Service
public class EmrXetNghiemKetQuaService {
    
    @Autowired EmrXetNghiemKetQuaRepository emrXetNghiemKetQuaRepository;
    
    public List<EmrXetNghiemKetQua> getByEmrXetNghiemDichVuId(ObjectId emrXetNghiemDichVuId) {
        return emrXetNghiemKetQuaRepository.findByEmrXetNghiemDichVuId(emrXetNghiemDichVuId);
    }
    
    public EmrXetNghiemKetQua createOrUpdate(EmrXetNghiemKetQua emrXetNghiemKetQua) {
        return emrXetNghiemKetQuaRepository.save(emrXetNghiemKetQua);
    }
    
    public void deleteAllByEmrXetNghiemDichVuId(ObjectId emrXetNghiemDichVuId) {
        for(var x : getByEmrXetNghiemDichVuId(emrXetNghiemDichVuId)) {
            emrXetNghiemKetQuaRepository.delete(x);
        }
    }
}
