package vikram.connect.com.connect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by vikram on 4/21/16.
 */

public class DefinitionActivity extends AppCompatActivity {
    private WebView mwv;

    /**
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definition);
        mwv = (WebView) findViewById(R.id.web);
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
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setTitle(Data.videoWord + " Explained");
        mwv.loadUrl(Data.video);
    }
}
