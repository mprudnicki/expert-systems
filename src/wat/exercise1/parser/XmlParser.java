package wat.exercise1.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import wat.exercise1.model.Fact;
import wat.exercise1.model.Rule;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class XmlParser implements DocumentParser<Document, Rule> {

    private Document document;

    @Override
    public boolean loadDocument(String path) {
        try {
            File xmlFile = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.parse(xmlFile);
            document.getDocumentElement().normalize();
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Failed read attempt");
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Fact> parseFactsDocument() {
        System.out.print("Parsing XML facts document... ");
        final List<Fact> factList = new LinkedList<>();

        NodeList factXmlList = document.getElementsByTagName("fact");
        for (int i = 0; i < factXmlList.getLength(); i++) {
            Node factXmlNode = factXmlList.item(i);
            final Fact fact = new Fact();
            if (factXmlNode.getNodeType() == Node.ELEMENT_NODE) {
                NodeList symptomNodeList = ((Element) factXmlNode).getElementsByTagName("symptom");
                for(int j = 0; j < symptomNodeList.getLength(); j++) {
                    fact.addSymptom(
                            symptomNodeList.item(j).getAttributes().getNamedItem("value").getTextContent(),
                            Boolean.valueOf(symptomNodeList.item(j).getTextContent())
                    );
                }
                factList.add(fact);
            }
        }
        System.out.println("Parsed!");
        return factList;
    }

    @Override
    public List<Rule> parseRulesDocument() {
        System.out.print("Parsing XML rules document... ");
        final List<Rule> ruleList = new LinkedList<>();
        final NodeList xmlRuleList = document.getElementsByTagName("rule");
        for (int i = 0; i < xmlRuleList.getLength(); i++) {
            Rule rule = new Rule();
            final Node node = xmlRuleList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                final Element outcome = (Element) ((Element) node).getElementsByTagName("outcome").item(0);
                rule.setOutcome(outcome.getAttribute("value"));
                rule.setCertainty(Double.valueOf(outcome.getAttribute("certainty")));

                final NodeList ruleConditionList = ((Element) node).getElementsByTagName("condition");
                for(int j = 0; j < ruleConditionList.getLength(); j++) {
                    Element condition = (Element) ruleConditionList.item(j);
                    rule.addCondition(condition.getAttribute("id"), condition.getTextContent());
                }
            }

            ruleList.add(rule);
        }
        generateRuleRelationsFromDocument(ruleList);
        System.out.println("Parsed!");
        return ruleList;
    }

    private void generateRuleRelationsFromDocument(List<Rule> ruleList) {
        ruleList.stream().forEach(e -> e.getConditions().entrySet().stream().forEach(entry -> {
            Rule parentRule = findRuleByOutcome(ruleList, entry.getKey()).orElse(null);
            if(parentRule != null) e.addToParentRuleList(parentRule);
        }));
    }

    private Optional<Rule> findRuleByOutcome(List<Rule> ruleList, String outcome) {
        return ruleList.stream().filter(e -> e.getOutcome().equals(outcome)).findAny();
    }
}
