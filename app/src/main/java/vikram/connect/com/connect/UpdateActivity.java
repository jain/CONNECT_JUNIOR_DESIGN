package vikram.connect.com.connect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.RequestQueue;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by vikram on 7/24/16.
 */
public class UpdateActivity extends AppCompatActivity {
    private RecyclerView oldRv;
    private RecyclerView newRv;

    private RequestQueue queue;
    private HashSet<String> moduleNames;

    public static final String TAG = "MyTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        oldRv = (RecyclerView) findViewById(R.id.old);
    }
    @Override
    public void onResume() {
        super.onResume();
        oldRv.setLayoutManager(new LinearLayoutManager(this));
        //newRv.setLayoutManager(new LinearLayoutManager(this));
        moduleNames = new HashSet<String>();
        try {
            initializeOld();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
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