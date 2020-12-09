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

import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class SignInActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		/*TextView tos = (TextView) findViewById(R.id.tos);
		tos.setMovementMethod(LinkMovementMethod.getInstance());*/

		Button loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                System.out.println("CLICKED 1");
				onLoginButtonClicked();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	private void onLoginButtonClicked() {
        System.out.println("CLICKED 2");
		List<String> permissions = Arrays.asList("public_profile");
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				if (user != null) {
					if (user.isNew()) {
						// set favorites as null, or mark it as empty somehow
						makeMeRequest();
					} else {
						finishActivity();
					}
				}

			}
		});
	}

	private void makeMeRequest() {
		Session session = ParseFacebookUtils.getSession();
		if (session != null && session.isOpened()) {
			Request request = Request.newMeRequest(
					ParseFacebookUtils.getSession(),
					new Request.GraphUserCallback() {
						@Override
						public void onCompleted(GraphUser user,
								Response response) {
							if (user != null) {
								/*ParseUser.getCurrentUser().put("firstName",
										user.getFirstName());*/
                                ParseUser.getCurrentUser().getParseObject("profile");
                                //ParseUser.getCurrentUser().put("profile", user.getFirstName());
                               /* ParseUser.getCurrentUser().put("profile",
                                        "123a");*/
                               ParseUser.getCurrentUser().put("firstName",
                                        "FirstName: " + user.getFirstName() + "LastName: " + user.getLastName() + "Birthday: " + user.getBirthday() + "Location: "+ user.getLocation());
                                /*ParseUser.getCurrentUser().put("lastName",
                                        user.getLastName());
                                ParseUser.getCurrentUser().put("birthday",
                                        user.getBirthday());
                                ParseUser.getCurrentUser().put("location",
                                        user.getLocation());*/
								ParseUser.getCurrentUser().saveInBackground();
                                try{
                                    Log.e("SHIT M8 encrypted", ParseUser.getCurrentUser().getUsername() + "<>" + ParseUser.getCurrentUser().getEmail() + "<>" + user.getLastName() + "<>" + user.getBirthday() + "<>" + user.getLocation());
                                }catch (Exception e){
                                    //  System.out.println(ParseUser.getCurrentUser().getUsername()+"<>"+ParseUser.getCurrentUser().getEmail());
                                }
								finishActivity();
							} else if (response.getError() != null) {
								if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
										|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
									Toast.makeText(getApplicationContext(),
											R.string.session_invalid_error,
											Toast.LENGTH_LONG).show();

								} else {
									Toast.makeText(getApplicationContext(),
											R.string.logn_generic_error,
											Toast.LENGTH_LONG).show();
								}
							}
						}
					});
			request.executeAsync();

		}
	}

	private void finishActivity() {
		// Start an intent for the dispatch activity
		//Intent intent = new Intent(SignInActivity.this, Map.class);
        Intent intent = new Intent("com.mysamos.Map");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
}
