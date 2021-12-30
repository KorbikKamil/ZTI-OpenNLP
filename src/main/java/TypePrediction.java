import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class TypePrediction {
    public static final String CATEGORY_RESOURCE = "resource";
    public static final String CATEGORY_LITERAL = "literal";
    public static final String CATEGORY_BOOLEAN = "boolean";

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

        if (showDataInConsole) {
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
        if(question.getWhWords().isEmpty()){
            if (question.getLemmatizated().get(0).equals("be") || question.getLemmatizated().get(0).equals("do")) {
                return new PredictionData(CATEGORY_BOOLEAN, Arrays.asList(TYPE_BOOLEAN));
            }else if(question.getLemmatizated().contains("count")){
                return new PredictionData(CATEGORY_LITERAL, Arrays.asList(TYPE_NUMBER));
            }else{
                return new PredictionData(CATEGORY_RESOURCE, generateResourceTypePrediction(question));
            }
        }else{
            if (question.getLemmatizated().get(0).equals("when")) /*Zwracamy datę, string lub liczbę*/ {
                return new PredictionData(CATEGORY_LITERAL, Arrays.asList(TYPE_DATE));
            } else if (question.getWhWords().contains("hownumber")) {
                System.out.println("hownumber " + question.getEntireText());
                return new PredictionData(CATEGORY_LITERAL, Arrays.asList(TYPE_NUMBER));
            } else if (question.getWhWords().contains("how")
                    || question.getWhWords().contains("why")
                    || question.getWhWords().contains("whatbe")) {
                return new PredictionData(CATEGORY_LITERAL, Arrays.asList(TYPE_STRING));
            } else if (question.getWhWords().contains("who")
                    || question.getWhWords().contains("whom")
                    || question.getWhWords().contains("whose")
                    || question.getWhWords().contains("which")
                    || question.getWhWords().contains("what")
                    || question.getWhWords().contains("where")) {
                return new PredictionData(CATEGORY_RESOURCE, generateResourceTypePrediction(question));
            } else if (question.getLemmatizated().contains("be")) {
                return new PredictionData(CATEGORY_BOOLEAN, Arrays.asList(TYPE_BOOLEAN));
            }
        }

        return new PredictionData(CATEGORY_RESOURCE, generateResourceTypePrediction(question));
    }

    private List<String> generateResourceTypePrediction(Question question){
        if(question.getLemmatizated().get(0).equals("who") ||
                question.getLemmatizated().get(0).equals("whose") || question.getLemmatizated().get(0).equals("whom")){
            return Arrays.asList("dbo:Person");
        }else if(question.getLemmatizated().get(0).equals("where") ||
                question.getLemmatizated().get(0).equals("in") && question.getLemmatizated().get(1).equals("which") ||
                question.getLemmatizated().get(0).equals("in") && question.getLemmatizated().get(1).equals("what") ||
                (question.getLemmatizated().get(0).equals("what") || question.getLemmatizated().get(0).equals("which")) &&
                        (question.getLemmatizated().get(1).equals("city") ||
                                question.getLemmatizated().get(1).equals("town") ||
                                question.getLemmatizated().get(1).equals("place") ||
                                question.getLemmatizated().get(1).equals("country") ||
                                question.getLemmatizated().get(1).equals("state") ||
                                question.getLemmatizated().get(1).equals("states"))
        ){
            return Arrays.asList("dbo:Place");
        }

        String word_to_search_for = "";
        if(question.getLemmatizated().get(0).equals("which") && !question.getLemmatizated().get(1).equals("be")){
            word_to_search_for = question.getTokens().get(1);
        }

        if(word_to_search_for != "") {
            String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                    "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\n" +
                    "PREFIX dbo:  <http://dbpedia.org/ontology/>\n" +
                    "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                    "\n" +
                    "SELECT ?item\n" +
                    " WHERE\n" +
                    "   {  \n" +
                    "     ?item rdfs:label \"" + word_to_search_for.toLowerCase() + "\"@en. \n" +
                    "     ?item rdf:type owl:Class.  \n" +
                    "   }";

            try (QueryExecution qexec = QueryExecution.service("http://dbpedia.org/sparql", queryString)) {
                ResultSet results = qexec.execSelect();
                while (results.hasNext()) {
                    QuerySolution soln = results.nextSolution();
                    RDFNode name = soln.get("item");
                    System.out.println(name.asResource().getLocalName());
                    return Arrays.asList("dbo:" + name.asResource().getLocalName());
                }
            }
        }
        return Arrays.asList("-");
    }

}
