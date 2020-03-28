package vn.ehealth.hl7.fhir.clinical.entity;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceReactionComponent;
import org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceSeverity;
import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;

public class AllergyIntoleranceReactionEntity {
	public BaseCodeableConcept substance;
	public List<BaseCodeableConcept> manifestation;
	public String description;
	public Date onset;
	public String severity;
	public BaseCodeableConcept exposureRoute;
	public List<BaseAnnotation> note;

	public static AllergyIntoleranceReactionEntity from(AllergyIntoleranceReactionComponent obj) {
		if (obj == null)
			return null;

		var ent = new AllergyIntoleranceReactionEntity();
		ent.substance = obj.hasSubstance() ? BaseCodeableConcept.fromCodeableConcept(obj.getSubstance()) : null;
		ent.manifestation = obj.hasManifestation() ? BaseCodeableConcept.fromCodeableConcept(obj.getManifestation())
				: null;
		ent.description = obj.hasDescription() ? obj.getDescription() : null;
		ent.onset = obj.hasOnset() ? obj.getOnset() : null;
		ent.severity = obj.hasSeverity() ? obj.getSeverity().toCode() : null;
		ent.exposureRoute = obj.hasExposureRoute() ? BaseCodeableConcept.fromCodeableConcept(obj.getExposureRoute())
				: null;
		ent.note = obj.hasNote() ? BaseAnnotation.fromAnnotationList(obj.getNote()) : null;
		return ent;
	}

	public static List<AllergyIntoleranceReactionEntity> from(List<AllergyIntoleranceReactionComponent> lst) {
		return transform(lst, x -> from(x));
	}

	public static AllergyIntoleranceReactionComponent to(AllergyIntoleranceReactionEntity ent) {
		if (ent == null)
			return null;

		var obj = new AllergyIntoleranceReactionComponent();
		obj.setSubstance(BaseCodeableConcept.toCodeableConcept(ent.substance));
		obj.setManifestation(BaseCodeableConcept.toCodeableConcept(ent.manifestation));
		obj.setDescription(ent.description != null ? ent.description : null);
		if (ent.onset != null) {
			obj.setOnset(ent.onset);
		}
		obj.setSeverity(AllergyIntoleranceSeverity.fromCode(ent.severity));
		obj.setExposureRoute(BaseCodeableConcept.toCodeableConcept(ent.exposureRoute));
		if (ent.note != null && ent.note.size() > 0) {
			obj.setNote(BaseAnnotation.toAnnotationList(ent.note));
		}
		return obj;
	}

	public static List<AllergyIntoleranceReactionComponent> to(List<AllergyIntoleranceReactionEntity> lst) {
		return transform(lst, x -> to(x));
	}

}
