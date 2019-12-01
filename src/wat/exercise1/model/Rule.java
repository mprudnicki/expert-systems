package wat.exercise1.model;

import java.util.*;
import java.util.stream.Collectors;


public class Rule {

    private String outcome;
    private Double certainty;

    private Boolean isCalculated = false;
    private Boolean calculationValue = false;

    private List<Rule> parentRules;
    private Map<String, Boolean> conditions;

    public Rule() {
        parentRules = new LinkedList<>();
        conditions = new HashMap<>();
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public Double getCertainty() {
        return certainty;
    }

    public Double getAbsoluteCertainty() {
        return Math.abs(certainty);
    }

    public void setCertainty(Double certainty) {
        this.certainty = certainty;
    }

    public Map<String, Boolean> getConditions() {
        return conditions;
    }

    public void addCondition(String key, String value) {
        conditions.put(key, Boolean.valueOf(value));
    }

    public void addToParentRuleList(Rule rule) {
        parentRules.add(rule);
    }

    // first check if all available conditions from rule are matching facts
    public boolean isRuleTrueForFacts(Map<String, Boolean> symptomList) {
      List<String> filteredConditions = conditions.keySet().stream().filter(condition -> !parentRules.stream().anyMatch(rule -> rule.symptomEqualsAnyTreeOutcome(condition))).collect(Collectors.toList());
        if(isCalculated) return calculationValue;
        for(String condition : filteredConditions) {
            if(conditions.get(condition) != symptomList.get(condition)) return false;
        }
        calculationValue = parentRules.stream().allMatch(e -> e.isRuleTrueForFacts(symptomList));
        isCalculated = true;
        return calculationValue;
    }

    private double calculateCertainty() {
        if(parentRules.isEmpty()) return certainty;
        else if(parentRules.size() == 1) return parentRules.get(0).calculateCertainty() * certainty;
        else {
            double sum = certainty * parentRules.stream()
                    .mapToDouble(Rule::calculateCertainty)
                    .sum();
            double multiplication = parentRules.stream()
                    .mapToDouble(Rule::calculateCertainty)
                    .reduce(1, (a, b) -> a * b);
            if(parentRules.stream().filter(e -> e.getCertainty() >= 0).count() > 0) {
                return sum - multiplication;
            } else if(parentRules.stream().filter(e -> e.getCertainty() < 0).count() > 0) {
                return sum + multiplication;
            } else {
                return sum / (1 - parentRules.stream()
                        .sorted(Comparator.comparingDouble(Rule::getAbsoluteCertainty).reversed())
                        .findFirst()
                        .orElseGet(null)
                        .getCertainty());
            }
        }
    }

    private boolean symptomEqualsAnyTreeOutcome(String symptom) {
        return outcome.equals(symptom) || parentRules.stream().anyMatch(e -> e.symptomEqualsAnyTreeOutcome(symptom));
    }

    public void printRuleTree(int level) {
        for(int i = 0; i < level; i++)
            System.out.print("\t");
        System.out.println(this.toString());
        parentRules.forEach(rule -> rule.printRuleTree(level + 1));
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(").append(Math.round(calculateCertainty() * 100.0) / 100.0).append(")");
        stringBuilder.append("[").append(outcome).append("]");
        stringBuilder.append(" <- ");
        conditions.entrySet().stream().map(e -> "{" + String.valueOf(e.getKey()) + "=" + String.valueOf(e.getValue()) + "}").forEach(stringBuilder::append);
        return stringBuilder.toString();
    }
}
