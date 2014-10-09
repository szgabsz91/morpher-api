package com.github.szgabsz91.morpher.api.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.github.szgabsz91.morpher.core.model.AffixType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AffixTypeJsonSerializerTest {

    private AffixTypeJsonSerializer serializer;

    @Mock
    private JsonGenerator jsonGenerator;

    @Before
    public void setUp() {
        this.serializer = new AffixTypeJsonSerializer();
    }

    @Test
    public void testSerialize() throws IOException {
        AffixType affixType = AffixType.of("token");
        this.serializer.serialize(affixType, this.jsonGenerator, null);
        verify(this.jsonGenerator).writeString(affixType.toString());
    }

}
