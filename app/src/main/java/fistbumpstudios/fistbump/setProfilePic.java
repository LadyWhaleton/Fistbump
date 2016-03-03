package fistbumpstudios.fistbump;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

public class setProfilePic extends AppCompatActivity {
    static int RESULT_LOAD_IMAGE = 1;
    String picturePath ="";

    private void writePicPath() {

        try {
            FileOutputStream fos = openFileOutput(setUserName.userFilename, Context.MODE_PRIVATE |MODE_APPEND);
            fos.write((picturePath + "\n").getBytes());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_pic);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.profilePreview);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }
    public void selectProfilePic(View view){
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);

    }
    public void verifyPic(View view){
        if(picturePath.equals("")){
            Toast.makeText(this, "Select a profile picture!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        writePicPath();
        Intent intent = new Intent(this, TabbedMain.class);
        startActivity(intent);
        finish();
    }
}
