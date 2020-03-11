package vn.ehealth.emr.service;

import java.util.List;
import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrVaoKhoa;
import vn.ehealth.emr.repository.EmrVaoKhoaRespository;

@Service
public class EmrVaoKhoaService {

    @Autowired EmrVaoKhoaRespository emrVaoKhoaRespository;
    @Autowired EmrChamSocService emrChamSocService;
    @Autowired EmrChucNangSongService emrChucNangSongService;
    @Autowired EmrDieuTriService emrDieuTriService;
    @Autowired EmrHoiChanService emrHoiChanService;
    
    public List<EmrVaoKhoa> getByEmrHoSoBenhAnId(ObjectId hsbaId) {
        var sort = new Sort(Sort.Direction.ASC, "ngaygiovaokhoa");                
        return emrVaoKhoaRespository.findByEmrHoSoBenhAnId(hsbaId, sort);
    }
    
    public void deleteAllByEmrHoSoBenhAnId(ObjectId hsbaId) {
        for(var emrVaoKhoa : getByEmrHoSoBenhAnId(hsbaId)) {
            emrVaoKhoaRespository.delete(emrVaoKhoa);
        }
    }
    
    public void getEmrVaoKhoaDetail(@Nonnull EmrVaoKhoa emrVaoKhoa) {
        emrVaoKhoa.emrChamSocs = emrChamSocService.getByEmrVaoKhoaId(emrVaoKhoa.id);
        emrVaoKhoa.emrChucNangSongs = emrChucNangSongService.getByEmrVaoKhoaId(emrVaoKhoa.id);
        emrVaoKhoa.emrDieuTris = emrDieuTriService.getByEmrVaoKhoaId(emrVaoKhoa.id);
        emrVaoKhoa.emrHoiChans = emrHoiChanService.getByEmrVaoKhoaId(emrVaoKhoa.id);
    }
    
    public EmrVaoKhoa createOrUpdate(@Nonnull EmrVaoKhoa emrVaoKhoa) {
        emrVaoKhoa = emrVaoKhoaRespository.save(emrVaoKhoa);
        
        emrChamSocService.deleteAllByEmrVaoKhoaId(emrVaoKhoa.id);
        
        for(int i = 0; emrVaoKhoa.emrChamSocs != null && i < emrVaoKhoa.emrChamSocs.size(); i++) {
            var chamsoc = emrVaoKhoa.emrChamSocs.get(i);
            chamsoc.emrVaoKhoaId = emrVaoKhoa.id;
            chamsoc.emrHoSoBenhAnId = emrVaoKhoa.emrHoSoBenhAnId;
            chamsoc.emrBenhNhanId = emrVaoKhoa.emrBenhNhanId;
            chamsoc.emrCoSoKhamBenhId = emrVaoKhoa.emrCoSoKhamBenhId;
            chamsoc = emrChamSocService.createOrUpdate(chamsoc);
            emrVaoKhoa.emrChamSocs.set(i, chamsoc);
        }
        
        emrChucNangSongService.deleteAllByEmrVaoKhoaId(emrVaoKhoa.id);
        
        for(int i = 0; emrVaoKhoa.emrChucNangSongs != null && i < emrVaoKhoa.emrChucNangSongs.size(); i++) {
            var chucnangsong = emrVaoKhoa.emrChucNangSongs.get(i);
            chucnangsong.emrVaoKhoaId = emrVaoKhoa.id;
            chucnangsong.emrHoSoBenhAnId = emrVaoKhoa.emrHoSoBenhAnId;
            chucnangsong.emrBenhNhanId = emrVaoKhoa.emrBenhNhanId;
            chucnangsong.emrCoSoKhamBenhId = emrVaoKhoa.emrCoSoKhamBenhId;
            chucnangsong = emrChucNangSongService.createOrUpdate(chucnangsong);
            emrVaoKhoa.emrChucNangSongs.set(i, chucnangsong);
        }
        
        emrDieuTriService.deleteAllByEmrVaoKhoaId(emrVaoKhoa.id);
        
        for(int i = 0; emrVaoKhoa.emrDieuTris != null && i < emrVaoKhoa.emrDieuTris.size(); i++) {
            var dieutri = emrVaoKhoa.emrDieuTris.get(i);
            dieutri.emrVaoKhoaId = emrVaoKhoa.id;
            dieutri.emrHoSoBenhAnId = emrVaoKhoa.emrHoSoBenhAnId;
            dieutri.emrBenhNhanId = emrVaoKhoa.emrBenhNhanId;
            dieutri.emrCoSoKhamBenhId = emrVaoKhoa.emrCoSoKhamBenhId;
            dieutri = emrDieuTriService.createOrUpdate(dieutri);
            emrVaoKhoa.emrDieuTris.set(i, dieutri);            
        }
       
        emrHoiChanService.deleteAllByEmrVaoKhoaId(emrVaoKhoa.id);
        
        for(int i = 0; emrVaoKhoa.emrHoiChans != null && i < emrVaoKhoa.emrHoiChans.size(); i++) {
            var hoichan = emrVaoKhoa.emrHoiChans.get(i);
            hoichan.emrVaoKhoaId = emrVaoKhoa.id;
            hoichan.emrHoSoBenhAnId = emrVaoKhoa.emrHoSoBenhAnId;
            hoichan.emrBenhNhanId = emrVaoKhoa.emrBenhNhanId;
            hoichan.emrCoSoKhamBenhId = emrVaoKhoa.emrCoSoKhamBenhId;
            hoichan = emrHoiChanService.createOrUpdate(hoichan);
            emrVaoKhoa.emrHoiChans.set(i, hoichan);                    
        }
       
        return emrVaoKhoa;
    }
    
    public void saveByEmrHoSoBenhAnId(ObjectId hsbaId, List<EmrVaoKhoa> emrVaoKhoas) {
        deleteAllByEmrHoSoBenhAnId(hsbaId);
        
        for(var emrVaoKhoa : emrVaoKhoas) {
            emrVaoKhoaRespository.save(emrVaoKhoa);
        }
    }
}
