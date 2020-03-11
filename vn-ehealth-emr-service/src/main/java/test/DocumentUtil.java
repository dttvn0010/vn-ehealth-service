package test;

import org.openhealthtools.mdht.uml.cda.ClinicalDocument;

public class DocumentUtil {

	public static ClinicalDocument createDocument() {
		var document = DataUtil.createDocument(CDAUtil.getTemplateId("document"));
		var typeIdRoot = CDAUtil.getValue("document.typeId.root");
		var typeIdExtension = CDAUtil.getValue("document.typeId.extension");
		document.setTypeId(DataUtil.createTypeId(typeIdRoot, typeIdExtension));
		return document;
	}
}
