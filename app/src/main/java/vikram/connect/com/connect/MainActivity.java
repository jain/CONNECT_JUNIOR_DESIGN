package vikram.connect.com.connect;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
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
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234; // reference to code for deciphering speech to text
    private EditText command; // reference to input text
    private TextView input; // reference to text input of voice input
    private HashMap<String, HashSet<String>> wordMap; // reference to phrase mappings to generate phrase tree
    private LinearLayout layout1; // reference to layout to modify dynamically to generate phrase tree
    private static String typedString = ""; // reference to input so far to decide state of phrase tree
    private DrawerLayout mDrawerLayout; // reference to navigation view layout
    private ActionBarDrawerToggle mDrawerToggle; // reference to action bar to design it
    private String mActivityTitle; // reference to title of current activity
    private TextToSpeech tts; // reference to text to speech engine

    /**
     * Method called when this Activity is displayed again
     * Regenerates the UI dynamically
     */
    @Override
    public void onResume() {
        super.onResume();
        typedString = "";
        wordMap = new HashMap<String, HashSet<String>>();
        // regenerate the map and thus the UI
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
     * @param savedInstanceState data stored in application
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
             * Called just before text written into EditText
             * Default required method by Interface, but is unused in this application
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
             * Called as text is being changed
             * Default required method by Interface, but is unused in this application
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
             * Called after text in editText is modified
             *
             * @param editable content which is in the EditText after modification
             */
            @Override
            public void afterTextChanged(Editable editable) {
                MainActivity.typedString = editable.toString();
                remake();
            }
        });
        // get elements from layout file
        input = (TextView) findViewById(R.id.input);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_main);
        // setup the navigation drawer with the 3 bars
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_main);
        navigationView.setNavigationItemSelectedListener(this);
        mActivityTitle = getTitle().toString();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /**
     * Code creates the navigation drawer in the right spot for the application
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
        // enable toggling of drawer
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
    protected void onPostCreate(Bundle savedInstanceState) {
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
        if (item.getTitle().toString().toLowerCase().contains("edit")) {
            Intent intent = new Intent(MainActivity.this, EditActivity.class);
            startActivity(intent);
        }
        return false;
    }

    /**
     * Method for creating the phrase tree on the screen dynamically based on current status of app
     */
    public void remake() {
        // invalidate the view
        layout1.removeAllViews();
        layout1.invalidate();
        String soFar = typedString.toLowerCase().trim();
        // check if the string exists in the HasMmap, if so we can generate the phrase tree from this point
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
                 * @param view view which was clicked
                 */
                @Override
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
            // add view and validate the layout
            layout1.addView(layout2);
            layout1.postInvalidate();
        }
        command.setSelection(command.getText().length());
    }

    /**
     * Generate remainder of the map for the phrase tree based on the json data loaded in the app
     *
     * @param soFar String which is so far in the map, and for what children phrases can be added
     * @param next  Next set of phrases to be parsed for generating child phrases
     * @throws JSONException
     */
    public void fillMapRecursion(String soFar, JSONObject next) throws JSONException {
        // recurse through the keys in the JSON object from loaded data
        Iterator<String> it = next.keys();
        while (it.hasNext()) {
            if (!wordMap.containsKey(soFar)) {
                wordMap.put(soFar, new HashSet<String>());
            }
            // recursively populate map with the keys such that combinations earlier are valid
            String word = it.next();
            wordMap.get(soFar).add(word.trim().toLowerCase());
            if (next.get(word) instanceof JSONObject) {
                fillMapRecursion((soFar + " " + word.trim().toLowerCase()).trim().toLowerCase(), next.getJSONObject(word));
            }
        }
    }

    /**
     * Generate the map for the phrase tree based on the json data loaded in the app
     *
     * @throws JSONException
     */
    public void fillMap() throws JSONException {
        // get the possible phrases
        JSONObject phrases = Data.module.getJSONObject("phrases");
        Iterator<String> iter = phrases.keys();
        // fill map with the phrases so far
        while (iter.hasNext()) {
            String soFar = "";
            if (!wordMap.containsKey(soFar)) {
                wordMap.put(soFar, new HashSet<String>());
            }
            String word = iter.next();
            wordMap.get(soFar).add(word.trim().toLowerCase());
            // recursively fill the remaining phrases
            if (phrases.get(word) instanceof JSONObject) {
                fillMapRecursion(word.trim().toLowerCase(), phrases.getJSONObject(word));
            }
        }
    }

    /**
     * Method takes text in a view and attaches a listener to the specific text, such that when it is
     * clicked they do actions specified by their listeners
     *
     * @param view          parent view in which the text is present
     * @param clickableText text which is to be made clickable
     * @param listener      listener which is to be attached to the text when which is activated upon click
     */
    public static void clickify(TextView view, final String clickableText, final ClickSpan.OnClickListener listener) {
        // assign listener to specified text
        CharSequence text = view.getText();
        String string = text.toString();
        ClickSpan span = new ClickSpan(listener);
        int start = string.indexOf(clickableText);
        int end = start + clickableText.length();
        // check for invalidity
        if (start == -1) return;
        // customize text to be of a certain type to make it clickable
        if (text instanceof Spannable) {
            ((Spannable) text).setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            SpannableString s = SpannableString.valueOf(text);
            s.setSpan(span, start, end, Spanned.SPAN_MARK_MARK);
            view.setText(s);
        }
        // set movement method for the view
        MovementMethod m = view.getMovementMethod();
        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            view.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    /**
     * Method which creates the intent for voice recognition and begins it
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
     * Method is called when the tts will say what the deaf person wants it to say
     *
     * @param view
     * @throws InterruptedException
     */
    public void voiceRec(View view) throws InterruptedException {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");
        // let hearing person know voice to text is about to begin via tts
        tts.speak("Please speak into the phone after the beep.", TextToSpeech.QUEUE_ADD, map);
        // listener to know when tts is active or not
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            /**
             * Called when tts is about to start
             * Default required method by Interface, but is unused in this application
             *
             * @param utteranceId
             */
            @Override
            public void onStart(String utteranceId) {

            }

            /**
             * Called when tts is done
             * Launches the voice recognition after tts is done
             *
             * @param utteranceId specified id for tts
             */
            @Override
            public void onDone(String utteranceId) {
                startVoiceRecognitionActivity();
            }

            /**
             * Called if tts has error
             * Default required method by Interface, but is unused in this application
             *
             * @param utteranceId
             */
            @Override
            public void onError(String utteranceId) {
            }
        });
    }

    /**
     * Called when the voice to speech is done
     * Tries to recognize the spoken text
     * Will Try to parse spoken text such that hard words are linked so that deaf people can look
     * up their definitions.
     *
     * @param requestCode code requested for voice input
     * @param resultCode  code outputted from voice input
     * @param data        data collected from the voice input engine
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // try to get the text from the voice
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
                && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String origString = matches.get(0);
            input.setText(origString);
            String[] splitWords = origString.split(" ");
            // for each of the words link it to its definition url
            for (String word : splitWords) {
                final String wrd = word;
                // attach unique listener to word
                clickify(input, word, new ClickSpan.OnClickListener() {
                    /**
                     * Called when specific word is clicked
                     * Sets the url for the word and launches appropriate activity to display it
                     */
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
     * Called when user wants his text to be spoken out loud
     * Will use tts to 'speak' the words
     *
     * @param view view in which tts is being used
     */
    public void speak(View view) {
        // set up tts with the input
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");
        tts.speak(command.getText().toString(), TextToSpeech.QUEUE_FLUSH, map);
        int len = command.getText().toString().length();
        final MainActivity ma = this;
        // setup listener such that will notify deaf person when the phone is speaking
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            /**
             * Called when tts is about to start
             *
             * @param utteranceId specified id for tts
             */
            @Override
            public void onStart(String utteranceId) {
                // display appropriate Toast message on UI
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
             * Called when tts is done
             *
             * @param utteranceId specified id for tts
             */
            @Override
            public void onDone(String utteranceId) {
                // display appropriate Toast message on UI
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
             * Called if tts has error
             * Default required method by Interface, but is unused in this application
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
     * Initialize tts to American English
     *
     * @param status status of tts currently
     */
    @Override
    public void onInit(int status) {
        tts.setLanguage(Locale.US);
    }

    /**
     * Called when user presses back button
     * Will close the navigation drawer if open, else will go back to last activity
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
