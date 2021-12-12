import java.util.ArrayList;

public class TypePrediction {
    private String pytanie;
    public TypePrediction(String pyt){
        pytanie = pyt;
    }
    public PredictionData GetPrediction(){
        ArrayList<String> types = new ArrayList<String>();
        types.add("T1");
        types.add("T2");
        PredictionData pred = new PredictionData("P1", types);
        //wyciaganie metadanych

        //wybor kategorii

        //wybor typu

        return pred;
    }

}
