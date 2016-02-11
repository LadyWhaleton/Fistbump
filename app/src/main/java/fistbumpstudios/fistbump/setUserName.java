package fistbumpstudios.fistbump;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class setUserName extends AppCompatActivity {
    String uname = "";
    String fname = "";
    File userInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_name);
    }

    public void setUname (View view){

        EditText mEdit=(EditText)findViewById(R.id.username);
        uname = mEdit.getText().toString();

        if(uname.equals("")){
            Toast.makeText(this, "Enter a username!" + uname,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        fname = uname + ".txt";
        Toast.makeText(this, "Set Username to " + uname ,
                Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("uname", uname);
        startActivity(intent);
        finish();
    }

}
