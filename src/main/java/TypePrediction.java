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
import java.util.Arrays;

public class TypePrediction {
    private static final String CATEGORY_RESOURCE = "resource";
    private static final String CATEGORY_LITERAL = "literal";
    private static final String CATEGORY_BOOLEAN = "boolean";

    public static final String TYPE_BOOLEAN = "boolean";
    public static final String TYPE_STRING = "string";
    public static final String TYPE_DATE = "date";
    public static final String TYPE_NUMBER = "number";

    private final TokenizerME tokenizer;
    private final NameFinderME nameFinderOrgME;
    private final NameFinderME nameFinderPerME;
    private final NameFinderME nameFinderLocME;
    private final POSTaggerME posTagger;
    private final DictionaryLemmatizer lemmatizer;

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

    public PredictionData getPrediction(String questionString, boolean showDataInConsole) throws IOException {
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

        if(showDataInConsole) {
            System.out.println(question);
        }

        //wybor kategorii i typu
        return generatePredictionData(question);
    }

    /**
     * @param question analyzed question with additional data
     * @return one of those categories:
     * resource,
     * literal,
     * boolean.
     */
    private PredictionData generatePredictionData(Question question) {
        if ("be".equals(question.getLemmatizated().get(0)))
        { //TODO: other cases eg. Marie, are you hungry?
            return new PredictionData(CATEGORY_BOOLEAN, Arrays.asList(TYPE_BOOLEAN));
        }
        else if(question.getWhWords().contains("when")) /*Zwracamy datę, string lub liczbę*/
        {
            return new PredictionData(CATEGORY_LITERAL, Arrays.asList(TYPE_DATE));
        }
        else if(question.getWhWords().contains("how"))
        {
            return new PredictionData(CATEGORY_LITERAL, Arrays.asList(""));
        }
        else if (question.getWhWords().contains("who")
                || question.getWhWords().contains("whom")
                || question.getWhWords().contains("where"))
        {
            return new PredictionData(CATEGORY_RESOURCE, Arrays.asList(""));
        }
        return new PredictionData(CATEGORY_RESOURCE, Arrays.asList(""));
    }

}
