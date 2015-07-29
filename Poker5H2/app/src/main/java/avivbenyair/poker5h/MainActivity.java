package avivbenyair.poker5h;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends FragmentActivity {


    ///Game Server///
    private GameConnectivity gameConnectivity;


    ///UI///
    private Handler handler;
    private FragmentManager fm;

    boolean loadingDialogOn;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        gameConnectivity = new GameConnectivity(getApplicationContext(), handler);
        
        showMenuScreen();


    }

    private void initUI() {
        handler = new Handler();
        loadingDialogOn = false;
        fm = getSupportFragmentManager();
    }

    private void showMenuScreen() {
        FragmentMenu fragmentMenu = new FragmentMenu(gameConnectivity);
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.mainFrame, fragmentMenu);
        ft.commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameConnectivity.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_connection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void loadingShow() {
        if (!loadingDialogOn) {
            loadingDialogOn = true;
            progress = ProgressDialog.show(this, "Loading",
                    "Looking for worthy opponent", true);
            progress.setCancelable(false);
        } else {
            loadingDialogOn = false;
            progress.dismiss();
        }
    }


}
