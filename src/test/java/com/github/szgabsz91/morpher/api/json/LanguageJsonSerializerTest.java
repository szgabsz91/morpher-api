package com.github.szgabsz91.morpher.api.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.github.szgabsz91.morpher.systems.api.model.Language;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LanguageJsonSerializerTest {

    private LanguageJsonSerializer serializer;

    @Mock
    private JsonGenerator jsonGenerator;

    @Before
    public void setUp() {
        this.serializer = new LanguageJsonSerializer();
    }

    @Test
    public void testSerialize() throws IOException {
        Language language = Language.of("lang");
        this.serializer.serialize(language, this.jsonGenerator, null);
        verify(this.jsonGenerator).writeString(language.toString());
    }

}
