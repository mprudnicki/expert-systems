package wat.exercise1.model;

import java.util.HashMap;
import java.util.Map;

public class Fact {
    public Map<String, Boolean> symptoms;

    public Fact() {
        symptoms = new HashMap<>();
    }

    public void addSymptom(String symptom, Boolean applicable) {
        symptoms.put(symptom, applicable);
    }

    public boolean hasSymptom(String symptom) {
        return symptoms.containsKey(symptom);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("F[");
        symptoms.entrySet().stream().map(e -> "{" + String.valueOf(e.getKey()) + "=" + String.valueOf(e.getValue()) + "}").forEach(stringBuilder::append);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
