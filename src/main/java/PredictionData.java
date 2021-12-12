import java.util.ArrayList;

public class PredictionData {
    public String category;
    public ArrayList<String> types;

    public PredictionData(String cat, ArrayList<String> ty){
        category=cat;
        types=ty;
    }
    public String getString(){
        String gString;
        gString = "category: " + category + "\n";
        gString += "types: " + types.toString() + "\n";
        return gString;
    }
}
