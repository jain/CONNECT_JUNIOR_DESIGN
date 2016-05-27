package vikram.connect.com.connect;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private ArrayList<String> sentences;
    private ListView wordsList;
    private EditText command;
    private TextView input;
    private HashMap<String, String> map;
    private HashMap<String, HashSet<String>> wordMap;
    private LinearLayout layout1;
    private static String cmon = "";
    TextToSpeech tts;
    @Override
    protected void onResume(){
        super.onResume();
        cmon = "";
        map = new HashMap<String, String>();
        map.put("follow", "trollololol");
        //test();
        //input.setText("follow the yellow brick road, follow, n00b");
        sentences = new ArrayList<String>();
        if (vikram.connect.com.connect.Select.state == 0){
            fillSentences0();
        } else {
            fillSentences1();
        }
        wordMap = new HashMap<>();
        fillMap();
        layout1 = (LinearLayout) findViewById(R.id.list1);
        remake("");
    }

    private void fillSentences0() {
        sentences.add("Hello");
        sentences.add("Excuse me");
        sentences.add("Please say that again");
        sentences.add("Yes");
        sentences.add("No");
        sentences.add("Where");
        sentences.add("When");
        sentences.add("Thank you");
        sentences.add("How much is it");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tts = new TextToSpeech(this, this);
        command = (EditText) findViewById(R.id.command);
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

    private void fillMap() {
        for (String sen: sentences){
            String[] words = sen.trim().toLowerCase().split(",");
            String soFar = "";
            if (!wordMap.containsKey(soFar)){
                wordMap.put(soFar, new HashSet<String>());
            }
            wordMap.get(soFar).add(words[0].trim().toLowerCase());
            for (int i = 0; i<words.length-1; i++){
                soFar = (soFar + " " + words[i]).trim().toLowerCase();
                if (!wordMap.containsKey(soFar)){
                    wordMap.put(soFar, new HashSet<String>());
                }
                wordMap.get(soFar).add(words[i+1].trim().toLowerCase());
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

    public void fillSentences1(){
        sentences.add("Where's the,cat food");
        sentences.add("Where's the,dog food");
        sentences.add("Where's the,flea powder");
        sentences.add("Where's the,cat litter");
        sentences.add("Where's the,dog collars");
        sentences.add("Where's the,leashes");
        sentences.add("Do you have");
    }
    public void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speech recognition demo");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }
    public void voiceRec(View v){
        startVoiceRecognitionActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
                && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String origString = matches.get(0);
            input.setText(origString);
            for (String word : map.keySet()){
                //origString = origString.replaceAll(word,"<a href=\"http://google.com/\">"+word+"</a>");
                //origString = origString.replaceAll(word,"<a href=\"http://vik.com/\">"+word+"</a>");
                clickify(input, word, new ClickSpan.OnClickListener() {
                    @Override
                    public void onClick() {
                        //Toast.makeText(MainActivity.this, "Whale was clicked!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                        startActivity(intent);
                        //MainActivity.this.finish();
                    }
                });
            }
            //input.setText(Html.fromHtml(origString));
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void speak(View v){
        tts.speak(command.getText().toString(), TextToSpeech.QUEUE_ADD, null);
        command.setText("");
    }
    @Override
    public void onInit(int status) {
        tts.setLanguage(Locale.US);
    }
}
