package com.github.szgabsz91.morpher.api.controllers;

import com.github.szgabsz91.morpher.api.exceptions.LanguageNotSupportedException;
import com.github.szgabsz91.morpher.api.model.ErrorResponse;
import com.github.szgabsz91.morpher.api.services.IMorpherService;
import com.github.szgabsz91.morpher.core.model.AffixType;
import com.github.szgabsz91.morpher.systems.api.model.Language;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MorpherControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private IMorpherService morpherService;

    @Test
    public void testGetSupportedLanguages() {
        List<Language> expected = List.of("hu")
                .stream()
                .map(Language::of)
                .collect(toList());
        when(this.morpherService.getSupportedLanguages())
                .thenReturn(Flux.fromIterable(expected));

        Language[] languages = this.testRestTemplate
                .getForObject("/morpher/languages", Language[].class);

        assertThat(languages).containsExactlyElementsOf(expected);
        verify(this.morpherService).getSupportedLanguages();
    }

    @Test
    public void testGetSupportedAffixTypesWithKnownLanguage() throws LanguageNotSupportedException {
        Language language = Language.of("language");
        List<AffixType> expected = List.of(AffixType.of("AFF1"), AffixType.of("AFF2"));
        when(this.morpherService.getSupportedAffixTypes(language))
                .thenReturn(Flux.fromIterable(expected));

        AffixType[] affixTypes = this.testRestTemplate
                .getForObject("/morpher/languages/" + language + "/affix-types", AffixType[].class);

        assertThat(affixTypes).containsExactlyElementsOf(expected);
        verify(this.morpherService).getSupportedAffixTypes(language);
    }

    @Test
    public void testGetSupportedAffixTypesWithUnknownLanguage() throws LanguageNotSupportedException {
        Language language = Language.of("language");
        when(this.morpherService.getSupportedAffixTypes(language))
                .thenThrow(new LanguageNotSupportedException(language));

        ErrorResponse response = this.testRestTemplate
                .getForObject("/morpher/languages/" + language + "/affix-types", ErrorResponse.class);

        assertThat(response.isError()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Language " + language + " is not supported");
        verify(this.morpherService).getSupportedAffixTypes(language);
    }

}
