package test;

import java.util.List;
import org.openhealthtools.mdht.uml.cda.Act;
import org.openhealthtools.mdht.uml.cda.Component3;
import org.openhealthtools.mdht.uml.cda.Entry;
import org.openhealthtools.mdht.uml.cda.EntryRelationship;
import org.openhealthtools.mdht.uml.cda.Observation;
import org.openhealthtools.mdht.uml.cda.Section;

public class ChanDoanUtil {
	
	public static Component3 createChanDoanComponent() {
		var section = DataUtil.createSection(CDAUtil.getTemplateId("chanDoanSection"));
		return DataUtil.createComponent3(section);
	}
	
	public static Entry getEntryChanDoanNoiDen(Section chanDoanSection) {
		return CDAUtil.getEntryByActTemplateIds(chanDoanSection, CDAUtil.getTemplateId("actChanDoanNoiDen")); 
	}
	
	public Entry getEntryChanDoanVaoVien(Section chanDoanSection) {
		return CDAUtil. getEntryByActTemplateIds(chanDoanSection, CDAUtil.getTemplateId("actChanDoanVaoVien"));
	}
	
	public Entry getEntryChanDoanRaVien(Section chanDoanSection) {
		return CDAUtil.getEntryByActTemplateIds(chanDoanSection, CDAUtil.getTemplateId("actChanDoanRaVien"));
	}
	
	public Entry getEntryChanDoanTruocPhauThuat(Section chanDoanSection) {
		return CDAUtil.getEntryByActTemplateIds(chanDoanSection, CDAUtil.getTemplateId("actChanDoanTruocPhauThuat"));
	}
	
	public Entry getEntryChanDoanSauPhauThuat(Section chanDoanSection) {
		return CDAUtil.getEntryByActTemplateIds(chanDoanSection, CDAUtil.getTemplateId("actChanDoanSauPhauThuat"));
	}
	
	public static EntryRelationship getEntryRelationshipChinh(Act act) {
		return CDAUtil.getEntryRelationshipByObsTemplateIds(act, CDAUtil.getTemplateId("observationChanDoanChinh"));
	}
	
	public static List<EntryRelationship> getEntryRelationshipKemTheo(Act act) {
		return CDAUtil.getListEntryRelationshipByObsTemplateIds(act, CDAUtil.getTemplateId("observationChanDoanKemTheo"));
	}
	
	public static EntryRelationship getEntryRelationshipNguyenNhan(Act act) {
		return CDAUtil.getEntryRelationshipByObsTemplateIds(act, CDAUtil.getTemplateId("observationChanDoanNguyenNhan"));
	}
	
	private Act createActChanDoan(String element) {
		var act = DataUtil.createAct(CDAUtil.getTemplateId(element));
		act.setCode(CDAUtil.getCodeCD(element));
		return act;
	}
	
	public Act createActChanDoanNoiDen() {
		return createActChanDoan("actChanDoanNoiDen");
	}
	
	public Act createActChanDoanVaoVien() {
		return createActChanDoan("actChanDoanVaoVien");
	}
	
	public Act createActChanDoanRaVien() {
		return createActChanDoan("actChanDoanRaVien");
	}
	
	public Act createActChanDoanTruocPhauThuat() {
		return createActChanDoan("actChanDoanTruocPhauThuat");
	}
	
	public Act createActChanDoanSauPhauThuat() {
		return createActChanDoan("actChanDoanSauPhauThuat");
	}
	
	private Observation createObservation(String element) {
		var obs = DataUtil.createObs(CDAUtil.getTemplateId(element));
		obs.setCode(CDAUtil.getCodeCD(element));
		return obs;
	}
	
	public Observation createObservationChinh() {
		return createObservation("observationChanDoanChinh");		
	}
	
	public Observation createObservationKemTheo() {
		return createObservation("observationChanDoanKemTheo");
	}
	
	public Observation createObservationNguyenNhan() {
		return createObservation("observationChanDoanNguyenNhan");
	}	
}
