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

    Question(String question_text,
             TokenizerME tokenizer,
             NameFinderME nameFinderOrgME,
             NameFinderME nameFinderPerME,
             NameFinderME nameFinderLocME,
             POSTaggerME posTagger,
             DictionaryLemmatizer lemmatizer
    ) throws IOException {
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
            int wrb_index = partsOfSpeech.indexOf("WRB"); //https://www.ling.upenn.edu/hist-corpora/annotation/pos-wh.htm
            wh_words = new ArrayList<>();
            wh_words.add(getWordsOfTag("WRB"));
            wh_words.add(getWordsOfTag("WDT"));
            wh_words.add(getWordsOfTag("WP"));
            wh_words.add(getWordsOfTag("WP$"));

            while(wh_words.remove(null));
        }

    }

    private String getWordsOfTag(String tag) {
        int index = partsOfSpeech.indexOf(tag);
        if (index >= 0) {
            return lemmatizated.get(index);
        }
        return null;
    }

    public String getEntireText() {
        return entireText;
    }
    public List<String> getTokens() {
        return tokens;
    }
    public List<Span> getOrganizations(){
        return organizations;
    }
    public List<Span> getPersons(){
        return persons;
    }
    public List<Span> getLocations(){
        return locations;
    }
    public List<String> getPartsOfSpeech(){
        return partsOfSpeech;
    }
    public List<String> getLemmatizated(){
        return lemmatizated;
    }
    public List<String> getWhWords(){
        return wh_words;
    }

    public String toString(){
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
