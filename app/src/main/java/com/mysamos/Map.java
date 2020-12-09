package com.mysamos;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.Session;
import com.google.android.gms.internal.ia;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseUser;

public class Map extends Activity implements LocationListener {
    ImageView ivMyLocation;
    GoogleMap mapfragmentMAP;
    LatLng User;
    Marker userLocation;// UserMarker on Map
    int tiltPercent = 0;
    static LatLng CurrentPlaceOnMap;
    boolean isItClicked = true;
    int zoom;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static File pictureTaken = null; // i fwtografia pou dialexame i vgalame
    Uri currImageURI;// i dieuthinsi tis stin siskeui
    static String userAddress = "";
    ImageButton ibInfo, ibNewReport, ibProfile;

    // Map Behavior when the Activity starts
    private void onMapActivityStart() {
        mapfragmentMAP.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CurrentPlaceOnMap = new LatLng(37.750002, 26.838570);
        AnimateTheCamera(CurrentPlaceOnMap, 10);

    }

    private void LocationUpdatesFrom() {
        // Initialize Location Manager
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Select way to acquire user position

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

    }

    // Animates the Camera Viewing on the Map


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void AnimateTheCamera(LatLng X, int zoomf) {
        Log.d("method:", "animatethecamera");
        CameraPosition cameraPositiona = new CameraPosition.Builder().target(X)
                .zoom(zoomf).tilt(tiltPercent).bearing(0).build();

        mapfragmentMAP.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPositiona));
    }

    // Places a Marker on the selected Place on Map

    private void PlaceMarkerOnMap() {
        try {
            mapfragmentMAP.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapfragmentMAP.addMarker(new MarkerOptions()
                .position(CurrentPlaceOnMap));
        // .title("User")).showInfoWindow();

    }

    protected void onCreate(Bundle savedInstanceState) {
        zoom = 10;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // getActionBar().setTitle("My Samos");
        getActionBar().setIcon(null);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setBackgroundDrawable(
                getResources().getDrawable(R.drawable.nvbg));
        ivMyLocation = (ImageView) findViewById(R.id.ivMyLocation);
        mapfragmentMAP = ((MapFragment) getFragmentManager().findFragmentById(
                R.id.mapFragment)).getMap();
        ivMyLocation.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                createOptionsDialog();
            }
        });
        LocationUpdatesFrom();
        onMapActivityStart();

        ibInfo = (ImageButton) findViewById(R.id.imageButtonappinfo);
        ibNewReport = (ImageButton) findViewById(R.id.imageButtonnewreport);
        ibProfile = (ImageButton) findViewById(R.id.imageButtonprofile);
        ibInfo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent("com.mysamos.Info"));
            }
        });
        /*
         * ibNewReport.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { startActivity(new
		 * Intent("com.mysamos.NewReport"));
		 * 
		 * } });
		 */
        ibNewReport.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent("com.mysamos.Map"));

            }
        });
        ibProfile.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Session session = Session.getActiveSession();
                if (session != null) {
                    session.closeAndClearTokenInformation();
                }
                Context context = getApplicationContext();
                CharSequence text = "Logged out successfully!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                startActivity(new Intent("com.mysamos.SignInActivity"));
                toast.show();
            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {// otan allazei i
        // topothesia mas
        zoom = 18;
        Log.d("method:", "onlocationchanged");
        location.getLatitude();
        CurrentPlaceOnMap = new LatLng(location.getLatitude(),
                location.getLongitude());
        if (isItClicked) {
            PlaceMarkerOnMap();
            AnimateTheCamera(CurrentPlaceOnMap, zoom);
            userAddress = revergeGeocode(CurrentPlaceOnMap);

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void createOptionsDialog() {// /////////////////////////////////////////////////////////////////
        final String[] options = new String[2];
        options[0] = "Take photo";
        options[1] = "Select photo";

        new AlertDialog.Builder(this)/* .setTitle("Shqiptar:") */
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("PATITHIKE", " " + which);
                        try {
                            if (which == 0) {
                                Intent openNewReport = new Intent("com.mysamos.NewReport");
                                openNewReport.putExtra("MODE","takePicture");
                                startActivity(openNewReport);

                            } else {
                                Intent openNewReport = new Intent("com.mysamos.NewReport");
                                openNewReport.putExtra("MODE","selectPicture");
                                startActivity(openNewReport);
                            }
                        } catch (Exception e) {

                        }


                    }
                }).show();

    }




    static final int REQUEST_TAKE_PHOTO = 1; // gia mia fwto

    private void dispatchTakePictureIntent() {// vgazei fwto
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                // galleryAddPic(photoFile);
                pictureTaken = photoFile;
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }

        }

    }

    String mCurrentPhotoPath;

    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        // galleryAddPic();
        return image;
    }

    private void selectImageFromGallery() {
        // To open up a gallery browser
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                1);

    }


    @SuppressWarnings("static-access")
    public String revergeGeocode(LatLng UserLocation) {// vriskei tin dieuthinsi
        // tou xristi kai tin
        // topothetei stin
        // actionbar pernontas
        // san eisodo ena
        // antikeimeno latlong
        // pou einai h thesh
        // tou
        String toBeReturned = ""; // xristi
        Geocoder gc = new Geocoder(this);
        List<Address> list = null;

        @SuppressWarnings("unused")
        String strNum = null;
        if (gc.isPresent()) {

            try {
                list = gc.getFromLocation(UserLocation.latitude,
                        UserLocation.longitude, 1);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Address address = list.get(0);

            StringBuffer str = new StringBuffer();
            str.append("Address: " + address.getAddressLine(0) + "\n");
            str.append("Sub-Admin Ares: " + address.getSubAdminArea() + "\n");
            str.append("Admin Area: " + address.getAdminArea() + "\n");
            // str.append("Name: " + address.getLocality() + "\n");
            // str.append("Country: " + address.getCountryName() + "\n");
            // str.append("Country Code: " + address.getCountryCode() + "\n");

            String strAddress = str.toString();
            Log.d("eimai sto ", strAddress);
			/*
			 * if (!address.getSubAdminArea().equals(null)) { strNum =
			 * address.getSubAdminArea(); } else { strNum = ""; }
			 */
            getActionBar().setTitle(" " + address.getAddressLine(0));
            toBeReturned = address.getAddressLine(0) + " , "
                    + address.getLocality();
            getActionBar().setIcon(null);
            getActionBar().setDisplayShowHomeEnabled(false);
            // ActivityCompat.invalidateOptionsMenu(this);

        }
        return toBeReturned;
    }

    public static File getTakenPictureFromMapActivity() {
        return pictureTaken;
    }

    public static LatLng getCoordinatesFromMapActivity() {
        return CurrentPlaceOnMap;
    }

    public static String getAddressFromMapActivity() {
        return userAddress;
    }

    // / New shit

    // Var

    ImageView ivThumbnailPhoto;
    Bitmap bitMap;
    static int TAKE_PICTURE = 1;

    public void cameraCheck() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        if (requestCode == TAKE_PICTURE && resultCode == RESULT_OK
                && intent != null) {
            // get bundle
            Bundle extras = intent.getExtras();

            // get bitmap
            bitMap = (Bitmap) extras.get("data");
            ivThumbnailPhoto.setImageBitmap(bitMap);
            Log.e("->", "onactivityresult");
            startActivity(new Intent("com.mysamos.NewReport"));
        }
    }
/*
	// method to check if you have a Camera
	private boolean hasCamera() {
		return getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA);
	}

	// method to check you have Camera Apps
	private boolean hasDefaultCameraApp(String action) {
		final PackageManager packageManager = getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);

		return list.size() > 0;

	}
*/
    // / End of new shit
}












