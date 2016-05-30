package vikram.connect.com.connect;

import android.content.Intent;

import android.content.res.Configuration;
import android.os.Bundle;

import android.support.design.widget.NavigationView;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by vikram on 4/21/16.
 */
public class Select extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public static final String TAG = "MyTag";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    private RecyclerView modules;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);


        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(Select.this));
        //http://blog.xebia.com/android-design-support-navigationview/
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);

        mActivityTitle = getTitle().toString();
        setupDrawer();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (item.getTitle().toString().toLowerCase().contains("download")){
            Intent intent = new Intent(Select.this, DownloadActivity.class);
            startActivity(intent);
        }
        Log.d(item.getTitle().toString(), "asd");
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Data.modules = new JSONObject(Data.read(this));
        } catch (IOException e) {
            e.printStackTrace();
            try {
                Data.modules =  new JSONObject(loadJSONFromAsset());
            } catch (JSONException e1) {
                Data.modules = new JSONObject();
            }
            try {
                Data.save(this);
            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                Data.modules =  new JSONObject(loadJSONFromAsset());
            } catch (JSONException e1) {
                Data.modules = new JSONObject();
            }
            try {
                Data.save(this);
            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        modules = (RecyclerView)findViewById(R.id.modules);
        modules.setLayoutManager(new LinearLayoutManager(this));
        try {
            recInit();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*ArrayAdapter moduleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, moduleNames);
        modules.setAdapter(moduleAdapter);
        modules.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Data.module = Data.modules.getJSONObject(moduleNames.get(position));
                    Intent intent = new Intent(Select.this, MainActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    Toast.makeText(Select.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });*/

    }

    private void recInit() throws JSONException {
        ArrayList<String[]> moduleNames = new ArrayList<String[]>();
        Iterator<String> it = Data.modules.keys();
        while (it.hasNext()) {
            String name = it.next();
            String[] data = new String[]{name, Data.modules.getJSONObject(name).getString("icon")};
            moduleNames.add(data);
        }
        SelectAdapter adapter = new SelectAdapter(moduleNames, this, modules);
        modules.setAdapter(adapter);

    }

    public String loadJSONFromAsset() {
        String json = "";
        try {
            InputStream is = getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
