package avivbenyair.pokerfiveo.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import avivbenyair.pokerfiveo.R;


public class LogIn extends Activity {

    final String TAG = "LogInActivity: ";
    private Button login;
    private Button login2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        if (ParseUser.getCurrentUser() != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        login = (Button) findViewById(R.id.login);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseUser.logInInBackground("aviv", "159753", new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e == null) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                });
            }
        });

        login2 = (Button) findViewById(R.id.login2);


        login2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseUser.logInInBackground("aviv2", "159753", new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e == null) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                });
            }
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }


    ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {

        @Override
        public void done(ParseUser user, ParseException err) {
            if (user == null) {
                Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
            } else if (user.isNew()) {
                Log.d("MyApp", "User signed up and logged in through Facebook!");
            } else {
                Log.d("MyApp", "User logged in through Facebook!");
            }
        }
    });



}
