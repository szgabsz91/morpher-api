package com.github.szgabsz91.morpher.api.services;

import com.github.szgabsz91.morpher.api.exceptions.LanguageNotSupportedException;
import com.github.szgabsz91.morpher.core.model.AffixType;
import com.github.szgabsz91.morpher.core.model.Word;
import com.github.szgabsz91.morpher.engines.api.model.AnalysisInput;
import com.github.szgabsz91.morpher.engines.api.model.InflectionInput;
import com.github.szgabsz91.morpher.engines.api.model.MorpherEngineResponse;
import com.github.szgabsz91.morpher.engines.api.model.ProbabilisticStep;
import com.github.szgabsz91.morpher.languagehandlers.api.model.ProbabilisticAffixType;
import com.github.szgabsz91.morpher.languagehandlers.hunmorph.impl.HunmorphAnnotationTokenizer;
import com.github.szgabsz91.morpher.systems.api.model.Language;
import com.github.szgabsz91.morpher.systems.api.model.LanguageAwareAnalysisInput;
import com.github.szgabsz91.morpher.systems.api.model.LanguageAwareInflectionInput;
import com.github.szgabsz91.morpher.systems.api.model.MorpherSystemResponse;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MorpherServiceTest {

    @Autowired
    private MorpherService morpherService;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @After
    public void tearDown() {
        this.morpherService.destroy();
    }

    @Test
    public void testInflectWithKnownLanguage() throws LanguageNotSupportedException {
        Language language = Language.of("hu");
        Word input = Word.of("alma");
        Set<AffixType> affixTypes = Set.of(AffixType.of("<CAS<ACC>>"));
        InflectionInput inflectionInput = new InflectionInput(input, affixTypes);
        LanguageAwareInflectionInput languageAwareInflectionInput = new LanguageAwareInflectionInput(language, inflectionInput);
        Mono<MorpherSystemResponse> morpherSystemResponseMono = this.morpherService.inflect(languageAwareInflectionInput);
        MorpherSystemResponse morpherSystemResponse = morpherSystemResponseMono.block();
        MorpherEngineResponse expectedMorpherEngineResponse = MorpherEngineResponse.inflectionResponse(
                input,
                Word.of("almát"),
                ProbabilisticAffixType.of(AffixType.of("/NOUN"), 1.0),
                0.5,
                List.of(
                        new ProbabilisticStep(input, Word.of("almát"), AffixType.of("<CAS<ACC>>"), 0.5, 1.0, 0.5)
                )
        );
        MorpherSystemResponse expectedMorpherSystemResponse = new MorpherSystemResponse(language, List.of(expectedMorpherEngineResponse));
        assertThat(morpherSystemResponse).isEqualTo(expectedMorpherSystemResponse);
    }

    @Test
    public void testInflectWithUnknownLanguage() throws LanguageNotSupportedException {
        Language language = Language.of("en");
        exception.expect(LanguageNotSupportedException.class);
        exception.expectMessage("Language " + language + " is not supported");
        LanguageAwareInflectionInput languageAwareInflectionInput = new LanguageAwareInflectionInput(language, null);
        this.morpherService.inflect(languageAwareInflectionInput);
    }

    @Test
    public void testAnalyzeWithKnownLanguage() throws LanguageNotSupportedException {
        Language language = Language.of("hu");
        Word input = Word.of("almát");
        AnalysisInput analysisInput = AnalysisInput.of(input);
        LanguageAwareAnalysisInput languageAwareAnalysisInput = new LanguageAwareAnalysisInput(language, analysisInput);
        Mono<MorpherSystemResponse> morpherSystemResponseMono = this.morpherService.analyze(languageAwareAnalysisInput);
        MorpherSystemResponse morpherSystemResponse = morpherSystemResponseMono.block();
        MorpherEngineResponse expectedMorpherEngineResponse = MorpherEngineResponse.analysisResponse(
                input,
                Word.of("alma"),
                ProbabilisticAffixType.of(AffixType.of("/NOUN"), 0.5),
                0.5,
                List.of(
                        new ProbabilisticStep(input, Word.of("alma"), AffixType.of("<CAS<ACC>>"), 1.0, 1.0, 1.0)
                )
        );
        MorpherSystemResponse expectedMorpherSystemResponse = new MorpherSystemResponse(language, List.of(expectedMorpherEngineResponse));
        assertThat(morpherSystemResponse).isEqualTo(expectedMorpherSystemResponse);
    }

    @Test
    public void testAnalyzeWithUnknownLanguage() throws LanguageNotSupportedException {
        Language language = Language.of("en");
        exception.expect(LanguageNotSupportedException.class);
        exception.expectMessage("Language " + language + " is not supported");
        LanguageAwareAnalysisInput languageAwareAnalysisInput = new LanguageAwareAnalysisInput(language, null);
        this.morpherService.analyze(languageAwareAnalysisInput);
    }

    @Test
    public void testGetSupportedLanguages() {
        Flux<Language> supportedLanguageFlux = this.morpherService.getSupportedLanguages();
        Mono<List<Language>> supportedLanguagesMono = supportedLanguageFlux.collect(toList());
        List<Language> supportedLanguages = supportedLanguagesMono.block();
        assertThat(supportedLanguages).containsExactly(Language.of("hu"));
    }

    @Test
    public void testGetSupportedAffixTypesWithKnownLanguage() throws LanguageNotSupportedException {
        Language language = Language.of("hu");
        Flux<AffixType> affixTypeFlux = this.morpherService.getSupportedAffixTypes(language);
        Mono<List<AffixType>> affixTypesMono = affixTypeFlux.collect(toList());
        List<AffixType> affixTypes = affixTypesMono.block();
        Set<AffixType> affixTypeSet = new HashSet<>(affixTypes);
        Set<AffixType> expected = HunmorphAnnotationTokenizer.KNOWN_TOKENS
                .stream()
                .filter(affixType -> !affixType.startsWith("/"))
                .map(AffixType::of)
                .collect(toSet());
        assertThat(affixTypeSet).isEqualTo(expected);
        assertThat(affixTypeSet).noneMatch(affixType -> affixType.toString().startsWith("/"));
    }

    @Test
    public void testGetSupportedAffixTypesWithUnknownLanguage() throws LanguageNotSupportedException {
        Language language = Language.of("en");
        exception.expect(LanguageNotSupportedException.class);
        exception.expectMessage("Language " + language + " is not supported");
        this.morpherService.getSupportedAffixTypes(language);
    }

}
