package com.github.szgabsz91.morpher.api.controllers;

import com.github.szgabsz91.morpher.api.exceptions.LanguageNotSupportedException;
import com.github.szgabsz91.morpher.api.model.ErrorResponse;
import com.github.szgabsz91.morpher.core.model.AffixType;
import com.github.szgabsz91.morpher.analyzeragents.api.model.ProbabilisticAffixType;
import com.github.szgabsz91.morpher.api.services.IMorpherService;
import com.github.szgabsz91.morpher.core.model.Word;
import com.github.szgabsz91.morpher.engines.api.model.InflectionInput;
import com.github.szgabsz91.morpher.engines.api.model.InflectionOrderedInput;
import com.github.szgabsz91.morpher.engines.api.model.MorpherEngineResponse;
import com.github.szgabsz91.morpher.engines.api.model.ProbabilisticStep;
import com.github.szgabsz91.morpher.systems.api.model.Language;
import com.github.szgabsz91.morpher.systems.api.model.LanguageAwareInflectionInput;
import com.github.szgabsz91.morpher.systems.api.model.LanguageAwareInflectionOrderedInput;
import com.github.szgabsz91.morpher.systems.api.model.MorpherSystemResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InflectionControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private IMorpherService morpherService;

    @Test
    public void testInflectUsingSetWithKnownLanguage() throws LanguageNotSupportedException {
        Language language = Language.of("language");
        Word input = Word.of("input");
        Word output = Word.of("output");
        Set<AffixType> affixTypes = new HashSet<>(Set.of(AffixType.of("AFF1"), AffixType.of("AFF2")));
        String affixTypesString = affixTypes
                .stream()
                .map(AffixType::toString)
                .collect(joining(","));
        InflectionInput inflectionInput = new InflectionInput(input, affixTypes);
        LanguageAwareInflectionInput languageAwareInflectionInput = new LanguageAwareInflectionInput(language, inflectionInput);

        MorpherEngineResponse expectedMorpherEngineResponse = MorpherEngineResponse.inflectionResponse(
                input,
                output,
                ProbabilisticAffixType.of(AffixType.of("/NOUN"), 0.5),
                0.4,
                Collections.singletonList(
                        new ProbabilisticStep(
                                input,
                                output,
                                AffixType.of("<CAS<ACC>>"),
                                0.6,
                                0.7,
                                0.8
                        )
                )
        );
        MorpherSystemResponse expectedMorpherSystemResponse = new MorpherSystemResponse(language, List.of(expectedMorpherEngineResponse));
        when(this.morpherService.inflect(languageAwareInflectionInput))
                .thenReturn(Mono.just(expectedMorpherSystemResponse));

        @SuppressWarnings("uncheck")
        Map<String, Object> morpherSystemResponseMap = this.testRestTemplate
                .getForObject("/morpher/languages/{language}/inflect?input={input}&affix-types={affixTypes}", HashMap.class, language, input, affixTypesString);
        MorpherSystemResponse morpherSystemResponse = ControllerTestUtils.toMorpherSystemResponse(morpherSystemResponseMap);

        assertThat(morpherSystemResponse).isEqualTo(expectedMorpherSystemResponse);
        verify(this.morpherService).inflect(languageAwareInflectionInput);
    }

    @Test
    public void testInflectUsingSetWithUnknownLanguage() throws LanguageNotSupportedException {
        Language language = Language.of("language");
        Word input = Word.of("input");
        Set<AffixType> affixTypes = new HashSet<>(Set.of(AffixType.of("AFF1"), AffixType.of("AFF2")));
        String affixTypesString = affixTypes
                .stream()
                .map(AffixType::toString)
                .collect(joining(","));
        InflectionInput inflectionInput = new InflectionInput(input, affixTypes);
        LanguageAwareInflectionInput languageAwareInflectionInput = new LanguageAwareInflectionInput(language, inflectionInput);

        when(this.morpherService.inflect(languageAwareInflectionInput))
                .thenThrow(new LanguageNotSupportedException(language));

        ErrorResponse errorResponse = this.testRestTemplate
                .getForObject("/morpher/languages/{language}/inflect?input={input}&affix-types={affixTypes}", ErrorResponse.class, language, input, affixTypesString);

        assertThat(errorResponse.isError()).isTrue();
        assertThat(errorResponse.getMessage()).isEqualTo("Language " + language + " is not supported");
        verify(this.morpherService).inflect(languageAwareInflectionInput);
    }

    @Test
    public void testInflectUsingListWithKnownLanguage() throws LanguageNotSupportedException {
        Language language = Language.of("language");
        Word input = Word.of("input");
        Word output = Word.of("output");
        List<AffixType> affixTypes = List.of(AffixType.of("AFF1"), AffixType.of("AFF2"));
        String affixTypesString = affixTypes
                .stream()
                .map(AffixType::toString)
                .collect(joining(","));
        InflectionOrderedInput inflectionOrderedInput = new InflectionOrderedInput(input, affixTypes);
        LanguageAwareInflectionOrderedInput languageAwareInflectionOrderedInput = new LanguageAwareInflectionOrderedInput(language, inflectionOrderedInput);

        MorpherEngineResponse expectedMorpherEngineResponse = MorpherEngineResponse.inflectionResponse(
                input,
                output,
                ProbabilisticAffixType.of(AffixType.of("/NOUN"), 0.5),
                0.4,
                Collections.singletonList(
                        new ProbabilisticStep(
                                input,
                                output,
                                AffixType.of("<CAS<ACC>>"),
                                0.6,
                                0.7,
                                0.8
                        )
                )
        );
        MorpherSystemResponse expectedMorpherSystemResponse = new MorpherSystemResponse(language, List.of(expectedMorpherEngineResponse));
        when(this.morpherService.inflect(languageAwareInflectionOrderedInput))
                .thenReturn(Mono.just(expectedMorpherSystemResponse));

        @SuppressWarnings("unchecked")
        Map<String, Object> morpherSystemResponseMap = this.testRestTemplate
                .getForObject("/morpher/languages/{language}/inflect?ordered&input={input}&affix-types={affixTypes}", HashMap.class, language, input, affixTypesString);
        MorpherSystemResponse morpherSystemResponse = ControllerTestUtils.toMorpherSystemResponse(morpherSystemResponseMap);

        assertThat(morpherSystemResponse).isEqualTo(expectedMorpherSystemResponse);
        verify(this.morpherService).inflect(languageAwareInflectionOrderedInput);
    }

    @Test
    public void testInflectUsingListWithUnknownLanguage() throws LanguageNotSupportedException {
        Language language = Language.of("language");
        Word input = Word.of("input");
        List<AffixType> affixTypes = List.of(AffixType.of("AFF1"), AffixType.of("AFF2"));
        String affixTypesString = affixTypes
                .stream()
                .map(AffixType::toString)
                .collect(joining(","));
        InflectionOrderedInput inflectionOrderedInput = new InflectionOrderedInput(input, affixTypes);
        LanguageAwareInflectionOrderedInput languageAwareInflectionOrderedInput = new LanguageAwareInflectionOrderedInput(language, inflectionOrderedInput);

        when(this.morpherService.inflect(languageAwareInflectionOrderedInput))
                .thenThrow(new LanguageNotSupportedException(language));

        ErrorResponse errorResponse = this.testRestTemplate
                .getForObject("/morpher/languages/{language}/inflect?ordered&input={input}&affix-types={affixTypes}", ErrorResponse.class, language, input, affixTypesString);

        assertThat(errorResponse.isError()).isTrue();
        assertThat(errorResponse.getMessage()).isEqualTo("Language " + language + " is not supported");
        verify(this.morpherService).inflect(languageAwareInflectionOrderedInput);
    }

}
