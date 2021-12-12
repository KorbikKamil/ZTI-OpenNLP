import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class TypePrediction {
    private TokenizerME tokenizer;
    private NameFinderME nameFinderOrgME;
    private NameFinderME nameFinderPerME;
    private NameFinderME nameFinderLocME;
    private POSTaggerME posTagger;
    private DictionaryLemmatizer lemmatizer;

    public TypePrediction() throws IOException {
        {
            InputStream inputStream = new FileInputStream("src/main/resources/models/en-token.bin");
            TokenizerModel model = new TokenizerModel(inputStream);
            tokenizer = new TokenizerME(model);
        }
        {
            InputStream inputStream = new FileInputStream("src/main/resources/models/en-ner-organization.bin");
            TokenNameFinderModel model = new TokenNameFinderModel(inputStream);
            nameFinderOrgME = new NameFinderME(model);
        }
        {
            InputStream inputStream = new FileInputStream("src/main/resources/models/en-ner-person.bin");
            TokenNameFinderModel model = new TokenNameFinderModel(inputStream);
            nameFinderPerME = new NameFinderME(model);
        }
        {
            InputStream inputStream = new FileInputStream("src/main/resources/models/en-ner-location.bin");
            TokenNameFinderModel model = new TokenNameFinderModel(inputStream);
            nameFinderLocME = new NameFinderME(model);
        }
        {
            InputStream inputStream = new FileInputStream("src/main/resources/models/en-pos-maxent.bin");
            POSModel posModel = new POSModel(inputStream);
            posTagger = new POSTaggerME(posModel);
        }
        {
            InputStream dictLemmatizer = new FileInputStream("src/main/resources/models/en-lemmatizer.dict");
            lemmatizer = new DictionaryLemmatizer(dictLemmatizer);
        }
    }
    public PredictionData GetPrediction(String pyt) throws IOException {
        //wyciaganie metadanych
        Question question = new Question(
                pyt,
                tokenizer,
                nameFinderOrgME,
                nameFinderPerME,
                nameFinderLocME,
                posTagger,
                lemmatizer
        );

        System.out.println(question.toString());

        //wybor kategorii

        //wybor typu


        //Tymczasowe
        ArrayList<String> types = new ArrayList<String>();
        types.add("T1");
        types.add("T2");
        PredictionData pred = new PredictionData("P1", types);

        return pred;
    }

}
