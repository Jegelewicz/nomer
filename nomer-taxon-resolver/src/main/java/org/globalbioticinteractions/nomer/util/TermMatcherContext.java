package org.globalbioticinteractions.nomer.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public interface TermMatcherContext extends PropertyContext {

    String getCacheDir();

    InputStream getResource(String uri) throws IOException;

    List<String> getMatchers();

    Map<Integer, String> getInputSchema();

    Map<Integer, String> getOutputSchema();

}
