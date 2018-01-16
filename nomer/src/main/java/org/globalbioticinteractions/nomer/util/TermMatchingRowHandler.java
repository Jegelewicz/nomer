package org.globalbioticinteractions.nomer.util;

import org.apache.commons.lang3.tuple.Pair;
import org.eol.globi.domain.NameType;
import org.eol.globi.domain.Taxon;
import org.eol.globi.domain.TaxonImpl;
import org.eol.globi.service.PropertyEnricherException;
import org.eol.globi.service.TaxonUtil;
import org.eol.globi.taxon.RowHandler;
import org.eol.globi.taxon.TermMatchListener;
import org.eol.globi.taxon.TermMatcher;
import org.eol.globi.util.CSVTSVUtil;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TermMatchingRowHandler implements RowHandler {
    private final PrintStream p;
    private final TermMatcherContext ctx;
    private TermMatcher termMatcher;

    public TermMatchingRowHandler(OutputStream os, TermMatcher termMatcher, TermMatcherContext ctx) {
        this.ctx = ctx;
        this.p = new PrintStream(os);
        this.termMatcher = termMatcher;
    }

    static Taxon asTaxon(String[] row, Map<Integer, String> schema) {
        Map<String, String> taxonMap = new TreeMap<>();
        for (Map.Entry<Integer, String> indexType : schema.entrySet()) {
            Integer key = indexType.getKey();
            if (row.length > key) {
                taxonMap.put(indexType.getValue(), row[key]);
            }
        }
        return TaxonUtil.mapToTaxon(taxonMap);
    }

    static void linesForTaxa(String[] row, Stream<Taxon> resolvedTaxa, PrintStream p, NameTypeOf nameTypeOf) {
        Stream<String> provided = Stream.of(row);

        Stream<Stream<String>> lines = resolvedTaxa.map(taxon -> Stream.of(
                nameTypeOf.nameTypeOf(taxon).name(),
                taxon.getExternalId(), taxon.getName(),
                taxon.getRank(),
                taxon.getCommonNames(),
                taxon.getPath(),
                taxon.getPathIds(),
                taxon.getPathNames(),
                taxon.getExternalUrl(),
                taxon.getThumbnailUrl(),
                taxon.getNameSource(),
                taxon.getNameSourceURL(),
                taxon.getNameSourceAccessedAt()))
                .map(resolved -> Stream.concat(provided, resolved));

        lines.map(combinedLine -> CSVTSVUtil.mapEscapedValues(combinedLine)
                .collect(Collectors.joining("\t")))
                .forEach(p::println);
    }



        @Override
        public void onRow(final String[] row) throws PropertyEnricherException {
            Taxon taxonProvided = asTaxon(row, ctx.getInputSchema());
            termMatcher.findTerms(Arrays.asList(taxonProvided), new TermMatchListener() {
                @Override
                public void foundTaxonForName(Long id, String name, Taxon taxon, NameType nameType) {
                    Taxon taxonWithServiceInfo = (TaxonUtil.mapToTaxon(TaxonUtil.appendNameSourceInfo(TaxonUtil.taxonToMap(taxon), termMatcher.getClass(), new Date())));
                    linesForTaxa(row, Stream.of(taxonWithServiceInfo), p, taxon1 -> nameType);
                }
            });
        }
    }
