package test;

import java.util.ArrayList;
import java.util.List;

public class CDATemplate {

    public static class Rule {
        public static class CHECK {
            public static final String SIZE = "size";
            public static final String EQUAL = "equal";
        }
        public String check;
        public Double min;
        public Double max;
    }
    
    public static class FieldDefault {
        public String field;
        public String value;
        public String type;
    }
    
    public static class FieldValidation {
        public String field;
        public String value;
        public List<Rule> rules = new ArrayList<>();
        
    }
    
    public static class DataType {
        public String id;
        public String templateId;
        public String parent;
        
        public List<FieldDefault> fieldDefaults = new ArrayList<>();
        public List<FieldValidation> fieldValidations = new ArrayList<>();        
    }
    
    public List<DataType> dataTypes = new ArrayList<>();
}
