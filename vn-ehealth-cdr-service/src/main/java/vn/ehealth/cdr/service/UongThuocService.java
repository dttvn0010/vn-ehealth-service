package vn.ehealth.cdr.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.UongThuoc;
import vn.ehealth.cdr.repository.UongThuocRepository;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;

@Service
public class UongThuocService {

    @Autowired private UongThuocRepository uongThuocRepository;
    
    public List<UongThuoc> getByChamSocId(ObjectId chamSocId) {
        return uongThuocRepository.findByChamSocRefObjectIdAndTrangThai(chamSocId, TRANGTHAI_DULIEU.DEFAULT);
    }
}
