package vn.ehealth.cdr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.DieuTri;
import vn.ehealth.cdr.repository.DieuTriRepository;

@Service
public class DieuTriService {

    @Autowired private DieuTriRepository dieuTriRepository;
    
    public DieuTri save(DieuTri dieuTri) {
        return dieuTriRepository.save(dieuTri);
    }
}
