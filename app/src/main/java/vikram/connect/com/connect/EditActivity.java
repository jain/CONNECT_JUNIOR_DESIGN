package vikram.connect.com.connect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class EditActivity extends AppCompatActivity {
    private HashMap<String, String> map;
    private ArrayList<String> links;
    private HashMap<String, HashSet<String>> wordMap;
    private LinearLayout layout1;
    private EditText command;
    private static String cmon = "";
    private ListView lView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        layout1 = (LinearLayout) findViewById(R.id.list2);
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
        lView = (ListView) findViewById(R.id.wordLinks);

    }

    @Override
    protected void onResume(){
        super.onResume();
        cmon = "";
        map = new HashMap<String, String>();
        links = new ArrayList<String>();
        try {
            genMap();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        wordMap = new HashMap<>();
        try {
            fillMap();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        remake("");
        ArrayAdapter linksAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, links);
        lView.setAdapter(linksAdapter);
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
            }
            String word = iter.next();
            wordMap.get(soFar).add(word.trim().toLowerCase());
            boolean hi = next.get(word) instanceof JSONObject;
            if(next.get(word) instanceof JSONObject){
                boolean n00b = hi;
                boolean maga = n00b;
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
}
