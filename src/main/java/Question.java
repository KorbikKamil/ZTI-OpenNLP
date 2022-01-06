import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.util.Span;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Question {
    private String entireText;
    private List<String> tokens;
    private List<Span> organizations;
    private List<Span> persons;
    private List<Span> locations;
    private List<String> partsOfSpeech;
    private List<String> lemmatizated;
    private List<String> wh_words;

    private List<String> whWordsTags;

    Question(String question_text,
             TokenizerME tokenizer,
             NameFinderME nameFinderOrgME,
             NameFinderME nameFinderPerME,
             NameFinderME nameFinderLocME,
             POSTaggerME posTagger,
             DictionaryLemmatizer lemmatizer
    ) throws IOException {
        whWordsTags = new ArrayList<String>();
        whWordsTags.add("WRB");
        whWordsTags.add("WDT");
        whWordsTags.add("WP");
        whWordsTags.add("WP$");

        entireText = question_text;

        String[] temp_tokens;

        //Tokenization
        {
            temp_tokens = tokenizer.tokenize(question_text);
            tokens = Arrays.asList(temp_tokens);
        }

        //Named entity recognition
        //Organizations
        {
            organizations = Arrays.asList(nameFinderOrgME.find(temp_tokens));
        }
        //Persons
        {
            persons = Arrays.asList(nameFinderPerME.find(temp_tokens));
        }
        //Locations
        {
            locations = Arrays.asList(nameFinderLocME.find(temp_tokens));
        }

        String[] temp_parts_of_speech;

        //Part of speech tagging
        {
            temp_parts_of_speech = posTagger.tag(temp_tokens);
            partsOfSpeech = Arrays.asList(temp_parts_of_speech);
        }

        //Lemmatization
        {
            lemmatizated = Arrays.asList(lemmatizer.lemmatize(temp_tokens, temp_parts_of_speech));
        }

        //Checking for wh-word
        {
            //https://www.ling.upenn.edu/hist-corpora/annotation/pos-wh.htm
            wh_words = getWhWordsOfTag();
        }

    }

    private ArrayList<String> getWhWordsOfTag() {
        ArrayList<String> to_return = new ArrayList<>();
        for(int index = 0; index < partsOfSpeech.size(); ++index){
            if(whWordsTags.contains(partsOfSpeech.get(index))){
                String whWord = lemmatizated.get(index);
                if ("how".equals(whWord)) {
                    System.out.println("how " + tokens.get(index + 1) + " -" + lemmatizated.get(index + 1) + "-");
                }
                if ("how".equals(whWord)
                        && ("O".equals(lemmatizated.get(index + 1))
                        || "many".equals(lemmatizated.get(index + 1))
                        || "much".equals(lemmatizated.get(index + 1))
                        || "short".equals(lemmatizated.get(index + 1))
                        || "deep".equals(lemmatizated.get(index + 1))
                        || "old".equals(lemmatizated.get(index + 1))
                        || "big".equals(lemmatizated.get(index + 1))
                        || "long".equals(lemmatizated.get(index + 1)))) {
                    System.out.println("dopisujemy number");
                    whWord += "number";
                }
                if(("which".equals(whWord) || "what".equals(whWord)) && "be".equals(lemmatizated.get(index + 1))){
                    whWord = "whatbe";
                }
                to_return.add(whWord);
            }
        }
        return to_return;
    }

    public String getEntireText() {
        return entireText;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public List<Span> getOrganizations() {
        return organizations;
    }

    public List<Span> getPersons() {
        return persons;
    }

    public List<Span> getLocations() {
        return locations;
    }

    public List<String> getPartsOfSpeech() {
        return partsOfSpeech;
    }

    public List<String> getLemmatizated() {
        return lemmatizated;
    }

    public List<String> getWhWords() {
        return wh_words;
    }

    public String toString() {
        String to_ret = entireText + "\n";
        to_ret += "Tokens: " + tokens.toString() + "\n";
        to_ret += "Named entities: \n";
        to_ret += "Organizations: " + organizations.toString() + "\n";
        to_ret += "Persons: " + persons.toString() + "\n";
        to_ret += "Locations: " + locations.toString() + "\n";
        to_ret += "Parts of speech: " + partsOfSpeech.toString() + "\n";
        to_ret += "Lemmatizated: " + lemmatizated.toString() + "\n";
        to_ret += "Wh-word: " + wh_words + "\n";
        return to_ret;
    }
}
