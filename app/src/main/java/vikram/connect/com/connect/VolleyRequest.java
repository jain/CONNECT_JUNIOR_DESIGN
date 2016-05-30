package vikram.connect.com.connect;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by vikram on 5/29/16.
 */
public class VolleyRequest implements Response.Listener<String> {
    private ArrayList<String[]> modules;
    private RecyclerView newRv;
    private HashSet<String> moduleNames;
    private AppCompatActivity act;

    public VolleyRequest(RecyclerView newRv, HashSet<String> moduleNames, AppCompatActivity act){
        super();
        this.newRv = newRv;
        this.moduleNames = moduleNames;
        this.act = act;
        modules = new ArrayList<String[]>();
    }

    @Override
    public void onResponse(String response) {
        // Display the first 500 characters of the response string.
        try{
            JSONObject js = new JSONObject(response);
            Data.firebaseJS = js;
            Iterator<String> it = js.keys();

            while(it.hasNext()){
                String name = it.next();
                if (!moduleNames.contains(name)){
                    String[] data = new String[]{name, js.getJSONObject(name).getString("icon")};
                    modules.add(data);
                }
            }
            DownloadAdapter adapter = new DownloadAdapter(modules, act);
            newRv.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
