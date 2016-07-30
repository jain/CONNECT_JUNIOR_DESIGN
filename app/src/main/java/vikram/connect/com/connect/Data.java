package vikram.connect.com.connect;

import android.content.Context;
import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Class is like the Model in MVP, as it basically stores the data for the duration of the app
 * being open
 */
public class Data {
    public static JSONObject modules = null;
    public static JSONObject module = null;
    public static JSONObject firebaseJS = null;
    public static String video = "";
    public static String videoWord = "";

    /**
     * writes data to local files in .json format
     *
     * @param context
     * @throws JSONException
     * @throws IOException
     */
    public static void save(Context context) throws JSONException, IOException {
        File path = context.getFilesDir();
        File file = new File(path, "CONNECT.json");
        Files.write(Data.modules.toString(2), file, Charsets.UTF_8);
        String result = Files.toString(file, Charsets.UTF_8);
        Log.d("what", "what");
        Log.d("path", path.toString());
    }

    /**
     * reads data from local .json files and parses it and puts it in variables to be used
     * by this application
     *
     * @param context
     * @return
     * @throws IOException
     */
    public static String read(Context context) throws IOException {
        File path = context.getFilesDir();
        File file = new File(path, "CONNECT.json");
        String result = Files.toString(file, Charsets.UTF_8);
        Log.d("what", "what");
        Log.d("path", path.toString());
        return result;
    }
}
