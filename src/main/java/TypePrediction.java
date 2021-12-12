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
    private static final String CATEGORY_RESOURCE = "resource";
    private static final String CATEGORY_LITERAL = "literal";
    private static final String CATEGORY_BOOLEAN = "boolean";

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

    public PredictionData getPrediction(String questionString) throws IOException {
        //wyciaganie metadanych
        Question question = new Question(
                questionString,
                tokenizer,
                nameFinderOrgME,
                nameFinderPerME,
                nameFinderLocME,
                posTagger,
                lemmatizer
        );

        System.out.println(question.toString());

        //wybor kategorii
        String category = getCategory(question);

        //wybor typu
        //Tymczasowe
        ArrayList<String> types = new ArrayList<>();
        types.add("T1");
        types.add("T2");

        return new PredictionData(category, types);
    }

    /**
     * @param question analyzed question with additional data
     * @return one of those categories:
     * resource,
     * literal,
     * boolean.
     */
    private String getCategory(Question question) {
        if ("be".equals(question.getLemmatizated().get(0))) { //TODO: other cases eg. Marie, are you hungry?
            return CATEGORY_BOOLEAN;
        } else if ("when".equals(question.getWhWord())) {
            return CATEGORY_LITERAL;
        }
        return "";
    }

}
