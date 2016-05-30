package vikram.connect.com.connect;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by vikram on 5/29/16.
 */
public class DownloadedAdapter extends RecyclerView.Adapter<DownloadedAdapter.OldViewHolder> {
    private ArrayList<String[]> data;
    private AppCompatActivity act;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class OldViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        CardView cv;
        TextView name;
        ImageView photo;
        Button update;
        Button deleteB;
        public OldViewHolder(View v) {
            super(v);
            cv = (CardView)itemView.findViewById(R.id.cv_old);
            name = (TextView)itemView.findViewById(R.id.old_name);
            update = (Button) itemView.findViewById(R.id.old_update);
            deleteB = (Button) itemView.findViewById(R.id.old_delete);
            photo = (ImageView)itemView.findViewById(R.id.old_photo);
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public DownloadedAdapter(ArrayList<String[]> myDataset, AppCompatActivity act) {
        data = myDataset;
        this.act = act;
    }

    @Override
    public OldViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.old_cv, viewGroup, false);
        OldViewHolder ovh = new OldViewHolder(v);
        return ovh;
    }

    @Override
    public void onBindViewHolder(OldViewHolder ovh, int i) {
        ovh.name.setText(data.get(i)[0]);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(data.get(i)[1], ovh.photo);
        final int index = i;
        ovh.deleteB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Data.modules.remove(data.get(index)[0]);
                try {
                    Data.save(act);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                act.recreate();
            }
        });
        ovh.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Object dat = Data.firebaseJS.get(data.get(index)[0]);
                    //Data.modules.remove(data.get(index)[0]);
                    Data.modules.put(data.get(index)[0], dat);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
                try {
                    Data.save(act);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                act.recreate();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
