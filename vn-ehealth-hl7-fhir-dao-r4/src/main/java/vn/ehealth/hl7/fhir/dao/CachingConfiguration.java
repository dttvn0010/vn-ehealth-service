package vn.ehealth.hl7.fhir.dao;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CachingConfiguration extends CachingConfigurerSupport {

    final public static String CACHE_RESOLVER_NAME = "simpleCacheResolver";

    @Bean
    @Override
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

    @Bean(CACHE_RESOLVER_NAME)
    public CacheResolver cacheResolver(CacheManager cacheManager) {
        return new RuntimeCacheResolver(cacheManager);
    }
}
