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

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, NavigationView.OnNavigationItemSelectedListener{
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private EditText command;
    private TextView input;
    private HashMap<String, HashSet<String>> wordMap;
    private LinearLayout layout1;
    private static String cmon = "";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    TextToSpeech tts;
    @Override
    protected void onResume(){
        super.onResume();
        cmon = "";
        wordMap = new HashMap<String, HashSet<String>>();
        try {
            fillMap();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        command.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tts = new TextToSpeech(this, this);
        command = (EditText) findViewById(R.id.command);
        layout1 = (LinearLayout) findViewById(R.id.list1);
        command.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                MainActivity.cmon = editable.toString();
                remake(editable.toString());
            }
        });
        input = (TextView) findViewById(R.id.input);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_main);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_main);
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
        if (item.getTitle().toString().toLowerCase().contains("edit")){
            Intent intent = new Intent(MainActivity.this, EditActivity.class);
            startActivity(intent);
        }
        Log.d(item.getTitle().toString(), "asd");
        return false;
    }

    private void remake(String soFar) {
        layout1.removeAllViews();
        layout1.invalidate();
        soFar = cmon.toLowerCase().trim();
        //soFar = soFar.toLowerCase().trim();
        if (!wordMap.containsKey(soFar)){
            command.setSelection(command.getText().length());
            return;
        }
        for (String word: wordMap.get(soFar)){
            LinearLayout layout2 = new LinearLayout(this);
            layout2.setOrientation(LinearLayout.HORIZONTAL);
            layout2.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 10.0f));
            layout2.setGravity(Gravity.CENTER);
            Button first = new Button(this);
            first.setText(word);
            final String w2 = word;
            first.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    command.setText(command.getText().toString() + " " + w2);
                }
            });
            layout2.addView(first);
            layout2.addView(new TextView(this));
            if (wordMap.containsKey((soFar + " " + word).trim().toLowerCase())) {
                LinearLayout layout3 = new LinearLayout(this);
                layout3.setOrientation(LinearLayout.VERTICAL);
                layout3.setGravity(Gravity.CENTER);

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
    private void fillMapRecursion(String soFar, JSONObject next) throws JSONException {
        Iterator<String> iter = next.keys();
        while (iter.hasNext()) {
            if (!wordMap.containsKey(soFar)){
                wordMap.put(soFar, new HashSet<String>());
            }
            String word = iter.next();
            wordMap.get(soFar).add(word.trim().toLowerCase());
            if(next.get(word) instanceof JSONObject){
                fillMapRecursion((soFar + " " + word.trim().toLowerCase()).trim().toLowerCase(), next.getJSONObject(word));
            }
        }
    }
    private void fillMap() throws JSONException {
        JSONObject phrases = Data.module.getJSONObject("phrases");
        Iterator<String> iter = phrases.keys();
        while (iter.hasNext()) {
            String soFar = "";
            if (!wordMap.containsKey(soFar)){
                wordMap.put(soFar, new HashSet<String>());
            }
            String word = iter.next();
            wordMap.get(soFar).add(word.trim().toLowerCase());
            if(phrases.get(word) instanceof JSONObject){
                fillMapRecursion(word.trim().toLowerCase(), phrases.getJSONObject(word));
            }
        }
    }

    public static void clickify(TextView view, final String clickableText,  final ClickSpan.OnClickListener listener) {
        CharSequence text = view.getText();
        String string = text.toString();
        ClickSpan span = new ClickSpan(listener);
        int start = string.indexOf(clickableText);
        int end = start + clickableText.length();
        if (start == -1) return;

        if (text instanceof Spannable) {
            ((Spannable)text).setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
    public void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speech recognition demo");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }
    public void voiceRec(View v) throws InterruptedException {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"messageID");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
                && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String origString = matches.get(0);
            input.setText(origString);
            String[] splitWords = origString.split(" ");
            for (String word : splitWords){
                final String wrd = word;
                clickify(input, word, new ClickSpan.OnClickListener() {
                    @Override
                    public void onClick() {
                        Data.videoWord = wrd;
                        Data.video = "http://www.signasl.org/sign/"+wrd;
                        Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                        startActivity(intent);

                    }
                });
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void speak(View v){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"messageID");
        tts.speak(command.getText().toString(), TextToSpeech.QUEUE_FLUSH, map);
        int len = command.getText().toString().length();
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                //Toast.
            }

            @Override
            public void onDone(String utteranceId) {

            }

            @Override
            public void onError(String utteranceId) {
            }
        });
        command.setText("");
    }
    @Override
    public void onInit(int status) {
        tts.setLanguage(Locale.US);
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
