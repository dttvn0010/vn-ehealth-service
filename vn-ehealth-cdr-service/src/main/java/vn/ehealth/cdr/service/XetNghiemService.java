package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.model.XetNghiem;
import vn.ehealth.cdr.repository.XetNghiemRepository;
import vn.ehealth.cdr.utils.CDRConstants.MA_HANH_DONG;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;

@Service
public class XetNghiemService {

    @Autowired private XetNghiemRepository xetNghiemRepository;
    
    @Autowired private LogService logService;
    
    public List<XetNghiem> getByHoSoBenhAnId(ObjectId hoSoBenhAnId) {
        return xetNghiemRepository.findByHoSoBenhAnIdAndTrangThai(hoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public List<XetNghiem> getByBenhNhanId(ObjectId benhNhanId) {
        return xetNghiemRepository.findByBenhNhanIdAndTrangThai(benhNhanId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public void createOrUpdateFromHIS(@Nonnull HoSoBenhAn hsba, @Nonnull List<XetNghiem> xetnghiemList, String jsonSt) {
        for(int i = 0; i < xetnghiemList.size(); i++) {
            var xetnghiem = xetnghiemList.get(i);
            if(xetnghiem.idhis != null) {
                xetnghiem.id = xetNghiemRepository.findByIdhis(xetnghiem.idhis).map(x -> x.id).orElse(null);
            }
            xetnghiem.hoSoBenhAnId = hsba.id;
            xetnghiem.benhNhanId = hsba.benhNhanId;
            xetnghiem.coSoKhamBenhId = hsba.coSoKhamBenhId;
            xetnghiem = xetNghiemRepository.save(xetnghiem);
            xetnghiemList.set(i, xetnghiem);
        }
        logService.logAction(HoSoBenhAn.class.getName() + ".XetNghiemList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(),  
                                null, "", jsonSt);
    }
}
