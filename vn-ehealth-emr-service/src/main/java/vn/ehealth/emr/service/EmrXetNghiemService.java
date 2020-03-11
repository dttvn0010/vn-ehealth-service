package vn.ehealth.emr.service;

import java.util.List;
import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrXetNghiem;
import vn.ehealth.emr.repository.EmrXetNghiemRepository;

@Service
public class EmrXetNghiemService {

    @Autowired EmrXetNghiemRepository emrXetNghiemRepository;
    @Autowired EmrXetNghiemDichVuService emrXetNghiemDichVuService; 
    
    public List<EmrXetNghiem> getByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        return emrXetNghiemRepository.findByEmrHoSoBenhAnId(emrHoSoBenhAnId);
    }
    
    public void deleteAllByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        for(var xn : getByEmrHoSoBenhAnId(emrHoSoBenhAnId)) {
            emrXetNghiemRepository.delete(xn);
        }
    }
    
    public EmrXetNghiem createOrUpdate(@Nonnull EmrXetNghiem xn) {
        xn = emrXetNghiemRepository.save(xn);
        
        emrXetNghiemDichVuService.deleteAllByXetNghiemId(xn.id);
        
        for(int i = 0; xn.emrXetNghiemDichVus != null && i < xn.emrXetNghiemDichVus.size(); i++) {
            var xndv = xn.emrXetNghiemDichVus.get(i);
            xndv.emrXetNghiemId = xn.id;
            xndv.emrHoSoBenhAnId = xn.emrHoSoBenhAnId;
            xndv.emrBenhNhanId = xn.emrBenhNhanId;
            xndv.emrCoSoKhamBenhId = xn.emrCoSoKhamBenhId;
            xndv = emrXetNghiemDichVuService.createOrUpdate(xndv);
            xn.emrXetNghiemDichVus.set(i, xndv);
        }
        
        return xn;
    }
    
    public void delete(ObjectId id) {
        emrXetNghiemRepository.deleteById(id);
    }
}
