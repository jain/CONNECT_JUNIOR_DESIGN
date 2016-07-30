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
import android.view.MenuItem;
import android.view.View;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Activity is first screen user sees on opening the app
 * Activity is basically a RecyclerView with button on bottom
 * RecyclerView is inflated with data user has stored on his/her phone
 */
public class SelectActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "MyTag"; // tag for volley network requests
    private DrawerLayout mDrawerLayout; // reference to navigation view layout
    private ActionBarDrawerToggle mDrawerToggle; // reference to action bar to design it
    private String mActivityTitle; // reference to title of current activity
    private RecyclerView modules; // reference to RecyclerView present in activity

    /**
     * Method to generate the view when initially loaded
     *
     * @param savedInstanceState data stored in application
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // set layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        // instantiate ImageView which will be used throughout the app for loading images
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(SelectActivity.this));
        // instantiate navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        // set activity name
        mActivityTitle = getTitle().toString();
        // customize the drawer for app
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    /**
     * Configure navigation drawer for app
     */
    public void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            /**
             * Called when a drawer has settled in a completely open state.
             *
             * @param drawerView view of which drawer is child
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely closed state.
             *
             * @param view view of which drawer was child
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        // allow easy toggling of nav drawer
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * Default method for when the configuration is changed, simply set to new config
     *
     * @param newConfig the new configuration
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Default method for after the navigation drawer is created
     *
     * @param savedInstanceState data which is saved inside drawer context
     */
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    /**
     * Default method for toggling item in navigation drawer
     *
     * @param item menu item selected
     * @return the success at selecting specified item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * When an item from the navigation view is selected perform the corresponding action
     *
     * @param item selected item
     * @return success at navigating to specified item in menu
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // if download button selected, go to the corresponding activity
        if (item.getTitle().toString().toLowerCase().contains("download")) {
            Intent intent = new Intent(SelectActivity.this, DownloadActivity.class);
            startActivity(intent);
        } // else if update selected go to that activity
        else if (item.getTitle().toString().toLowerCase().contains("update")) {
            Intent intent = new Intent(SelectActivity.this, UpdateActivity.class);
            startActivity(intent);
        }
        return false;
    }

    /**
     * When the activity is resumed, reload the data and recreate the recycler view
     */
    @Override
    public void onResume() {
        super.onResume();
        // reload the data
        try {
            Data.modules = new JSONObject(Data.read(this));
        } catch (IOException e) {
            // if issues getting data from app try reloading data from the local storage
            e.printStackTrace();
            try {
                Data.modules = new JSONObject(loadJSONFromAsset());
            } catch (JSONException e1) {
                Data.modules = new JSONObject();
            }
            // ensure data is synced
            try {
                Data.save(this);
            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (JSONException e) {
            // if issues getting data from app try reloading data from the local storage
            e.printStackTrace();
            try {
                Data.modules = new JSONObject(loadJSONFromAsset());
            } catch (JSONException e1) {
                Data.modules = new JSONObject();
            }
            // ensure data is synced
            try {
                Data.save(this);
            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        // recreate the RecyclerView
        modules = (RecyclerView) findViewById(R.id.modules);
        modules.setLayoutManager(new LinearLayoutManager(this));
        try {
            recInit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate data for the RecyclerView and generate the RecyclerView for the activity
     *
     * @throws JSONException
     */
    public void recInit() throws JSONException {
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

    /**
     * Method interprets the json data stored on the phone
     *
     * @return json data stored on phone
     */
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

    /**
     * When back button pressed close drawer if open, else continue with default action
     */
    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * If clicked on activity_instructions button go to designated activity
     *
     * @param view clicked button
     */
    public void instructions(View view) {
        Intent intent = new Intent(SelectActivity.this, InstructionsActivity.class);
        startActivity(intent);
    }

}
