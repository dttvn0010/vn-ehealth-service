package vn.ehealth.cdr.service;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.CanboYte;
import vn.ehealth.cdr.repository.CanboYteRepository;

@Service
public class CanboYteService {

    @Autowired private CanboYteRepository canboYteRepository;
    
    public Optional<CanboYte> getById(ObjectId id) {
        return canboYteRepository.findById(id);
    }
    
    public Optional<CanboYte> getByChungChiHanhNghe(String chungChiHanhNghe) {
        return canboYteRepository.findByChungChiHanhNghe(chungChiHanhNghe);
    }
}
