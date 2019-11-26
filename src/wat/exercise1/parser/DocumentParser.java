package wat.exercise1.parser;

import wat.exercise1.model.Fact;

import java.util.List;

public interface DocumentParser<T, K> {
    boolean loadDocument(String path);

    List<Fact> parseFactsDocument();

    List<K> parseRulesDocument();
}
