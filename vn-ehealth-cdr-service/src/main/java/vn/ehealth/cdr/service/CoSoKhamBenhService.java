package vn.ehealth.cdr.service;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.CoSoKhamBenh;
import vn.ehealth.cdr.repository.CoSoKhamBenhRepository;

@Service
public class CoSoKhamBenhService {

    @Autowired CoSoKhamBenhRepository coSoKhamBenhRepository;
    
    public Optional<CoSoKhamBenh> getById(ObjectId id){        
        return coSoKhamBenhRepository.findById(id);        
    }
    
    public Optional<CoSoKhamBenh> getByMa(String ma) {
        return coSoKhamBenhRepository.findByMa(ma); 
    }
    
    public CoSoKhamBenh save(@Nonnull CoSoKhamBenh cskb) {
        return coSoKhamBenhRepository.save(cskb);
    }
}
