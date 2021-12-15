import java.util.List;

public class PredictionData {
    public String category;
    public List<String> types;

    public PredictionData(String cat, List<String> ty){
        category=cat;
        types=ty;
    }

    @Override
    public String toString() {
        return "PredictionData{" +
                "category='" + category + '\'' +
                ", types=" + types +
                '}';
    }
}
