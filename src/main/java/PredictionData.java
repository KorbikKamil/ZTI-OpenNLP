import java.util.ArrayList;

public class PredictionData {
    public String category;
    public ArrayList<String> types;

    public PredictionData(String cat, ArrayList<String> ty){
        category=cat;
        types=ty;
    }
    public String GetString(){
        String gString;
        gString = "category: " + category;
        gString += "\ntypes" + types.toString();
        return gString;
    }
}
