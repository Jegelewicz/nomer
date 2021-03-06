package org.globalbioticinteractions.nomer.util;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eol.globi.domain.Taxon;
import org.eol.globi.taxon.TaxonCacheParser;
import org.eol.globi.taxon.TaxonMapParser;
import org.eol.globi.util.CSVTSVUtil;
import org.eol.globi.util.ExternalIdUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class TermValidatorPredicates {

    public static final Predicate<String> SUPPORTED_ID = ExternalIdUtil::isSupported;

    public static final Predicate<String> PREFIXED_ID = line -> line.split(":")[0].matches("\\S+");

    public static final Predicate<String> VALID_NUMBER_OF_TERM_COLUMNS = line -> CSVTSVUtil.splitTSV(line).length == 9;

    public static final Predicate<String> VALID_NUMBER_OF_MAP_COLUMNS = line -> CSVTSVUtil.splitTSV(line).length == 4;

    public static final Predicate<String> SUPPORTED_RESOLVED_ID = line -> {
        Taxon taxon = TaxonMapParser.parseResolvedTaxon(CSVTSVUtil.splitTSV(line));
        return ExternalIdUtil.isSupported(taxon.getExternalId());
    };

    public static final Predicate<String> PREFIXED_RESOLVED_ID = line -> {
        Taxon taxon = TaxonMapParser.parseResolvedTaxon(CSVTSVUtil.splitTSV(line));
        return PREFIXED_ID.test(taxon.getExternalId());
    };

    public static final Predicate<String> CONSISTENT_PATH = line -> {
        Taxon taxon = TaxonCacheParser.parseLine(line);
        int paths = taxon == null ? 0 : getLength(taxon.getPath());
        int ids = taxon == null ? 0 : getLength(taxon.getPathIds());
        int ranks = taxon == null ? 0 : getLength(taxon.getPathNames());
        IntStream distinct = IntStream.of(paths, ids, ranks).filter(i -> i > 0).distinct();
        return distinct.count() <= 1;
    };

    public static final Predicate<String> PATH_EXISTS = line -> {
        Taxon taxon = TaxonCacheParser.parseLine(line);
        return taxon != null && (getLength(taxon.getPath()) > 0 || getLength(taxon.getPathIds()) > 0);
    };

    public static final Predicate<String> SUPPORTED_PATH_IDS = line -> validatePathIds(line, SUPPORTED_ID);

    public static final Predicate<String> PREFIXED_PATH_IDS = line -> validatePathIds(line, PREFIXED_ID);

    private static boolean validatePathIds(String line, Predicate<String> supportedId) {
        Taxon taxon = TaxonCacheParser.parseLine(line);
        String ids = taxon == null ? null : taxon.getPathIds();
        return StringUtils.isBlank(ids)
                || Stream.of(CSVTSVUtil.splitPipes(ids))
                .map(StringUtils::trim)
                .filter(StringUtils::isNotBlank)
                .allMatch(supportedId);
    }

    public static List<Pair<Predicate<String>, String>> TERM_VALIDATION_PREDICATES = Arrays.asList(
            Pair.of(Objects::nonNull, "non empty"),
            Pair.of(VALID_NUMBER_OF_TERM_COLUMNS, "9 columns"),
            Pair.of(SUPPORTED_ID, "supported id"),
            Pair.of(PREFIXED_ID, "prefixed id"),
            Pair.of(CONSISTENT_PATH, "consistent term path"),
            Pair.of(PATH_EXISTS, "non empty term path or path ids"),
            Pair.of(SUPPORTED_PATH_IDS, "supported path ids"),
            Pair.of(PREFIXED_PATH_IDS, "prefixed path ids")
    );

    public static List<Pair<Predicate<String>, String>> allFor(List<Pair<Predicate<String>, String>> individual) {
        List<Pair<Predicate<String>, String>> aggregate = Collections.singletonList(Pair.of(all(individual), "all validations pass"));
        return ListUtils.union(individual, aggregate);
    }

    public static List<Pair<Predicate<String>, String>> LINK_VALIDATION_PREDICATES = Arrays.asList(
            Pair.of(Objects::nonNull, "non empty"),
            Pair.of(VALID_NUMBER_OF_MAP_COLUMNS, "4 columns"),
            Pair.of(SUPPORTED_RESOLVED_ID, "supported resolved id"),
            Pair.of(PREFIXED_RESOLVED_ID, "prefixed resolved id")
    );

    private static int getLength(String path) {
        int pathLength = 0;
        if (StringUtils.isNotBlank(path)) {
            pathLength = CSVTSVUtil.splitPipes(path).length;
        }
        return pathLength;
    }


    public static Predicate<String> all(List<Pair<Predicate<String>, String>> mapPredicates) {
        return mapPredicates
                .stream()
                .map(Pair::getLeft)
                .reduce(Predicate::and)
                .orElse(Objects::nonNull);
    }
}
