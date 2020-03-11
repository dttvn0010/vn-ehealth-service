package vn.ehealth.emr.service;

import java.util.List;
import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrXetNghiemDichVu;
import vn.ehealth.emr.repository.EmrXetNghiemDichVuRepository;

@Service
public class EmrXetNghiemDichVuService {

    @Autowired EmrXetNghiemDichVuRepository emrXetNghiemDichVuRepository;
    @Autowired EmrXetNghiemKetQuaService emrXetNghiemKetQuaService;
    
    public List<EmrXetNghiemDichVu> getByEmrXetNghiemId(ObjectId emrXetNghiemId) {
        return emrXetNghiemDichVuRepository.findByEmrXetNghiemId(emrXetNghiemId);        
    }
    
    public EmrXetNghiemDichVu createOrUpdate(@Nonnull EmrXetNghiemDichVu xndv) {
        xndv = emrXetNghiemDichVuRepository.save(xndv);
        
        emrXetNghiemKetQuaService.deleteAllByEmrXetNghiemDichVuId(xndv.id);
        
        for(int i = 0; xndv.emrXetNghiemKetQuas != null && i < xndv.emrXetNghiemKetQuas.size(); i++) {
            var xnkq = xndv.emrXetNghiemKetQuas.get(i);
            xnkq.emrXetNghiemDichVuId = xndv.id;
            xnkq.emrXetNghiemId = xndv.emrXetNghiemId;
            xnkq.emrHoSoBenhAnId = xndv.emrHoSoBenhAnId;
            xnkq.emrBenhNhanId = xndv.emrBenhNhanId;
            xnkq.emrCoSoKhamBenhId = xndv.emrCoSoKhamBenhId;
            xnkq = emrXetNghiemKetQuaService.createOrUpdate(xnkq);
            xndv.emrXetNghiemKetQuas.set(i, xnkq);            
        }
        
        return xndv;        
    }
    
    public void deleteAllByXetNghiemId(ObjectId emrXetNghiemId) {
        for(var x : getByEmrXetNghiemId(emrXetNghiemId)) {
            emrXetNghiemDichVuRepository.delete(x);
        }
    }
}
