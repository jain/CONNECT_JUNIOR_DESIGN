package vikram.demo.com.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by vikram on 4/21/16.
 */
public class Select extends Activity {
    static int state = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);
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
