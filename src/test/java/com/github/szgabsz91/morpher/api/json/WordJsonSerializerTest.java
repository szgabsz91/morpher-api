package com.github.szgabsz91.morpher.api.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.github.szgabsz91.morpher.core.model.Word;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WordJsonSerializerTest {

    private WordJsonSerializer serializer;

    @Mock
    private JsonGenerator jsonGenerator;

    @Before
    public void setUp() {
        this.serializer = new WordJsonSerializer();
    }

    @Test
    public void testSerialize() throws IOException {
        Word word = Word.of("word");
        this.serializer.serialize(word, this.jsonGenerator, null);
        verify(this.jsonGenerator).writeString(word.toString());
    }

}
