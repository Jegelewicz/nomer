package org.eol.globi.taxon;

import org.apache.commons.io.IOUtils;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertThat;

public class ManualSuggesterTest {

    @Test(expected = IllegalStateException.class)
    public void throwOnUninitialized() {
        new ManualSuggester().suggest("bla");
    }

    @Test
    public void match() throws IOException {
        String suggested = new ManualSuggester() {{
            init(IOUtils.toInputStream("provided,corrected\nHolo grapiens,Homo sapiens"));
        }}.suggest("Holo grapiens");
        assertThat(suggested, Is.is("Homo sapiens"));
    }

    @Test(expected = RuntimeException.class)
    public void throwOnDuplicateMapping() throws IOException {
        String suggested = new ManualSuggester() {{
            init(IOUtils.toInputStream("provided,corrected\nHolo grapiens,Homo sapiens\nprovided,corrected2\n"));
        }}.suggest("Holo grapiens");
        assertThat(suggested, Is.is("Homo sapiens"));
    }
}
