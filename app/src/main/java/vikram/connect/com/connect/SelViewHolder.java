package vikram.connect.com.connect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

/**
 * Created by vikram on 5/30/16.
 */
public class SelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    // each data item is just a string in this case

    CardView cv;
    TextView name;
    ImageView photo;
    AppCompatActivity act;
    String module;
    public SelViewHolder(View v) {
        super(v);
        cv = (CardView)itemView.findViewById(R.id.cv_sel);
        name = (TextView)itemView.findViewById(R.id.sel_name);
        photo = (ImageView)itemView.findViewById(R.id.sel_photo);
    }

    @Override
    public void onClick(View view) {
        try {
            Data.module = Data.modules.getJSONObject(module);
            Intent intent = new Intent(act, MainActivity.class);
            act.startActivity(intent);
        } catch (JSONException e) {
            Toast.makeText(act, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}