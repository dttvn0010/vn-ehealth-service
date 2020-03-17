package vn.ehealth.hl7.fhir.core.util;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FPUtil {

    public static <T,U> List<U> transform(List<T> lst, Function<T, U> func) {
        if(lst != null) {
            return lst.stream().map(x -> func.apply(x))
                    .collect(Collectors.toList());
        }
        return null;
    }
    
    public static <T> List<T> filter(List<T> lst, Predicate<T> pred) {
        if(lst != null) {
            return lst.stream().filter(pred).collect(Collectors.toList());
        }
        return null;
    }
    
    public static <T> boolean anyMatch(List<T> lst, Predicate<T> pred) {
        if(lst != null) {
            return lst.stream().anyMatch(pred);
        }
        return false;
    }
    
    public static <T> boolean allMatch(List<T> lst, Predicate<T> pred) {
        if(lst != null) {
            return lst.stream().allMatch(pred);
        }
        return false;
    }
    
    public static <T> T findFirst(List<T> lst, Predicate<T> pred) {
        if(lst != null) {
            return lst.stream().filter(pred).findFirst().orElse(null);
        }
        return null;
    }
}
