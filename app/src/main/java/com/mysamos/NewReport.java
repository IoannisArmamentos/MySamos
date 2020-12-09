package com.mysamos;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class NewReport extends Activity {
    private TextView successText = null;
    boolean firstTime = true;
    TextView tvNewActivityHeader;
    EditText etUserDescription;
    Button bSubmit;
    String userInput, address;
    static File fTakenPhoto;
    LatLng llcoordinates;
    String mode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.abs_layout);


        try {
            mode = getIntent().getStringExtra("MODE");
            Log.d("TO MODE EINAI ", mode);
        } catch (Exception e) {
            Log.e("CRASHARA", "GAMW TO SHISTO MOU");
        }

        if (mode.equals("takePicture")) {
            fTakenPhoto = takePicture();
        } else if (mode.equals("selectPicture")) {
            selectPicture();
        }
        tvNewActivityHeader = (TextView) findViewById(R.id.tvHeaderNewReport);
        etUserDescription = (EditText) findViewById(R.id.etUserDescriptionNewReport);
        bSubmit = (Button) findViewById(R.id.bSubmit);
        tvNewActivityHeader.setText(Map.getAddressFromMapActivity());
        etUserDescription.setText("Add more info...");


        etUserDescription.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (firstTime) {
                    etUserDescription.setText("");
                    firstTime = false;
                }
                return false;
            }
        });


        bSubmit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                address = Map.getAddressFromMapActivity();
                userInput = etUserDescription.getText().toString();
                fTakenPhoto = Map.getTakenPictureFromMapActivity();
                llcoordinates = Map.getCoordinatesFromMapActivity();

                // Submit to Parse
                ParseObject reps = new ParseObject("Reports");
                reps.put("area", "Νέο Καρλόβασι, Σάμος");
                reps.put("comments", userInput);
                //reps.put("image", fTakenPhoto);
                //reps.put("latidude", llcoordinates);
                reps.put("thoroughfare", address);
                reps.put("user", ParseUser.getCurrentUser());
                try {
                    reps.save();
                } catch (ParseException e) {
                }


                // Open Map Again
                //This is in the onCreate method
                Context context = getApplicationContext();
                CharSequence text = "New Report uploaded successfully!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                startActivity(new Intent("com.mysamos.Map"));
            }
        });
    }

    public File takePicture() {

        // create intent with ACTION_IMAGE_CAPTURE action
        File pictureTaken;
        final Intent intent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        // this part to save captured image on provided path
        File file = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),

                imageFileName);

        pictureTaken = file;

        Uri photoPath = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoPath);

        // start camera activity
        startActivityForResult(intent, 1);

        return pictureTaken;
    }

    public void selectPicture() {
        Intent i = new Intent(
                Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        startActivityForResult(i, 2);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        Bitmap bitMap;
        if (requestCode == 1 && resultCode == RESULT_OK
                && intent != null) {
            // get bundle
            Bundle extras = intent.getExtras();

            // get bitmap
            bitMap = (Bitmap) extras.get("data");

            Log.e("->", "onactivityresult");
            startActivity(new Intent("com.mysamos.NewReport"));
        } else if (requestCode == 2 && resultCode == RESULT_OK && null != intent) {
            Uri selectedImage = intent.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            String[] projection = new String[]{
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_TAKEN
            };
            try {
            Cursor cursor = getContentResolver().query(selectedImage,
                    projection, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

                File file = new File(picturePath);
                setFile(file);
            }catch (Exception e){
               System.out.println("Crash to file setting and creating");
            }

        }


    }

    public static void setFile(File file) {
        fTakenPhoto = file;
    }
}
