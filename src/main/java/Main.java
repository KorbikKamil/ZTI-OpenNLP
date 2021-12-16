import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
        while(true) {
            System.out.println("*=================== Menu ===================*");
            System.out.println("|1 - Przewiduj dla jednego zdania            |");
            System.out.println("|2 - Przewiduj z pliku (JSON)                |");
            System.out.println("|3 - Przetestuj poprawność                   |");
            System.out.println("|4 - Wyjdz                                   |");
            System.out.println("|============================================|");

            Scanner scanner = new Scanner(System.in);

            int menu_val = scanner.nextInt();
            String userInput;
            switch (menu_val){
                case 1:
                    System.out.println("Wprowadz pytanie");
                    Scanner scanner2 = new Scanner(System.in);
                    userInput = scanner2.nextLine();
                    TypePrediction tp = new TypePrediction();
                    System.out.println(tp.getPrediction(userInput, true));
                    break;
                case 2:
                    System.out.println("Podaj sciezke do pliku");
                    Scanner scanner3 = new Scanner(System.in);
                    userInput = scanner3.nextLine();
                    JSONObject jsonObject = JSONInputReader.parseJSONFile(userInput);
                    JSONArray questions = jsonObject.getJSONArray("questions");
                    TypePrediction tp2 = new TypePrediction();
                    for(int i = 0; i < questions.length(); i++)
                    {
                        JSONObject object = questions.getJSONObject(i);
                        String questionString = object.getString("question");
                        PredictionData pd = tp2.getPrediction(questionString, true);
                        object.put("category", pd.category);
                        object.put("types", pd.types);
                    }
                    System.out.println(jsonObject.toString());
                    FileWriter file2 = new FileWriter("src/main/resources/testResult.json");
                    file2.write(jsonObject.toString());
                    file2.flush();
                    file2.close();
                    break;
                case 3:
                    int totalQuestionsNumber = 0;

                    //total questions by category
                    int totalQuestionsLiteralCatNumber = 0;
                    int totalQuestionsResourceCatNumber = 0;
                    int totalQuestionsBooleanCatNumber = 0;

                    int correctCategoriesNumber = 0;

                    //correct questions by category
                    int correctLiteralCategoriesNumber = 0;
                    int correctResourceCategoriesNumber = 0;
                    int correctBooleanCategoriesNumber = 0;

                    //total literal questions by type
                    int totalDateTypeNumber = 0;
                    int totalStringTypeNumber = 0;
                    int totalNumberTypeNumber = 0;

                    //correct literal questions by type
                    int correctDateTypeNumber = 0;
                    int correctStringTypeNumber = 0;
                    int correctNumberTypeNumber = 0;

                    int correctTypeNumber = 0;
                    System.out.println("Podaj sciezke do pliku");
                    Scanner scanner4 = new Scanner(System.in);
                    userInput = scanner4.nextLine();
                    JSONObject jsonObject2 = JSONInputReader.parseJSONFile(userInput);
                    JSONArray questions2 = jsonObject2.getJSONArray("questions");
                    TypePrediction tp3 = new TypePrediction();
                    for(int i = 0; i < questions2.length(); i++)
                    {
                        JSONObject object = questions2.getJSONObject(i);
                        String questionString = object.getString("question");
                        String correctCategory = object.getString("category");
                        String correctType = object.getJSONArray("type").get(0).toString();

                        PredictionData pd = tp3.getPrediction(questionString, false);
                        if(pd.category.equals(correctCategory)){
                            correctCategoriesNumber++;
                            switch (correctCategory) {
                                case TypePrediction.CATEGORY_LITERAL: {
                                    correctLiteralCategoriesNumber++;
                                    if(pd.types.contains(correctType)) {
                                        switch (correctType) {
                                            case TypePrediction.TYPE_DATE: {
                                                correctDateTypeNumber++;
                                                break;
                                            }
                                            case TypePrediction.TYPE_STRING: {
                                                correctStringTypeNumber++;
                                                break;
                                            }
                                            case TypePrediction.TYPE_NUMBER: {
                                                correctNumberTypeNumber++;
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                                case TypePrediction.CATEGORY_RESOURCE: {
                                    correctResourceCategoriesNumber++;
                                    break;
                                }
                                case TypePrediction.CATEGORY_BOOLEAN: {
                                    correctBooleanCategoriesNumber++;
                                    break;
                                }
                            }
                        }

                        /*if(correctCategory.equals(TypePrediction.CATEGORY_LITERAL) && pd.category.equals(correctCategory)) {
                            totalQuestionsLiteralCatNumber++;
                            if(correctType.equals(pd.types.get(0))) {
                                correctTypeNumber++;
                            } else {
                                System.out.println("question: " + questionString + "; correct type: " + correctType + "; our type:" + pd.types.get(0));
                            }
                        }*/
                        switch (correctCategory) {
                            case TypePrediction.CATEGORY_LITERAL: {
                                totalQuestionsLiteralCatNumber++;
                                switch (correctType){
                                    case TypePrediction.TYPE_DATE:{
                                        totalDateTypeNumber++;
                                        break;
                                    }
                                    case TypePrediction.TYPE_STRING:{
                                        totalStringTypeNumber++;
                                        break;
                                    }
                                    case TypePrediction.TYPE_NUMBER:{
                                        totalNumberTypeNumber++;
                                        break;
                                    }
                                }
                                break;
                            }
                            case TypePrediction.CATEGORY_RESOURCE: {
                                totalQuestionsResourceCatNumber++;
                                break;
                            }
                            case TypePrediction.CATEGORY_BOOLEAN: {
                                totalQuestionsBooleanCatNumber++;
                                break;
                            }
                        }
                        totalQuestionsNumber++;
                    }
                    System.out.println("CATEGORIES");
                    System.out.println("Correct categories: " + String.valueOf(correctCategoriesNumber) + "/" + String.valueOf(totalQuestionsNumber) + " (" + String.valueOf((double)correctCategoriesNumber / (double)totalQuestionsNumber) + ")");
                    System.out.println("Correctly identified literal category: " + String.valueOf(correctLiteralCategoriesNumber) + "/" + String.valueOf(totalQuestionsLiteralCatNumber) + " (" + String.valueOf((double)correctLiteralCategoriesNumber / (double)totalQuestionsLiteralCatNumber) + ")");
                    System.out.println("Correctly identified resource category: " + String.valueOf(correctResourceCategoriesNumber) + "/" + String.valueOf(totalQuestionsResourceCatNumber) + " (" + String.valueOf((double)correctResourceCategoriesNumber / (double)totalQuestionsResourceCatNumber) + ")");
                    System.out.println("Correctly identified boolean category: " + String.valueOf(correctBooleanCategoriesNumber) + "/" + String.valueOf(totalQuestionsBooleanCatNumber) + " (" + String.valueOf((double)correctBooleanCategoriesNumber / (double)totalQuestionsBooleanCatNumber) + ")");
                    System.out.println("LITERAL TYPES");
                    System.out.println("Correctly identified date type: " + String.valueOf(correctDateTypeNumber) + "/" + String.valueOf(totalDateTypeNumber) + " (" + String.valueOf((double)correctDateTypeNumber / (double)totalDateTypeNumber) + ")");
                    System.out.println("Correctly identified string type: " + String.valueOf(correctStringTypeNumber) + "/" + String.valueOf(totalStringTypeNumber) + " (" + String.valueOf((double)correctStringTypeNumber / (double)totalStringTypeNumber) + ")");
                    System.out.println("Correctly identified number type: " + String.valueOf(correctNumberTypeNumber) + "/" + String.valueOf(totalNumberTypeNumber) + " (" + String.valueOf((double)correctNumberTypeNumber / (double)totalNumberTypeNumber) + ")");
                    break;
                case 4:
                    System.exit(0);
            }

        }
    }
}
