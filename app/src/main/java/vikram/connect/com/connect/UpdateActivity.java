package vikram.connect.com.connect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Activity class for when user is wanting to update modules
 * Will inflate cardviews in a recyclerview for modules the user currently has
 * The user can then pick to delete or update those modules to the data stored on Firebase
 */
public class UpdateActivity extends AppCompatActivity {
    private RecyclerView oldRv;
    private RequestQueue queue;
    private HashSet<String> moduleNames;
    public static final String TAG = "MyTag";

    /**
     * creates the view
     * finds necessary elements from layout file
     *
     * @param savedInstanceState data saved so far in application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        oldRv = (RecyclerView) findViewById(R.id.old);
    }

    /**
     * called when user switches to this activity once it has been created
     * sets up the cardview based on data present and data available online
     */
    @Override
    public void onResume() {
        super.onResume();
        oldRv.setLayoutManager(new LinearLayoutManager(this));
        moduleNames = new HashSet<String>();
        try {
            initializeOld();
            initializeNew();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * called when user switches to different screen
     * must end network requests at this time
     */
    public void onPause() {
        super.onPause();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

    /**
     * provides data for the recycler view adapter which is formatted properly
     * recyclerview is assigned adapter and inflates internal cardviews
     *
     * @throws JSONException
     */
    public void initializeOld() throws JSONException {
        // get data and format it
        Iterator<String> it = Data.modules.keys();
        ArrayList<String[]> modules = new ArrayList<String[]>();
        while (it.hasNext()) {
            String name = it.next();
            moduleNames.add(name);
            String[] data = new String[]{name, Data.modules.getJSONObject(name).getString("icon")};
            modules.add(data);
        }
        // give formatted data to adapter for recycler view to use
        DownloadedAdapter adapter = new DownloadedAdapter(modules, this);
        oldRv.setAdapter(adapter);
    }

    /**
     * query data from Firebase to see what updates the modules can gets
     */
    public void initializeNew() {
        // code for query
        queue = Volley.newRequestQueue(this);
        String url = "https://connectjuniordesign.firebaseio.com//.json?print=pretty";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new VolleyRequest(null, moduleNames, this), new Response.ErrorListener() {
            /**
             * ensure the request was successful else through an error to let the user know
             *
             * @param error
             */
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UpdateActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        // add query to network queue
        queue.add(stringRequest);
    }

}