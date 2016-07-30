package vikram.connect.com.connect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Activity which shows definitions of hard words that deaf people may not be familiar with
 */

public class DefinitionActivity extends AppCompatActivity {
    private WebView mwv; // container for website

    /**
     * Creates and loads the container for the website with the definition
     *
     * @param savedInstanceState savedInstanceState data stored in application
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definition);
        mwv = (WebView) findViewById(R.id.web);
        // specify settings for loading url
        mwv.getSettings().setJavaScriptEnabled(true);
        mwv.getSettings().setPluginState(WebSettings.PluginState.ON);
        mwv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    /**
     * When screen is changed back this is called
     * So load the relevant data and change the title of the screen
     */
    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setTitle(Data.videoWord + " Explained");
        mwv.loadUrl(Data.video);
    }
}
