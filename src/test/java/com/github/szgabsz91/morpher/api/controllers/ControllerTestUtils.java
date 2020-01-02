package com.github.szgabsz91.morpher.api.controllers;

import com.github.szgabsz91.morpher.core.model.AffixType;
import com.github.szgabsz91.morpher.core.model.Word;
import com.github.szgabsz91.morpher.engines.api.model.Mode;
import com.github.szgabsz91.morpher.engines.api.model.MorpherEngineResponse;
import com.github.szgabsz91.morpher.engines.api.model.ProbabilisticStep;
import com.github.szgabsz91.morpher.languagehandlers.api.model.ProbabilisticAffixType;
import com.github.szgabsz91.morpher.systems.api.model.Language;
import com.github.szgabsz91.morpher.systems.api.model.MorpherSystemResponse;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

final class ControllerTestUtils {

    private ControllerTestUtils() {

    }

    static MorpherSystemResponse toMorpherSystemResponse(Map<String, Object> map) {
        Language language = Language.of((String) map.get("language"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> morpherEngineResponseArray = (List<Map<String, Object>>) map.get("morpherEngineResponses");
        List<MorpherEngineResponse> morpherEngineResponses = morpherEngineResponseArray
                .stream()
                .map(ControllerTestUtils::toMorpherEngineResponse)
                .collect(toList());
        return new MorpherSystemResponse(language, morpherEngineResponses);
    }

    private static MorpherEngineResponse toMorpherEngineResponse(Map<String, Object> map) {
        String mode = (String) map.get("mode");

        Word input = Word.of((String) map.get("input"));
        Word output = Word.of((String) map.get("output"));

        @SuppressWarnings("unchecked")
        Map<String, Object> posMap = (Map<String, Object>) map.get("pos");
        ProbabilisticAffixType pos = ProbabilisticAffixType.of(
                AffixType.of((String) posMap.get("affixType")),
                (double) posMap.get("probability")
        );
        double probability = (double) map.get("affixTypeChainProbability");
        double aggregatedWeight = (double) map.get("aggregatedWeight");
        @SuppressWarnings("unchecked")
        List<ProbabilisticStep> steps = ((List<Map<String, Object>>) map.get("steps"))
                .stream()
                .map(ControllerTestUtils::toStep)
                .collect(toList());

        if (Mode.ANALYSIS.toString().equals(mode)) {
            MorpherEngineResponse morpherEngineResponse = MorpherEngineResponse.analysisResponse(input, output, pos, probability, steps);
            morpherEngineResponse.setAggregatedWeight(aggregatedWeight);
            return morpherEngineResponse;
        }

        MorpherEngineResponse morpherEngineResponse = MorpherEngineResponse.inflectionResponse(input, output, pos, probability, steps);
        morpherEngineResponse.setAggregatedWeight(aggregatedWeight);
        return morpherEngineResponse;
    }

    private static ProbabilisticStep toStep(Map<String, Object> map) {
        Word input = Word.of((String) map.get("input"));
        Word output = Word.of((String) map.get("output"));
        AffixType affixType = AffixType.of((String) map.get("affixType"));
        double affixTypeProbability = (double) map.get("affixTypeProbability");
        double outputWordProbability = (double) map.get("outputWordProbability");
        double aggregatedProbability = (double) map.get("aggregatedProbability");
        return new ProbabilisticStep(input, output, affixType, affixTypeProbability, outputWordProbability, aggregatedProbability);
    }

}
