/**
 * Copyright 2014 Facebook, Inc.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to
 * use, copy, modify, and distribute this software in source code or binary
 * form for use in connection with the web services and APIs provided by
 * Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use
 * of this software is subject to the Facebook Developer Principles and
 * Policies [http://developers.facebook.com/policy/]. This copyright notice
 * shall be included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 */

package com.mysamos;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

import java.security.MessageDigest;

public class DispatchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        // Initialize Parse
        Parse.initialize(this, getString(R.string.parse_application_id),
                getString(R.string.parse_client_key));
        // Initialize Facebook
        String appId = getString(R.string.facebook_app_id);
        ParseFacebookUtils.initialize(appId);
        // Set up Push
       /* PushService.setDefaultPushCallback(this, AlertsActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();*/
		// Check if there is current user info
        try{
            Log.e("SHIT M8",ParseUser.getCurrentUser().getUsername()+"<>"+ParseUser.getCurrentUser().getEmail());
        }catch (Exception e){
          //  System.out.println(ParseUser.getCurrentUser().getUsername()+"<>"+ParseUser.getCurrentUser().getEmail());
        }


		if (ParseUser.getCurrentUser() != null) {
			// Start an intent for the logged in activity
			Log.d("F8Debug", "onCreate, got user,  "
					+ ParseUser.getCurrentUser().getUsername());
			startActivity(new Intent(this, Map.class));
		} else {
			// Start and intent for the logged out activity
			Log.d("F8Debug", "onCreate, no user");
			startActivity(new Intent(this, SignInActivity.class));
		}
	}

}


