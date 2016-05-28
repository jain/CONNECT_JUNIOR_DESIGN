package vikram.connect.com.connect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Created by vikram on 4/21/16.
 */
public class Select extends AppCompatActivity {
    static int state = 0;
    public static final String TAG = "MyTag";
    private RequestQueue queue;
    Firebase ref;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);
        mDrawerList = (ListView)findViewById(R.id.navList);
        addDrawerItems();
    }
    private void addDrawerItems() {
        String[] osArray = { "Android", "iOS", "Windows", "OS X", "Linux" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
    }
    @Override
    protected void onResume() {
        super.onResume();
        /*Firebase.setAndroidContext(this);
        ref = new Firebase("https://connectjuniordesign.firebaseio.com");
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
                for (DataSnapshot child : snapshot.getChildren()){
                    String module = child.getKey();
                    Log.d(module, module);
                    for (DataSnapshot moduleInner :child.getChildren()){
                        System.out.println(moduleInner);
                    }
                }
                snapshot.getValue();
            }

            @Override public void onCancelled(FirebaseError error) { }

        });*/
        /*JSONObject modules = loadJSONFromAsset();
        //modules.
        Iterator<String> iter = modules.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                Object value = modules.get(key);
                Log.d("val", value.toString());
            } catch (JSONException e) {
                // Something went wrong!
            }
        }*/
        queue = Volley.newRequestQueue(this);
        String url = "https://connectjuniordesign.firebaseio.com//.json?print=pretty";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try{
                            JSONObject js = new JSONObject(response);
                            Log.d("hi", js.toString(2)); // 2 for printing in a pretty way
                            Log.d("a", "a");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("no", "no");
            }
        });
        queue.add(stringRequest);
    }

    public JSONObject loadJSONFromAsset() {
        JSONObject jsonObj = null;
        try {

            InputStream is = getAssets().open("data.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            String json = new String(buffer, "UTF-8");

            jsonObj = new JSONObject(json);

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

    public void proceed(View v) {
        if (((Button) v).getText().toString().toLowerCase().equals("petsmart")) {
            state = 1;
        } else {
            state = 0;
        }
        Intent intent = new Intent(Select.this, MainActivity.class);
        startActivity(intent);
    }

}
