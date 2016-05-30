package vikram.connect.com.connect;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class EditActivity extends AppCompatActivity {
    private HashMap<String, String> map;
    private ArrayList<String> links;
    private HashMap<String, HashSet<String>> wordMap;
    private HashMap<String, JSONObject> jsonMap;
    private LinearLayout layout1;
    EditText command;
    private static String cmon = "";
    private ListView lView;

    private EditText phraseText;
    private EditText wordText;
    private EditText linkText;
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        layout1 = (LinearLayout) findViewById(R.id.list2);
        lView = (ListView) findViewById(R.id.wordLinks);
        command = (EditText) findViewById(R.id.command2);
        command.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                EditActivity.cmon = editable.toString();
                remake(editable.toString());
            }
        });

    }
    public void addPhrase (View view){
        String phr = phraseText.getText().toString().trim().toLowerCase();
        if(phr.isEmpty()){
            Toast.makeText(this, "Please enter a valid phrase", Toast.LENGTH_LONG).show();
            return;
        }
        if(!wordMap.containsKey(cmon + " " + phr)){
            jsonMap.get(cmon);
        } else {
            Toast.makeText(this, "Phrase already exists.", Toast.LENGTH_LONG).show();
        }
        dialog.cancel();
    }
    public void addLink(View view) {
        String word = wordText.getText().toString().trim().toLowerCase();
        String link = linkText.getText().toString().trim();
        if(word.isEmpty()||link.isEmpty()){
            Toast.makeText(this, "Please enter a valid phrase", Toast.LENGTH_LONG).show();
            return;
        }
        if(!map.containsKey(word)){
            try {
                Data.module.put("editted", "1");
                JSONObject wordLinks = Data.module.getJSONObject("word links");
                Data.save(this);
                wordLinks.put(word, link);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "JSON Parsing caused error", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong while saving", Toast.LENGTH_LONG).show();
            }
            setupListView();
        } else {
            Toast.makeText(this, "Word already exists.", Toast.LENGTH_LONG).show();
        }
        dialog.cancel();
    }
    public void newPhrase(View view){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.phrase);
        dialog.setTitle("Addition");

        // set the custom dialog components - text, image and button
        phraseText = (EditText) dialog.findViewById(R.id.phrase);
        wordText = (EditText) dialog.findViewById(R.id.word);
        linkText = (EditText) dialog.findViewById(R.id.link);


        dialog.show();
    }
    @Override
    protected void onResume(){
        super.onResume();
        cmon = "";
        wordMap = new HashMap<String, HashSet<String>>();
        jsonMap = new HashMap<String, JSONObject>();
        try {
            fillMap();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        remake("");
        setupListView();
    }

    public void setupListView() {
        lView.invalidate();
        map = new HashMap<String, String>();
        links = new ArrayList<String>();
        try {
            genMap();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter linksAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, links);
        lView.setAdapter(linksAdapter);
        lView.setOnItemClickListener(new LinkClickListener(map, links, this));
        lView.postInvalidate();
    }

    protected void genMap() throws JSONException {
        JSONObject linksJS = Data.module.getJSONObject("word links");
        Iterator iter = linksJS.keys();
        while (iter.hasNext()){
            String wrd = iter.next().toString();
            map.put(wrd, linksJS.getString(wrd));
            links.add(wrd);
        }
    }

    private void fillMapRecursion(String soFar, JSONObject next) throws JSONException {
        Iterator<String> iter = next.keys();
        while (iter.hasNext()) {
            if (!wordMap.containsKey(soFar)){
                wordMap.put(soFar, new HashSet<String>());
                jsonMap.put(soFar, next);
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
                jsonMap.put(soFar, phrases);
            }
            String word = iter.next();
            wordMap.get(soFar).add(word.trim().toLowerCase());
            if(phrases.get(word) instanceof JSONObject){
                fillMapRecursion(word.trim().toLowerCase(), phrases.getJSONObject(word));
            }
        }
    }
    private void remake(String soFar) {
        layout1.removeAllViews();
        layout1.invalidate();
        soFar = cmon.toLowerCase().trim();
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
            first.setOnLongClickListener(new EditLongClickListener(this, soFar, jsonMap, word));
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
}
