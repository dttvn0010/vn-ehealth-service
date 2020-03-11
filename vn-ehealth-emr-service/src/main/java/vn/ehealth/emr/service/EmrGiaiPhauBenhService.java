package vn.ehealth.emr.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrGiaiPhauBenh;
import vn.ehealth.emr.repository.EmrGiaiPhauBenhRepository;

@Service
public class EmrGiaiPhauBenhService {

    @Autowired EmrGiaiPhauBenhRepository emrGiaiPhauBenhRepository;
    
    public List<EmrGiaiPhauBenh> getByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        return emrGiaiPhauBenhRepository.findByEmrHoSoBenhAnId(emrHoSoBenhAnId);
    }
    
    public void deleteAllByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        for(var gpb : getByEmrHoSoBenhAnId(emrHoSoBenhAnId)) {
            emrGiaiPhauBenhRepository.delete(gpb);
        }
    }
    
    public EmrGiaiPhauBenh createOrUpdate(EmrGiaiPhauBenh emrGiaiPhauBenh) {
        return emrGiaiPhauBenhRepository.save(emrGiaiPhauBenh);
    }
    
    public void delete(ObjectId id) {
        emrGiaiPhauBenhRepository.deleteById(id);
    }
}
