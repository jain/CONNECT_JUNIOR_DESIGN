package vikram.connect.com.connect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class DownloadActivity extends AppCompatActivity {
    private RecyclerView oldRv;
    private RecyclerView newRv;

    private RequestQueue queue;
    private HashSet<String> moduleNames;

    private JSONObject firebaseJS;
    public static final String TAG = "MyTag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(DownloadActivity.this));
        oldRv = (RecyclerView) findViewById(R.id.old);
        newRv = (RecyclerView) findViewById(R.id.newOnes);
    }
    @Override
    public void onResume(){
        super.onResume();
        oldRv.setLayoutManager(new LinearLayoutManager(this));
        newRv.setLayoutManager(new LinearLayoutManager(this));
        moduleNames = new HashSet<String>();
        try {
            initializeOld();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initializeNew();
    }

    private void initializeNew() {
        queue = Volley.newRequestQueue(this);
        String url = "https://connectjuniordesign.firebaseio.com//.json?print=pretty";
        final ArrayList<String[]> modules = new ArrayList<String[]>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try{
                            JSONObject js = new JSONObject(response);
                            firebaseJS = js;
                            Iterator<String> it = js.keys();

                            while(it.hasNext()){
                                String name = it.next();
                                if (!moduleNames.contains(name)){
                                    String[] data = new String[]{name, js.getJSONObject(name).getString("icon")};
                                    modules.add(data);
                                }
                            }
                            DownloadAdapter adapter = new DownloadAdapter(modules);
                            newRv.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DownloadActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }
    @Override
    public void onPause(){
        super.onPause();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }
    private void initializeOld() throws JSONException {
        Iterator<String> it = Data.modules.keys();
        ArrayList<String[]> modules = new ArrayList<String[]>();
        while (it.hasNext()){
            String name = it.next();
            moduleNames.add(name);
            String[] data = new String[]{name, Data.modules.getJSONObject(name).getString("icon")};
            modules.add(data);
        }
        DownloadedAdapter adapter = new DownloadedAdapter(modules, this);
        oldRv.setAdapter(adapter);
    }
}
