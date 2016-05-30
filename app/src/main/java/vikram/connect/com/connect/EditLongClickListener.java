package vikram.connect.com.connect;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle("Edit Phrase");
        final EditText phrTxt = new EditText(act);

        phrTxt.setText(word);
        builder.setView(phrTxt);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
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
            }
        });
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
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
            }
        });
        // Create the AlertDialog object and return it
        builder.create().show();
        return true;
    }
}
