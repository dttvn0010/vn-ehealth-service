package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.DichVuKyThuat;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.repository.DichVuKyThuatRepository;
import vn.ehealth.cdr.utils.CDRConstants.MA_HANH_DONG;

@Service
public class DichVuKyThuatService {

    @Autowired private LogService logService;
    
    @Autowired private DichVuKyThuatRepository dichVuKyThuatRepository;
    
    public Optional<DichVuKyThuat> getById(ObjectId id) {
        return dichVuKyThuatRepository.findById(id);
    }
    
    public List<DichVuKyThuat> getByHsbaIdAndLoaiDVKT(String hsbaId, String maLoaiDVKT) {
        return dichVuKyThuatRepository.findByHoSoBenhAnRefObjectIdAndDmLoaiDVKTMa(hsbaId, maLoaiDVKT);
    }    
    
    
    public void createOrUpdateFromHIS(@Nonnull HoSoBenhAn hsba, @Nonnull List<DichVuKyThuat> dvktList, String jsonSt) {
        for(int i = 0; i < dvktList.size(); i++) {
            var dvkt = dvktList.get(i);
            if(dvkt.idhis != null) {
                dvkt.id = dichVuKyThuatRepository.findByIdhis(dvkt.idhis).map(x -> x.id).orElse(null);
            }
            dvkt.hoSoBenhAnRef = HoSoBenhAn.toEmrRef(hsba);
            dvkt.benhNhanRef = hsba.benhNhanRef;
            dvkt.coSoKhamBenhRef = hsba.coSoKhamBenhRef;
            dvkt = dichVuKyThuatRepository.save(dvkt);
            dvktList.set(i, dvkt);
        }
        
        logService.logAction(HoSoBenhAn.class.getName() + ".PhauThuatThuThuatList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(),  
                                null, "", jsonSt); 
    }
}
