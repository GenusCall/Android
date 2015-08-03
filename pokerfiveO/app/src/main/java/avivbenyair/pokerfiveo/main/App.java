package avivbenyair.pokerfiveo.main;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;


public class App extends Application {

    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "tTg47SAcrYcDqkN0xZZQrjj3olbFnw9FFqzehn2o", "U5LPrr1NoIvYA17tK8WhjpTRbhg39YDrvFcFK4tp");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}


