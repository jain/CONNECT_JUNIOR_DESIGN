package vikram.connect.com.connect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Responds to a CardView being clicked on the SelectActivity page
 * This class ensures that the following activity which is opened has the necessary data
 * to proceed.
 */
public class SelectClickListener implements View.OnClickListener {
    private RecyclerView mRecyclerView;  // RecyclerView variable for activity
    private ArrayList<String[]> data; // data which is being used to populate RecyclerView
    private AppCompatActivity act; // reference to activity which called listener

    /**
     * Constructor to create listener
     *
     * @param mRecyclerView recycler view in which the click occurs
     * @param data          data present in the recycler view
     * @param act           variable representing current activity to make UI calls
     */
    public SelectClickListener(RecyclerView mRecyclerView, ArrayList<String[]> data, AppCompatActivity act) {
        this.mRecyclerView = mRecyclerView;
        this.data = data;
        this.act = act;
    }

    /**
     * Switches activities when view is selected such that the new activity has information about
     * what was clicked
     *
     * @param view which was clicked
     */
    @Override
    public void onClick(View view) {
        // get item position to get necessary data
        int itemPosition = mRecyclerView.getChildLayoutPosition(view);
        try {
            Data.module = Data.modules.getJSONObject(data.get(itemPosition)[0]);
            // change activity
            Intent intent = new Intent(act, MainActivity.class);
            act.startActivity(intent);
        } catch (JSONException e) {
            // in case of mishap notify user
            Toast.makeText(act, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
