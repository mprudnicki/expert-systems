package wat.exercise1;

import wat.exercise1.model.Fact;
import wat.exercise1.model.Rule;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ForwardChainer {
    static List<Rule> execute(Map<String, Boolean> symptomList, List<Rule> ruleList) {
        return ruleList.stream().filter(rule -> rule.isRuleTrueForFacts(symptomList)).collect(Collectors.toList());
    }
}
