package vikram.connect.com.connect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by vikram on 5/30/16.
 */
public class SelectClickListener implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private ArrayList<String[]> data;
    private AppCompatActivity act;
    public SelectClickListener(RecyclerView mRecyclerView, ArrayList<String[]> data, AppCompatActivity act){
        this.mRecyclerView = mRecyclerView;
        this.data = data;
        this.act = act;
    }
    @Override
    public void onClick(View view) {
        int itemPosition = mRecyclerView.getChildLayoutPosition(view);
        try {
            Data.module = Data.modules.getJSONObject(data.get(itemPosition)[0]);
            Intent intent = new Intent(act, MainActivity.class);
            act.startActivity(intent);
        } catch (JSONException e) {
            Toast.makeText(act, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
