package org.globalbioticinteractions.nomer.util;

import org.apache.commons.lang.StringUtils;
import org.eol.globi.taxon.TaxonCacheService;
import org.eol.globi.taxon.TermMatcher;

public class TermMatcherCacheFactory implements TermMatcherFactory {

    private static final String DEPOT_PREFIX = "https://depot.globalbioticinteractions.org/snapshot/target/data/taxa/";
    private final static String TAXON_MAP_DEFAULT_URL = DEPOT_PREFIX +"taxonMap.tsv.gz";
    private final static String TAXON_CACHE_DEFAULT_URL = DEPOT_PREFIX + "taxonCache.tsv.gz";

    @Override
    public TermMatcher createTermMatcher(TermMatcherContext ctx) {
        String termMapUrl = ctx.getProperty("term.map.url");
        String termCacheUrl = ctx.getProperty("term.cache.url");

        TaxonCacheService cacheService = new TaxonCacheService(
                StringUtils.isBlank(termCacheUrl) ? TAXON_CACHE_DEFAULT_URL : termCacheUrl,
                StringUtils.isBlank(termMapUrl) ? TAXON_MAP_DEFAULT_URL : termMapUrl);
        cacheService.setTemporary(false);
        return cacheService;
    }
}
