package vn.ehealth.emr.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrPhauThuatThuThuat;
import vn.ehealth.emr.repository.EmrPhauThuatThuThuatRepository;

@Service
public class EmrPhauThuatThuThuatService {

    @Autowired EmrPhauThuatThuThuatRepository emrPhauThuatThuThuatRepository;
    
    public List<EmrPhauThuatThuThuat> getByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId){
        return emrPhauThuatThuThuatRepository.findByEmrHoSoBenhAnId(emrHoSoBenhAnId);
    }
    
    public void deleteAllByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        for(var pttt : getByEmrHoSoBenhAnId(emrHoSoBenhAnId)) {
            emrPhauThuatThuThuatRepository.delete(pttt);
        }
    }
    
    public EmrPhauThuatThuThuat createOrUpdate(EmrPhauThuatThuThuat emrPhauThuatThuThuat) {
        return emrPhauThuatThuThuatRepository.save(emrPhauThuatThuThuat);                
    }
    
    public void delete(ObjectId id) {
        emrPhauThuatThuThuatRepository.deleteById(id);
    }
}
