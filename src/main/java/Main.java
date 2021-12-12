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
            System.out.println("|3 - Wyjdz                                   |");
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
                    System.out.println(tp.GetPrediction(userInput).getString());
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
                        PredictionData pd = tp2.GetPrediction(questionString);
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
                    System.exit(0);
            }

        }
    }
}
