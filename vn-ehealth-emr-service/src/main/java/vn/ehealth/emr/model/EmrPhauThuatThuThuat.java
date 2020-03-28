package vn.ehealth.emr.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.model.dto.DichVuKyThuat;
import vn.ehealth.emr.model.dto.PhauThuatThuThuat;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.ObjectIdUtil;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_phau_thuat_thu_thuat")
public class EmrPhauThuatThuThuat extends EmrDichVuKyThuat {

    private ObjectMapper objectMapper = EmrUtils.createObjectMapper();

    @Id
    public ObjectId id;
    public ObjectId emrHoSoBenhAnId;
    public ObjectId emrBenhNhanId;
    public ObjectId emrCoSoKhamBenhId;
    public int trangThai;
    public String idhis;

    public List<EmrDmContent> emrDmMaBenhChandoansaus = new ArrayList<>();
    public List<EmrDmContent> emrDmMaBenhChandoantruocs = new ArrayList<>();

    public EmrDmContent emrDmMaBenhChandoansau;
    public EmrDmContent emrDmMaBenhChandoantruoc;

    public EmrDmContent emrDmPhauThuThuat;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date ngaygiopttt;
    public EmrCanboYte bacsithuchien;
    public EmrCanboYte bacsygayme;
    public String chidinhptt;
    public String phuongphapvocam;
    public String luocdoptt;
    public String trinhtuptt;

    public String motachandoantruocpt;
    public String motachandoansaupt;

    public List<EmrFileDinhKem> emrFileDinhKemPttts = new ArrayList<>();

    @JsonInclude(Include.NON_NULL)
    public static class EmrThanhVienPttt {

        public EmrDmContent emrDmVaiTro;

        public EmrCanboYte bacsipttt;
    }

    public List<EmrThanhVienPttt> emrThanhVienPttts = new ArrayList<>();


    public String getId() {
        return ObjectIdUtil.idToString(id);
    }

    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    }

    public String getEmrHoSoBenhAnId() {
        return ObjectIdUtil.idToString(emrHoSoBenhAnId);
    }

    public void setEmrHoSoBenhAnId(String emrHoSoBenhAnId) {
        this.emrHoSoBenhAnId = ObjectIdUtil.stringToId(emrHoSoBenhAnId);
    }

    public String getEmrBenhNhanId() {
        return ObjectIdUtil.idToString(emrBenhNhanId);
    }

    public void setEmrBenhNhanId(String emrBenhNhanId) {
        this.emrBenhNhanId = ObjectIdUtil.stringToId(emrBenhNhanId);
    }

    public String getEmrCoSoKhamBenhId() {
        return ObjectIdUtil.idToString(emrCoSoKhamBenhId);
    }

    public void setEmrCoSoKhamBenhId(String emrCoSoKhamBenhId) {
        this.emrCoSoKhamBenhId = ObjectIdUtil.stringToId(emrCoSoKhamBenhId);
    }

    @Override
    public PhauThuatThuThuat toDto() {
        var dto = new PhauThuatThuThuat();
        dto.dmPttt = this.emrDmPhauThuThuat != null ? this.emrDmPhauThuThuat.toDto() : null;
//        dto.ngayYeuCau
        if (this.bacsithuchien != null) {
            dto.bacSiYeuCau = this.bacsithuchien.toRef();
        }
//        dto.noiDungYeuCau
        dto.ngayThucHien = this.ngaygiopttt;
//        dto.ketLuan =
        dto.trinhTuPttt = this.trinhtuptt;
//        dto.ghiChu =
//        dto.thuKyGhiChep
//        dto.chuTichHoiDong
//        dto.hoiDongPttt
//        dto.nguoiVietBaoCao
//        dto.nguoiDanhGiaKetQua
//        dto.ngayGioBaoCao
        if (this.emrThanhVienPttts != null) {
            List<PhauThuatThuThuat.ThanhVienPttt> hoiDongPttt = new ArrayList<>();
            emrThanhVienPttts.forEach(emrThanhVienPttt -> {
                PhauThuatThuThuat.ThanhVienPttt thanhVienPttt = new PhauThuatThuThuat.ThanhVienPttt();
                if (emrThanhVienPttt.bacsipttt != null) {
                    thanhVienPttt.bacsi = emrThanhVienPttt.bacsipttt.toRef();
                }
                if (emrThanhVienPttt.emrDmVaiTro != null) {
                    thanhVienPttt.dmVaiTro = emrThanhVienPttt.emrDmVaiTro.toDto();
                }
                hoiDongPttt.add(thanhVienPttt);
            });
        }
        return dto;
    }
}
