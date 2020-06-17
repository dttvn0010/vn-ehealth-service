package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.ChucNangSong;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.repository.ChucNangSongRepository;
import vn.ehealth.cdr.utils.CDRConstants.MA_HANH_DONG;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;

@Service
public class ChucNangSongService {

    @Autowired 
    private ChucNangSongRepository chucNangSongRepository;
    
    @Autowired private LogService logService;
    
    public Optional<ChucNangSong> getById(ObjectId id) {
        return chucNangSongRepository.findById(id);
    }
    
    public List<ChucNangSong> getByHoSoBenhAnId(ObjectId hoSoBenhAnId) {
        return chucNangSongRepository.findByHoSoBenhAnIdAndTrangThai(hoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public void createOrUpdateFromHIS( @Nonnull HoSoBenhAn hsba, @Nonnull List<ChucNangSong> cnsList, @Nonnull List<Object> cnsObjList, String jsonSt) {
        for(int i = 0; i < cnsList.size(); i++) {
            var cns = cnsList.get(i);
            if(cns.idhis != null) {
            	cns.id = chucNangSongRepository.findByIdhis(cns.idhis).map(x -> x.id).orElse(null);
            }
            
            cns.hoSoBenhAnRef = HoSoBenhAn.toEmrRef(hsba);
            cns.benhNhanRef = hsba.benhNhanRef;
            cns.coSoKhamBenhRef = hsba.coSoKhamBenhRef;
            cns = chucNangSongRepository.save(cns);
            cnsList.set(i, cns);
        }
        logService.logAction(HoSoBenhAn.class.getName() + ".ChucNangSongList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), null, 
                "", jsonSt);
    }
}
