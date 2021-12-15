import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

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
                    int correctCategoriesNumber = 0;
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
                        PredictionData pd = tp3.getPrediction(questionString, false);
                        if(pd.category.equals(correctCategory)){
                            correctCategoriesNumber++;
                        }
                        totalQuestionsNumber++;
                    }
                    System.out.println("Correct categories: " + String.valueOf(correctCategoriesNumber) + "/" + String.valueOf(totalQuestionsNumber) + " (" + String.valueOf((double)correctCategoriesNumber / (double)totalQuestionsNumber) + ")");
                    break;
                case 4:
                    System.exit(0);
            }

        }
    }
}
