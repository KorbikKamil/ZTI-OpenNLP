import org.json.*;
import sun.misc.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JSONInputReader {
   public static JSONObject parseJSONFile(String filename) throws JSONException, IOException {
       String content = new String(Files.readAllBytes(Paths.get(filename)));
       return new JSONObject(content);
   }
}
