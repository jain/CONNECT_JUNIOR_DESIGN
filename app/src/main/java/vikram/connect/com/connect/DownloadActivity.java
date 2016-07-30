package vikram.connect.com.connect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Activity class for when user is wanting to download new modules
 * Will inflate CardViews in a RecyclerView for modules the user currently has
 * The user can then pick modules not present on his/her device to be downloaded from Firebase
 */
public class DownloadActivity extends AppCompatActivity {
    private RecyclerView newRv; // RecyclerView for this activity
    private RequestQueue queue; // queue of network requests
    private HashSet<String> moduleNames; // stores data to populate RecyclerView
    public static final String TAG = "MyTag"; // necessary to perform network requests

    /**
     * Loads and creates the layout for current view
     * Instantiates elements from layout
     *
     * @param savedInstanceState data stored in application so far
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        newRv = (RecyclerView) findViewById(R.id.newOnes);
    }

    /**
     * Called when user switches to this activity once it has been created
     * Sets up the RecyclerView based on data present and data available online
     */
    @Override
    public void onResume() {
        super.onResume();
        newRv.setLayoutManager(new LinearLayoutManager(this));
        moduleNames = new HashSet<String>();
        try {
            initializeOld();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initializeNew();
    }

    /**
     * Query data from Firebase to see what modules user can get
     * Updates RecyclerView based on the information obtained
     */
    public void initializeNew() {
        // code for query
        queue = Volley.newRequestQueue(this);
        String url = "https://connectjuniordesign.firebaseio.com//.json?print=pretty";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new VolleyRequest(newRv, moduleNames, this), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DownloadActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        // add query to network queue
        queue.add(stringRequest);
    }

    /**
     * Called when user switches to different screen
     * Must remove all pending network requests from this screen
     */
    @Override
    public void onPause() {
        super.onPause();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

    /**
     * Gets list of already downloaded modules
     *
     * @throws JSONException
     */
    private void initializeOld() throws JSONException {
        // get data from local storage and append it to global variable
        Iterator<String> it = Data.modules.keys();
        ArrayList<String[]> modules = new ArrayList<String[]>();
        while (it.hasNext()) {
            String name = it.next();
            moduleNames.add(name);
            String[] data = new String[]{name, Data.modules.getJSONObject(name).getString("icon")};
            modules.add(data);
        }
    }
}
