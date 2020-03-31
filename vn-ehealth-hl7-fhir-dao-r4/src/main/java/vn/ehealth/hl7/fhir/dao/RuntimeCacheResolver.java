package vn.ehealth.hl7.fhir.dao;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.SimpleCacheResolver;

public class RuntimeCacheResolver extends SimpleCacheResolver {

    private String cachePool;
    
    protected RuntimeCacheResolver(CacheManager cacheManager, String cachePool) {
        super(cacheManager);
        this.cachePool = cachePool;
    }

    @Override
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        return Arrays.asList(cachePool + "." + context.getTarget().getClass().getSimpleName());
    }
}
