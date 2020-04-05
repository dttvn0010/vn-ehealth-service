package vn.ehealth.cda;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.w3c.dom.Node;

public class CDAUtils {

    private static String VM_PATH = "static/vm/";
    
    private static void trimWhitespace(Node node)
    {
        var attributes = node.getAttributes();
        
        var blankAttributes = new HashSet<String>();
        
        for(int i = 0; attributes != null && i < attributes.getLength(); i++) {
            var attr = attributes.item(i);
            if(StringUtils.isBlank(attr.getNodeValue())) {
                blankAttributes.add(attr.getNodeName());
            }
        }
        
        for(var attr : blankAttributes) {
            attributes.removeNamedItem(attr);
        }
        
        var children = node.getChildNodes();
        for(int i = 0; i < children.getLength(); ++i) {
            var child = children.item(i);
            if(child.getNodeType() == Node.TEXT_NODE) {
                child.setTextContent(child.getTextContent().trim());
            }
            trimWhitespace(child);
        }
    }
    
    public static String formatXml(String xmlStr) {
        try {
            xmlStr = xmlStr.replace("\t", "  ");
            var factory = DocumentBuilderFactory.newInstance();
            var builder = factory.newDocumentBuilder();
            var document = builder.parse(new ByteArrayInputStream(xmlStr.getBytes()));
            trimWhitespace(document);
    
            var tform = TransformerFactory.newInstance().newTransformer();
            tform.setOutputProperty(OutputKeys.INDENT, "yes");
            tform.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            var writer = new StringWriter();
            tform.transform(new DOMSource(document), new StreamResult(writer));
            return writer.toString();
        }catch(Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    private static String getTemplateString(String templatePath, Map<String, Object> _data) {
        var engine = new VelocityEngine();
        engine.setProperty("directive.set.null.allowed", true);
        engine.setProperty("file.resource.loader.path", "");
        
        var resourcePath = CDAUtils.class.getClassLoader().getResource(VM_PATH + templatePath);
        var template = engine.getTemplate(resourcePath.getPath(), "UTF-8" );
        var data = new HashMap<>(_data);
        var writer = new StringWriter();
        template.merge(new VelocityContext(data), writer );
        return writer.toString();
    }
    
    public static String getClinicalDocument(Map<String, Object> _data) {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty("directive.set.null.allowed", true);
        engine.setProperty("file.resource.loader.path", "");
        
        var data = new HashMap<>(_data);
        data.put("recordTarget", getTemplateString("header/record_target.vm.xml", data));
        data.put("author", getTemplateString("header/author.vm.xml", data));
        data.put("custodian", getTemplateString("header/custodian.vm.xml", data));
        data.put("legalAuthenticator", getTemplateString("header/legal_authenticator.vm.xml", data));
        data.put("authenticator", getTemplateString("header/authenticator.vm.xml", data));
        data.put("participant", getTemplateString("header/participant.vm.xml", data));
        data.put("componentOf", getTemplateString("header/component_of.vm.xml", data));
        
        data.put("quanLyNguoiBenhComp", getTemplateString("components/qlnb.vm.xml", data));
        data.put("chanDoanComp", getTemplateString("components/chandoan.vm.xml", data));
        data.put("xetNghiemComp", getTemplateString("components/xetnghiem.vm.xml", data));
        data.put("phauThuatThuThuatComp", getTemplateString("components/pttt.vm.xml", data));
        data.put("donThuocComp", getTemplateString("components/donthuoc.vm.xml", data));
        
        var resourcePath = CDAUtils.class.getClassLoader().getResource(VM_PATH + "document.vm.xml");
        var template = engine.getTemplate(resourcePath.getPath(), "UTF-8" );
        var writer = new StringWriter();

        template.merge(new VelocityContext(data), writer );
        return formatXml(writer.toString());
    }
}
