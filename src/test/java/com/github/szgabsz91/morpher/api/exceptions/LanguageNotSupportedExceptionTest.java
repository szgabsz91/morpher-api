package com.github.szgabsz91.morpher.api.exceptions;

import com.github.szgabsz91.morpher.systems.api.model.Language;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LanguageNotSupportedExceptionTest {

    private Language language;
    private LanguageNotSupportedException exception;

    @Before
    public void setUp() {
        this.language = Language.of("en");
        this.exception = new LanguageNotSupportedException(language);
    }

    @Test
    public void testGetLanguage() {
        assertThat(this.exception.getLanguage()).isEqualTo(this.language);
    }

    @Test
    public void testMessage() {
        assertThat(this.exception.getMessage()).isEqualTo("Language " + this.language + " is not supported");
    }

}
