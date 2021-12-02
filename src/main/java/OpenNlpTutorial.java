import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.langdetect.*;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.*;
import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertThat;


public class OpenNlpTutorial {
//    SOURCE:
//    https://www.baeldung.com/apache-open-nlp


    @Test
    public void givenEnglishModel_whenDetect_thenSentencesAreDetected()
            throws Exception {

        String paragraph = "This is a statement. This is another statement. "
                + "Now is an abstract word for time, "
                + "that is always flying. And my email address is google@gmail.com.";

        try (InputStream modelIn = new FileInputStream("en-sent.bin")){
            SentenceModel model = new SentenceModel(modelIn);

            SentenceDetectorME sdetector = new SentenceDetectorME(model);

            String[] sentences = sdetector.sentDetect(paragraph);

            System.out.println(Arrays.toString(sentences));
        }
    }

    @Test
    public void givenWhitespaceTokenizer_whenTokenize_thenTokensAreDetected() {

        WhitespaceTokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize("Baeldung is a Spring Resource.");

        System.out.println(Arrays.toString("Baeldung is a Spring Resource.".split(" ")));
//        assertThat(tokens)
//                .contains("Baeldung", "is", "a", "Spring", "Resource.");
        System.out.println(Arrays.toString(tokens));
    }

    @Test
    public void givenSimpleTokenizer_whenTokenize_thenTokensAreDetected()
            throws Exception {

        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer
                .tokenize("Baeldung is a Spring Resource.");

        System.out.println(Arrays.toString(tokens));
    }


//    NN – noun, singular or mass
//    DT – determiner
//    VB – verb, base form
//    VBD – verb, past tense
//    VBZ – verb, third person singular present
//    IN – preposition or subordinating conjunction
//    NNP – proper noun, singular
//    TO – the word “to”
//    JJ – adjective
    @Test
    public void givenPOSModel_whenPOSTagging_thenPOSAreDetected()
            throws Exception {

        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize("John likes Beer very Much, but Angie Says paul is Weird.");

        try (InputStream inputStreamPOSTagger = new FileInputStream("en-pos-maxent.bin")) {
            POSModel posModel = new POSModel(inputStreamPOSTagger);
            POSTaggerME posTagger = new POSTaggerME(posModel);
            String[] tags = posTagger.tag(tokens);

            System.out.println(Arrays.toString(tags));
        }
    }

    @Test
    public void givenEnglishDictionary_whenLemmatize_thenLemmasAreDetected()
            throws Exception {

        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize("John has a sister named Penny.");

        try (InputStream inputStreamPOSTagger = new FileInputStream("en-pos-maxent.bin")) {
            POSModel posModel = new POSModel(inputStreamPOSTagger);
            POSTaggerME posTagger = new POSTaggerME(posModel);
            String[] tags = posTagger.tag(tokens);
            InputStream dictLemmatizer = new FileInputStream("en-lemmatizer.dict");
            DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(
                    dictLemmatizer);
            String[] lemmas = lemmatizer.lemmatize(tokens, tags);

            System.out.println(Arrays.toString(lemmas));
        }
    }

    @Test
    public void
    givenChunkerModel_whenChunk_thenChunksAreDetected()
            throws Exception {

        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize("He reckons the current account deficit will narrow to only 8 billion.");

        InputStream inputStreamPOSTagger = new FileInputStream("en-pos-maxent.bin");
        POSModel posModel = new POSModel(inputStreamPOSTagger);
        POSTaggerME posTagger = new POSTaggerME(posModel);
        String[] tags = posTagger.tag(tokens);

        InputStream inputStreamChunker = new FileInputStream("en-chunker.bin");
        ChunkerModel chunkerModel
                = new ChunkerModel(inputStreamChunker);
        ChunkerME chunker = new ChunkerME(chunkerModel);
        String[] chunks = chunker.chunk(tokens, tags);
        System.out.println(Arrays.toString(chunks));
    }

    @Test
    public void
    givenLanguageDictionary_whenLanguageDetect_thenLanguageIsDetected()
            throws IOException {

        InputStreamFactory dataIn
                = new MarkableFileInputStreamFactory(
                new File("DoccatSample.txt"));
        ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
        LanguageDetectorSampleStream sampleStream
                = new LanguageDetectorSampleStream(lineStream);
        TrainingParameters params = new TrainingParameters();
        params.put(TrainingParameters.ITERATIONS_PARAM, 100);
        params.put(TrainingParameters.CUTOFF_PARAM, 5);
        params.put("DataIndexer", "TwoPass");
        params.put(TrainingParameters.ALGORITHM_PARAM, "NAIVEBAYES");

        LanguageDetectorModel model = LanguageDetectorME
                .train(sampleStream, params, new LanguageDetectorFactory());

        LanguageDetector ld = new LanguageDetectorME(model);
        Language[] languages = ld
                .predictLanguages("Mi piace molto bere birra");
        System.out.println(Arrays.toString(languages));
    }
}
