package wat.exercise1;

import wat.exercise1.model.Fact;
import wat.exercise1.model.Rule;
import wat.exercise1.parser.XmlParser;

import java.util.*;
import java.util.stream.Collectors;

public class ExpertSystem {
    public static void main(String[] args) {
        XmlParser xmlRulesParser = new XmlParser();
        XmlParser xmlFactsParser = new XmlParser();
        if(!xmlRulesParser.loadDocument("E:\\WAT\\Magisterskie\\Semestr_2\\Niezawodność Oprogramowania\\Projekt-Rudnicki\\se-rudnicki\\src\\wat\\exercise1\\base\\knowledge-base.xml") ||
                !xmlFactsParser.loadDocument("E:\\WAT\\Magisterskie\\Semestr_2\\Niezawodność Oprogramowania\\Projekt-Rudnicki\\se-rudnicki\\src\\wat\\exercise1\\base\\fact-base.xml")) {
            throw new IllegalArgumentException();
        }

        final List<Rule> rules = xmlRulesParser.parseRulesDocument();
        final List<Fact> facts = addAbsentSymptoms(
                                    xmlFactsParser.parseFactsDocument(),
                                    gatherAllPossibleSymptoms(rules)
        );
        final Map<String, Boolean> symptoms = getAllSymptomsFromFacts(facts);
        final List possibleOutcomes = ForwardChainer.execute(symptoms, rules);
        final Comparator<Rule> compareByCertainty = Comparator.comparing(Rule::getCertainty);
        final Rule mostCertainOutcome = (Rule) possibleOutcomes.stream().sorted(compareByCertainty.reversed()).findFirst().orElse(null);
        if(mostCertainOutcome == null) System.out.println("No solution found for that symptoms!");
        else {
            System.out.println("SYMPTOMS " + symptoms);
            System.out.println("THE MOST CERTAIN OUTCOME IS...\t " + mostCertainOutcome.getOutcome().toUpperCase());
            System.out.println("HERE IS HOW THE SYSTEM DECIDED:");
            mostCertainOutcome.printRuleTree(0);
        }
    }

    private static Map<String, Boolean> getAllSymptomsFromFacts(List<Fact> facts) {
        Map<String, Boolean> symptomList = new HashMap<>();
        for(Fact fact : facts) {
            symptomList.putAll(fact.symptoms);
        }
        return symptomList;
    }

    private static List<String> gatherAllPossibleSymptoms(List<Rule> ruleList) {
        return ruleList.stream().map(Rule::getConditions)
                .flatMap(conditions -> conditions.keySet().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    private static List<Fact> addAbsentSymptoms(List<Fact> factList, List<String> symptomList) {
        for(int i = 0; i < factList.size(); i++) {
            Fact fact = factList.get(i);
            symptomList.stream().forEach(symptom -> {
                if(!fact.hasSymptom(symptom)) fact.addSymptom(symptom, false);
            });
        }

        return factList;
    }
}
