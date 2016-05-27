package vikram.connect.com.connect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by vikram on 4/21/16.
 */
public class Select extends AppCompatActivity {
    static int state = 0;
    Firebase ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);

    }
    @Override
    protected void onResume() {
        Log.d("hi", "hi");
        super.onResume();
        Firebase.setAndroidContext(this);
        ref = new Firebase("https://connectjuniordesign.firebaseio.com");
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
                for (DataSnapshot child : snapshot.getChildren()){
                    String module = child.getKey();
                    Log.d(module, module);
                    for (DataSnapshot moduleInner :child.getChildren()){
                        System.out.println(moduleInner);
                    }
                }
                snapshot.getValue();
            }

            @Override public void onCancelled(FirebaseError error) { }

        });
    }
    @Override
    protected void onPause(){
        super.onPause();
    }
    public void proceed (View v){
        if(((Button)v).getText().toString().toLowerCase().equals("petsmart")){
            state = 1;
        } else {
            state = 0;
        }
        Intent intent = new Intent(Select.this, MainActivity.class);
        startActivity(intent);
    }

}
