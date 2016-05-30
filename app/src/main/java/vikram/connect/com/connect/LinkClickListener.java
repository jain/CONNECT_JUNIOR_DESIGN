package vikram.connect.com.connect;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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
public class LinkClickListener implements AdapterView.OnItemClickListener {
    private HashMap<String, String> map;
    private ArrayList<String> links;
    private EditActivity act;
    public LinkClickListener(HashMap<String, String> map, ArrayList<String> links, EditActivity act){
        this.map = map;
        this.links = links;
        this.act = act;
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        final String title = links.get(pos);
        builder.setTitle(title);
        builder.setMessage("Link:");
        final EditText link = new EditText(act);
        final String url = map.get(title).trim();

        link.setText(url);
        builder.setView(link);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!link.getText().toString().trim().equals(url)) {
                            try {
                                //Data.module.remove("editted");
                                Data.module.put("editted", "1");
                                JSONObject wordLinks = Data.module.getJSONObject("word links");
                                wordLinks.put(title, link.getText().toString().trim());
                                Data.save(act);
                                act.setupListView();
                            } catch (JSONException e) {
                                Toast.makeText(act, "JSON Failed", Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(act, "Save Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        builder.create().show();
    }
}
