import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Question {
    private String entire_text;
    private List<String> tokens;
    private List<Span> organizations;
    private List<Span> persons;
    private List<Span> locations;
    private List<String> parts_of_speech;
    private List<String> lemmatizated;
    private String wh_word;

    Question(String question_text,
             TokenizerME tokenizer,
             NameFinderME nameFinderOrgME,
             NameFinderME nameFinderPerME,
             NameFinderME nameFinderLocME,
             POSTaggerME posTagger,
             DictionaryLemmatizer lemmatizer
    ) throws IOException {
        entire_text = question_text;

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
            parts_of_speech = Arrays.asList(temp_parts_of_speech);
        }

        //Lemmatization
        {
            lemmatizated = Arrays.asList(lemmatizer.lemmatize(temp_tokens, temp_parts_of_speech));
        }

        //Checking for wh-word
        {
            int wrb_index = parts_of_speech.indexOf("WRB");
            if(wrb_index == -1){
                wh_word = null;
            }else{
                wh_word = lemmatizated.get(wrb_index);
            }
        }

    }

    public String getEntireText() {
        return entire_text;
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
        return parts_of_speech;
    }
    public List<String> getLemmatizated(){
        return lemmatizated;
    }
    public String getWhWord(){
        return wh_word;
    }

    public String toString(){
        String to_ret = entire_text + "\n";
        to_ret += "Tokens: " + tokens.toString() + "\n";
        to_ret += "Named entities: \n";
        to_ret += "Organizations: " + organizations.toString() + "\n";
        to_ret += "Persons: " + persons.toString() + "\n";
        to_ret += "Locations: " + locations.toString() + "\n";
        to_ret += "Parts of speech: " + parts_of_speech.toString() + "\n";
        to_ret += "Lemmatizated: " + lemmatizated.toString() + "\n";
        to_ret += "Wh-word: " + wh_word + "\n";
        return to_ret;
    }
}
