package com.mysamos;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;

import com.facebook.Session;
import com.parse.Parse;
import com.parse.ParseUser;

public class Info extends Activity {
	ImageView ivInfo, ivNewReport, ivProfile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.abs_layout_info);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.nvbg));
		ivInfo = (ImageView) findViewById(R.id.imageView2);
		ivNewReport = (ImageView) findViewById(R.id.imageButton1);
		ivProfile = (ImageView) findViewById(R.id.imageView1);

		ivProfile.setOnClickListener(new OnClickListener() {

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

		ivNewReport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent("com.mysamos.Map"));

			}
		});

		ivInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent("com.mysamos.Info"));

			}
		});

	}

/*	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        Log.d(this.getClass().getName(), "back button pressed");
	        moveTaskToBack(true);
	    }
	    return super.onKeyDown(keyCode, event);
	}*/
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.finish();
	}

}
