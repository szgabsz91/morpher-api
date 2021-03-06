package com.github.szgabsz91.morpher.api.controllers;

import com.github.szgabsz91.morpher.api.exceptions.LanguageNotSupportedException;
import com.github.szgabsz91.morpher.api.model.ErrorResponse;
import com.github.szgabsz91.morpher.api.services.IMorpherService;
import com.github.szgabsz91.morpher.core.model.AffixType;
import com.github.szgabsz91.morpher.core.model.Word;
import com.github.szgabsz91.morpher.engines.api.model.AnalysisInput;
import com.github.szgabsz91.morpher.engines.api.model.MorpherEngineResponse;
import com.github.szgabsz91.morpher.engines.api.model.ProbabilisticStep;
import com.github.szgabsz91.morpher.languagehandlers.api.model.ProbabilisticAffixType;
import com.github.szgabsz91.morpher.systems.api.model.Language;
import com.github.szgabsz91.morpher.systems.api.model.LanguageAwareAnalysisInput;
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
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AnalysisControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private IMorpherService morpherService;

    @Test
    public void testAnalyzeWithKnownLanguage() throws LanguageNotSupportedException {
        Language language = Language.of("hu");
        Word input = Word.of("input");
        Word output = Word.of("output");
        AnalysisInput analysisInput = AnalysisInput.of(input);
        LanguageAwareAnalysisInput languageAwareAnalysisInput = new LanguageAwareAnalysisInput(language, analysisInput);

        MorpherEngineResponse expectedMorpherEngineResponse = MorpherEngineResponse.analysisResponse(
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
        when(this.morpherService.analyze(languageAwareAnalysisInput))
                .thenReturn(Mono.just(expectedMorpherSystemResponse));

        @SuppressWarnings("unchecked")
        Map<String, Object> morpherSystemResponseMap = this.testRestTemplate
                .getForObject("/morpher/languages/{language}/analyze?input={input}", HashMap.class, language, input);
        MorpherSystemResponse morpherSystemResponse = ControllerTestUtils.toMorpherSystemResponse(morpherSystemResponseMap);

        assertThat(morpherSystemResponse).isEqualTo(expectedMorpherSystemResponse);
        verify(this.morpherService).analyze(languageAwareAnalysisInput);
    }

    @Test
    public void testAnalyzeWithUnknownLanguage() throws LanguageNotSupportedException {
        Language language = Language.of("en");
        AnalysisInput analysisInput = AnalysisInput.of(Word.of("input"));
        LanguageAwareAnalysisInput languageAwareAnalysisInput = new LanguageAwareAnalysisInput(language, analysisInput);

        when(this.morpherService.analyze(languageAwareAnalysisInput))
                .thenThrow(new LanguageNotSupportedException(language));

        ErrorResponse response = this.testRestTemplate
                .getForObject("/morpher/languages/{language}/analyze?input={input}", ErrorResponse.class, language, "input");

        assertThat(response.isError()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Language " + language + " is not supported");
        verify(this.morpherService).analyze(languageAwareAnalysisInput);
    }

}
