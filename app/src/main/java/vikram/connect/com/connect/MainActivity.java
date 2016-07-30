package vikram.connect.com.connect;

import android.content.Intent;
import android.content.res.Configuration;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

/**
 * Activity which is the main screen of the app
 * User can construct phrase trees and input text
 * This will be said out loud by push of a button
 * User can request speech to be interpreted in this Activity too via speech to text
 */
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, NavigationView.OnNavigationItemSelectedListener {
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private EditText command;
    private TextView input;
    private HashMap<String, HashSet<String>> wordMap;
    private LinearLayout layout1;
    private static String typedString = "";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private TextToSpeech tts;

    /**
     * method called when this Activity is displayed again
     */
    @Override
    public void onResume() {
        super.onResume();
        typedString = "";
        wordMap = new HashMap<String, HashSet<String>>();
        // refill the map
        try {
            fillMap();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        command.setText("");
    }

    /**
     * Creates the the layout for the activity
     * Instantiates the elements of the layouts
     * Instantiates key variables
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // instantiate variables and get elements from layout file
        tts = new TextToSpeech(this, this);
        command = (EditText) findViewById(R.id.command);
        layout1 = (LinearLayout) findViewById(R.id.list1);
        // set listener to edit text
        command.addTextChangedListener(new TextWatcher() {
            /**
             *
             * @param charSequence entered text
             * @param i
             * @param i1
             * @param i2
             */
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            /**
             *
             * @param charSequence
             * @param i
             * @param i1
             * @param i2
             */
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            /**
             *
             * @param editable
             */
            @Override
            public void afterTextChanged(Editable editable) {
                MainActivity.typedString = editable.toString();
                remake();
            }
        });
        input = (TextView) findViewById(R.id.input);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_main);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_main);
        navigationView.setNavigationItemSelectedListener(this);
        mActivityTitle = getTitle().toString();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /**
     *
     */
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            /**
             * Called when a drawer has settled in a completely open state. *
             * @param drawerView
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely closed state.
             * @param view
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    /**
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (item.getTitle().toString().toLowerCase().contains("edit")) {
            Intent intent = new Intent(MainActivity.this, EditActivity.class);
            startActivity(intent);
        }
        Log.d(item.getTitle().toString(), "asd");
        return false;
    }

    /**
     * method for creating the phrase tree on the screen
     */
    private void remake() {
        // invalidate the view
        layout1.removeAllViews();
        layout1.invalidate();
        String soFar = typedString.toLowerCase().trim();
        // check if the string exists in the hashmap, if so we can generate the phrase tree from this point
        if (!wordMap.containsKey(soFar)) {
            command.setSelection(command.getText().length());
            return;
        }
        // generate the first linear layout and populate it
        for (String word : wordMap.get(soFar)) {
            LinearLayout layout2 = new LinearLayout(this);
            layout2.setOrientation(LinearLayout.HORIZONTAL);
            layout2.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 10.0f));
            layout2.setGravity(Gravity.CENTER);
            Button first = new Button(this);
            first.setText(word);
            final String w2 = word;
            // if a phrase is selected add it to the speech which will be said
            first.setOnClickListener(new Button.OnClickListener() {
                /**
                 * method which is called when the button is clicked, it will append phrase to the edittext
                 * @param view
                 */
                public void onClick(View view) {
                    command.setText(command.getText().toString() + " " + w2);
                }
            });
            // generate the second layer of the phrase tree
            layout2.addView(first);
            layout2.addView(new TextView(this));
            if (wordMap.containsKey((soFar + " " + word).trim().toLowerCase())) {
                LinearLayout layout3 = new LinearLayout(this);
                layout3.setOrientation(LinearLayout.VERTICAL);
                layout3.setGravity(Gravity.CENTER);
                // add each phrase to second layer
                for (String word2 : wordMap.get((soFar + " " + word).trim().toLowerCase())) {
                    TextView sec = new TextView(this);
                    sec.setTextSize(20);
                    sec.setText(word2);
                    layout3.addView(sec);
                }

                layout2.addView(layout3);
            }
            layout1.addView(layout2);
            layout1.postInvalidate();
        }
        command.setSelection(command.getText().length());
    }

    /**
     * generate the map for the phrase tree based on the json data loaded in the app
     *
     * @param soFar
     * @param next
     * @throws JSONException
     */
    private void fillMapRecursion(String soFar, JSONObject next) throws JSONException {
        // recurse through the keys in the JSON object from loaded data
        Iterator<String> it = next.keys();
        while (it.hasNext()) {
            if (!wordMap.containsKey(soFar)) {
                wordMap.put(soFar, new HashSet<String>());
            }
            String word = it.next();
            wordMap.get(soFar).add(word.trim().toLowerCase());
            if (next.get(word) instanceof JSONObject) {
                fillMapRecursion((soFar + " " + word.trim().toLowerCase()).trim().toLowerCase(), next.getJSONObject(word));
            }
        }
    }

    /**
     * @throws JSONException
     */
    private void fillMap() throws JSONException {
        JSONObject phrases = Data.module.getJSONObject("phrases");
        Iterator<String> iter = phrases.keys();
        while (iter.hasNext()) {
            String soFar = "";
            if (!wordMap.containsKey(soFar)) {
                wordMap.put(soFar, new HashSet<String>());
            }
            String word = iter.next();
            wordMap.get(soFar).add(word.trim().toLowerCase());
            if (phrases.get(word) instanceof JSONObject) {
                fillMapRecursion(word.trim().toLowerCase(), phrases.getJSONObject(word));
            }
        }
    }

    /**
     * @param view
     * @param clickableText
     * @param listener
     */
    public static void clickify(TextView view, final String clickableText, final ClickSpan.OnClickListener listener) {
        CharSequence text = view.getText();
        String string = text.toString();
        ClickSpan span = new ClickSpan(listener);
        int start = string.indexOf(clickableText);
        int end = start + clickableText.length();
        if (start == -1) return;

        if (text instanceof Spannable) {
            ((Spannable) text).setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            SpannableString s = SpannableString.valueOf(text);
            s.setSpan(span, start, end, Spanned.SPAN_MARK_MARK);
            view.setText(s);
        }

        MovementMethod m = view.getMovementMethod();
        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            view.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    /**
     *
     */
    public void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speech recognition demo");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    /**
     * @param view
     * @throws InterruptedException
     */
    public void voiceRec(View view) throws InterruptedException {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");
        tts.speak("Please speak into the phone after the beep.", TextToSpeech.QUEUE_ADD, map);
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                startVoiceRecognitionActivity();
            }

            @Override
            public void onError(String utteranceId) {
            }
        });


    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
                && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String origString = matches.get(0);
            input.setText(origString);
            String[] splitWords = origString.split(" ");
            for (String word : splitWords) {
                final String wrd = word;
                clickify(input, word, new ClickSpan.OnClickListener() {
                    @Override
                    public void onClick() {
                        Data.videoWord = wrd;
                        Data.video = "http://www.signasl.org/sign/" + wrd;
                        Intent intent = new Intent(MainActivity.this, DefinitionActivity.class);
                        startActivity(intent);

                    }
                });
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * @param view
     */
    public void speak(View view) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");
        tts.speak(command.getText().toString(), TextToSpeech.QUEUE_FLUSH, map);
        int len = command.getText().toString().length();
        final MainActivity ma = this;
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            /**
             *
             * @param utteranceId
             */
            @Override
            public void onStart(String utteranceId) {
                runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      Toast.makeText(ma, "Your message is being said",
                                              Toast.LENGTH_SHORT).show();
                                  }
                              }

                );
            }

            /**
             *
             * @param utteranceId
             */
            @Override
            public void onDone(String utteranceId) {
                runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      Toast.makeText(ma, "Your message has ended",
                                              Toast.LENGTH_SHORT).show();
                                  }
                              }

                );
            }

            /**
             *
             * @param utteranceId
             */
            @Override
            public void onError(String utteranceId) {
            }
        });
        command.setText("");
    }

    /**
     * @param status
     */
    @Override
    public void onInit(int status) {
        tts.setLanguage(Locale.US);
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
