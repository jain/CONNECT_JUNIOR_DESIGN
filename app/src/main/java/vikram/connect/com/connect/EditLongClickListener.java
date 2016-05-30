package vikram.connect.com.connect;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vikram on 5/30/16.
 */
public class EditLongClickListener implements Button.OnLongClickListener {
    private String soFar;
    private HashMap<String, JSONObject> jsonMap;
    private EditActivity act;
    private String word;
    public EditLongClickListener(EditActivity act, String soFar, HashMap<String, JSONObject> jsonMap
    , String word){
        this.act = act;
        this.soFar = soFar;
        this.jsonMap = jsonMap;
        this.word = word;
    }
    @Override
    public boolean onLongClick(View view) {
        final Dialog dialog = new Dialog(act);
        dialog.setContentView(R.layout.long_click);
        dialog.setTitle("Edit Phrase");
        final EditText phrTxt = (EditText) dialog.findViewById(R.id.edit);
        phrTxt.setText(word);
        Button editButton = (Button) dialog.findViewById(R.id.editButton);
        editButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!phrTxt.getText().toString().trim().equals(word)) {
                    try {
                        Data.module.put("editted", "1");
                        JSONObject parent = jsonMap.get(soFar);
                        parent.put(phrTxt.getText().toString().trim(), parent.get(word));
                        parent.remove(word);
                        Data.save(act);
                        act.onResume();
                        act.command.setText(soFar);
                    } catch (JSONException e) {
                        Toast.makeText(act, "JSON Failed", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(act, "Save Failed", Toast.LENGTH_LONG).show();
                    }
                }
                dialog.cancel();
            }
        });
        Button delButton = (Button) dialog.findViewById(R.id.delButton);
        delButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Data.module.put("editted", "1");
                    JSONObject parent = jsonMap.get(soFar);
                    parent.remove(word);
                    Data.save(act);
                    act.onResume();
                    act.command.setText(soFar);
                } catch (JSONException e) {
                    Toast.makeText(act, "JSON Failed", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(act, "Save Failed", Toast.LENGTH_LONG).show();
                }
                dialog.cancel();
            }
        });
        final EditText child = (EditText) dialog.findViewById(R.id.child);
        Button addChild = (Button) dialog.findViewById(R.id.addChild);
        addChild.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String childText = child.getText().toString().trim().toLowerCase();
                try {
                    Data.module.put("editted", "1");
                    JSONObject parent = jsonMap.get(soFar);
                    if (parent.get(word) instanceof JSONObject){
                        if (parent.getJSONObject(word).has(childText)){
                            Toast.makeText(act, "Already Has Specified Child", Toast.LENGTH_LONG).show();
                        } else {
                            parent.getJSONObject(word).put(childText, ".asd");
                            Data.save(act);
                        }
                    } else {
                        JSONObject childJS = new JSONObject();
                        childJS.put(childText, ".asd");
                        parent.put(word, childJS);
                        Data.save(act);
                    }
                } catch (JSONException e) {
                    Toast.makeText(act, "JSON Failed", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(act, "Save Failed", Toast.LENGTH_LONG).show();
                }
                act.onResume();
                act.command.setText(soFar);
                dialog.cancel();
            }
        });
        dialog.show();
        return true;
    }
}
