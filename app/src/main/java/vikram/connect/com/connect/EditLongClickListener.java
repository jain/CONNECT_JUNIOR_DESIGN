package vikram.connect.com.connect;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

/**
 * Listener class which is attached to button on phrase tree in Edit mode when it is held for a
 * long time.
 * Allows user to use this to edit components of the phrase tree
 */
public class EditLongClickListener implements Button.OnLongClickListener {
    private String soFar; // reference to input string so far to decide state of phrase tree
    private HashMap<String, JSONObject> jsonMap; // reference to map of strings following word
    private EditActivity act; // reference to calling activity
    private String word; // reference to word which was long clicked

    /**
     * Constructor which takes in params which will be necessary in case of edits made
     *
     * @param act     // calling activity
     * @param soFar   // input string so far
     * @param jsonMap // map of strings following word
     * @param word    // word which was long clicked
     */
    public EditLongClickListener(EditActivity act, String soFar, HashMap<String, JSONObject> jsonMap
            , String word) {
        this.act = act;
        this.soFar = soFar;
        this.jsonMap = jsonMap;
        this.word = word;
    }

    /**
     * Called when the button on phrase tree in edit mode is held for a long time
     * Launches a dialog which can modify the tree upon user's request
     * Dialog is constructed in this method
     *
     * @param view view which was clicked
     * @return success of click
     */
    @Override
    public boolean onLongClick(View view) {
        // inflate dialog from view and pull elements
        final Dialog dialog = new Dialog(act);
        dialog.setContentView(R.layout.edit_phrase_dialog);
        dialog.setTitle("Edit Phrase");
        final EditText phrTxt = (EditText) dialog.findViewById(R.id.edit);
        phrTxt.setText(word);
        // set a button to edit the current phrase held down
        Button editButton = (Button) dialog.findViewById(R.id.editButton);
        editButton.setOnClickListener(new Button.OnClickListener() {
            /**
             * Called when edit button is clicked, will try and edit the phrase tree and
             * regenerate the UI accordingly
             *
             * @param view view which was clicked
             */
            @Override
            public void onClick(View view) {
                // checks if edit is valid
                if (!phrTxt.getText().toString().trim().equals(word)) {
                    try {
                        // tries to write the edit to the local storage system
                        Data.module.put("edited", "1");
                        JSONObject parent = jsonMap.get(soFar);
                        parent.put(phrTxt.getText().toString().trim(), parent.get(word));
                        parent.remove(word);
                        Data.save(act);
                        // reconstruct the layout on the screen based on edit
                        act.onResume();
                        act.getCommand().setText(soFar);
                    } catch (JSONException e) { // show errs as toast messages
                        Toast.makeText(act, "JSON Failed", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(act, "Save Failed", Toast.LENGTH_LONG).show();
                    }
                }
                dialog.cancel();
            }
        });
        // set a button to delete the current phrase held down
        Button delButton = (Button) dialog.findViewById(R.id.delButton);
        delButton.setOnClickListener(new Button.OnClickListener() {
            /**
             * Called when delete button is clicked, will try and delete the phrase tree and
             * regenerate the UI accordingly
             *
             * @param view view which was clicked
             */
            @Override
            public void onClick(View view) {
                // check if the deletion is possible and if so do it
                try {
                    Data.module.put("edited", "1");
                    JSONObject parent = jsonMap.get(soFar);
                    parent.remove(word);
                    Data.save(act);
                    // once deletion occurs from local storage regenerate the view on the screen
                    act.onResume();
                    act.getCommand().setText(soFar);
                } catch (JSONException e) { // show errs as toast messages
                    Toast.makeText(act, "JSON Failed", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(act, "Save Failed", Toast.LENGTH_LONG).show();
                }
                dialog.cancel();
            }
        });
        // this part deals with adding children to the current phrase
        // so we need an edit text and a button
        final EditText child = (EditText) dialog.findViewById(R.id.child);
        Button addChild = (Button) dialog.findViewById(R.id.addChild);
        addChild.setOnClickListener(new Button.OnClickListener() {
            /**
             * Called when add button is clicked, will try and add the phrase, after which it will
             * regenerate the UI accordingly
             *
             * @param view view which was clicked
             */
            @Override
            public void onClick(View view) {
                // check if child is valid for current phrase
                String childText = child.getText().toString().trim().toLowerCase();
                try {
                    // try to edit local storage
                    Data.module.put("edited", "1");
                    JSONObject parent = jsonMap.get(soFar);
                    // check if parent is JSONObject otherwise we create one for itself to stand alone
                    if (parent.get(word) instanceof JSONObject) {
                        //  check for duplication
                        if (parent.getJSONObject(word).has(childText)) {
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
                } catch (JSONException e) { // show errs as toast messages
                    Toast.makeText(act, "JSON Failed", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(act, "Save Failed", Toast.LENGTH_LONG).show();
                }
                // regen view to take into account what has changed on screen
                act.onResume();
                act.getCommand().setText(soFar);
                dialog.cancel();
            }
        });
        // launch the dialog once its settings have been set
        dialog.show();
        return true;
    }
}
