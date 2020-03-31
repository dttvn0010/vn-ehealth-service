package vn.ehealth.emr.service;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrCoSoKhamBenh;
import vn.ehealth.emr.repository.EmrCoSoKhamBenhRepository;

@Service
public class EmrCoSoKhamBenhService {

    @Autowired EmrCoSoKhamBenhRepository emrCoSoKhamBenhRepository;
    
    public Optional<EmrCoSoKhamBenh> getById(ObjectId id){        
        return emrCoSoKhamBenhRepository.findById(id);        
    }
    
    public Optional<EmrCoSoKhamBenh> getByMa(String ma) {
        return emrCoSoKhamBenhRepository.findByMa(ma); 
    }
    
    public EmrCoSoKhamBenh save(@Nonnull EmrCoSoKhamBenh cskb) {
        return emrCoSoKhamBenhRepository.save(cskb);
    }
}
