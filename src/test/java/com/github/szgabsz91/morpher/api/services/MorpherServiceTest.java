package com.github.szgabsz91.morpher.api.services;

import com.github.szgabsz91.morpher.api.exceptions.LanguageNotSupportedException;
import com.github.szgabsz91.morpher.core.model.AffixType;
import com.github.szgabsz91.morpher.analyzeragents.api.model.ProbabilisticAffixType;
import com.github.szgabsz91.morpher.analyzeragents.hunmorph.impl.HunmorphAnnotationTokenizer;
import com.github.szgabsz91.morpher.core.model.Word;
import com.github.szgabsz91.morpher.engines.api.model.InflectionInput;
import com.github.szgabsz91.morpher.engines.api.model.InflectionOrderedInput;
import com.github.szgabsz91.morpher.engines.api.model.LemmatizationInput;
import com.github.szgabsz91.morpher.engines.api.model.MorpherEngineResponse;
import com.github.szgabsz91.morpher.engines.api.model.ProbabilisticStep;
import com.github.szgabsz91.morpher.systems.api.model.Language;
import com.github.szgabsz91.morpher.systems.api.model.LanguageAwareInflectionInput;
import com.github.szgabsz91.morpher.systems.api.model.LanguageAwareInflectionOrderedInput;
import com.github.szgabsz91.morpher.systems.api.model.LanguageAwareLemmatizationInput;
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
    public void testInflectWithAffixTypeSetAndKnownLanguage() throws LanguageNotSupportedException {
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
    public void testInflectWithAffixTypeSetAndUnknownLanguage() throws LanguageNotSupportedException {
        Language language = Language.of("en");
        exception.expect(LanguageNotSupportedException.class);
        exception.expectMessage("Language " + language + " is not supported");
        LanguageAwareInflectionInput languageAwareInflectionInput = new LanguageAwareInflectionInput(language, null);
        this.morpherService.inflect(languageAwareInflectionInput);
    }

    @Test
    public void testInflectWithAffixTypeListAndKnownLanguage() throws LanguageNotSupportedException {
        Language language = Language.of("hu");
        Word input = Word.of("alma");
        List<AffixType> affixTypes = List.of(AffixType.of("<CAS<ACC>>"));
        InflectionOrderedInput inflectionOrderedInput = new InflectionOrderedInput(input, affixTypes);
        LanguageAwareInflectionOrderedInput languageAwareInflectionOrderedInput = new LanguageAwareInflectionOrderedInput(language, inflectionOrderedInput);
        Mono<MorpherSystemResponse> morpherSystemResponseMono = this.morpherService.inflect(languageAwareInflectionOrderedInput);
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
    public void testInflectWithAffixTypeListAndUnknownLanguage() throws LanguageNotSupportedException {
        Language language = Language.of("en");
        exception.expect(LanguageNotSupportedException.class);
        exception.expectMessage("Language " + language + " is not supported");
        LanguageAwareInflectionOrderedInput languageAwareInflectionOrderedInput = new LanguageAwareInflectionOrderedInput(language, null);
        this.morpherService.inflect(languageAwareInflectionOrderedInput);
    }

    @Test
    public void testLemmatizeWithKnownLanguage() throws LanguageNotSupportedException {
        Language language = Language.of("hu");
        Word input = Word.of("almát");
        LemmatizationInput lemmatizationInput = LemmatizationInput.of(input);
        LanguageAwareLemmatizationInput languageAwareLemmatizationInput = new LanguageAwareLemmatizationInput(language, lemmatizationInput);
        Mono<MorpherSystemResponse> morpherSystemResponseMono = this.morpherService.lemmatize(languageAwareLemmatizationInput);
        MorpherSystemResponse morpherSystemResponse = morpherSystemResponseMono.block();
        MorpherEngineResponse expectedMorpherEngineResponse = MorpherEngineResponse.lemmatizationResponse(
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
    public void testLemmatizeWithUnknownLanguage() throws LanguageNotSupportedException {
        Language language = Language.of("en");
        exception.expect(LanguageNotSupportedException.class);
        exception.expectMessage("Language " + language + " is not supported");
        LanguageAwareLemmatizationInput languageAwareLemmatizationInput = new LanguageAwareLemmatizationInput(language, null);
        this.morpherService.lemmatize(languageAwareLemmatizationInput);
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
                .map(AffixType::of)
                .collect(toSet());
        assertThat(affixTypeSet).isEqualTo(expected);
    }

    @Test
    public void testGetSupportedAffixTypesWithUnknownLanguage() throws LanguageNotSupportedException {
        Language language = Language.of("en");
        exception.expect(LanguageNotSupportedException.class);
        exception.expectMessage("Language " + language + " is not supported");
        this.morpherService.getSupportedAffixTypes(language);
    }

}
