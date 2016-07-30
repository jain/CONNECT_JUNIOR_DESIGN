package vikram.connect.com.connect;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Class crawls Firebase and pulls new data. The data is stored on RAM unless the user decides
 * to persist with data on local db storage
 * Class will also be responsible for assigning the recycler view new data to populate the UI
 */
public class VolleyRequest implements Response.Listener<String> {
    private ArrayList<String[]> modules;
    private RecyclerView newRv;
    private HashSet<String> moduleNames;
    private AppCompatActivity act;

    /**
     * constructor which takes in params to be used later on
     *
     * @param newRv       recyclerview to be filled
     * @param moduleNames data which may be used to fill recycler view
     * @param act         var representing activity to make UI calls to
     */
    public VolleyRequest(RecyclerView newRv, HashSet<String> moduleNames, AppCompatActivity act) {
        super();
        this.newRv = newRv;
        this.moduleNames = moduleNames;
        this.act = act;
        modules = new ArrayList<String[]>();
    }

    /**
     * response of volley request, assign the newly crawled data to respective variables
     * inflate the recycler view by giving its adapter the newly crawled data
     *
     * @param response response of the network request
     */
    @Override
    public void onResponse(String response) {
        // see if data is valid
        try {
            // parse new data
            JSONObject js = new JSONObject(response);
            Data.firebaseJS = js;
            Iterator<String> it = js.keys();
            // if parsed store the data locally
            while (it.hasNext()) {
                String name = it.next();
                if (!moduleNames.contains(name)) {
                    String[] data = new String[]{name, js.getJSONObject(name).getString("icon")};
                    modules.add(data);
                }
            }
            // assign data to recyclerview to generate UI
            if (newRv != null) {
                DownloadAdapter adapter = new DownloadAdapter(modules, act);
                newRv.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
