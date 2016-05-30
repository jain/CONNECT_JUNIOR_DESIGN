package vikram.connect.com.connect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by vikram on 5/29/16.
 */
public class SelectAdapter extends RecyclerView.Adapter<SelViewHolder> {
    private ArrayList<String[]> data;
    private final View.OnClickListener mOnClickListener;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    // Provide a suitable constructor (depends on the kind of dataset)
    public SelectAdapter(ArrayList<String[]> myDataset, AppCompatActivity act, RecyclerView rView) {
        data = myDataset;
        mOnClickListener = new SelectClickListener(rView, data, act);
    }

    @Override
    public SelViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.select_cv, viewGroup, false);
        v.setOnClickListener(mOnClickListener);
        SelViewHolder svh = new SelViewHolder(v);
        return svh;
    }

    @Override
    public void onBindViewHolder(SelViewHolder svh, int i) {
        svh.name.setText(data.get(i)[0]);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(data.get(i)[1], svh.photo);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
