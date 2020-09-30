package org.globalbioticinteractions.nomer.match;

import org.eol.globi.domain.NameType;
import org.eol.globi.domain.TaxonImpl;
import org.eol.globi.domain.Term;
import org.eol.globi.service.PropertyEnricherException;
import org.eol.globi.service.TaxonUtil;
import org.eol.globi.taxon.TermMatchListener;
import org.eol.globi.taxon.TermMatcher;
import org.globalbioticinteractions.nomer.util.TermMatcherContext;

import java.util.List;
import java.util.Map;

public class TermMatcherPlaziFactory implements TermMatcherFactory {

    @Override
    public String getName() {
        return "plazi";
    }

    @Override
    public String getDescription() {
        return "uses plazi.org to links names to their treatments";
    }

    @Override
    public TermMatcher createTermMatcher(TermMatcherContext ctx) {
        final PlaziService plaziService = new PlaziService(ctx);

        return new TermMatcher() {
            @Override
            public void match(List<Term> terms, TermMatchListener termMatchListener) throws PropertyEnricherException {
                for (Term term : terms) {
                    List<Map<String, String>> linkedTreatments = plaziService
                            .enrichAllMatches(TaxonUtil.taxonToMap(new TaxonImpl(term.getName(), term.getId())));

                    for (Map<String, String> linkedTreatment : linkedTreatments) {
                        termMatchListener.foundTaxonForTerm(
                                null,
                                term,
                                TaxonUtil.mapToTaxon(linkedTreatment),
                                NameType.SAME_AS);

                    }
                }
            }
        };
    }
}
