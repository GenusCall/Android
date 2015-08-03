package avivbenyair.pokerfiveo.main;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import avivbenyair.pokerfiveo.R;
import avivbenyair.pokerfiveo.bricks.Game;
import avivbenyair.pokerfiveo.bricks.Opponent;
import avivbenyair.pokerfiveo.bricks.Player;
import avivbenyair.pokerfiveo.views.FragmentMenu;
import avivbenyair.pokerfiveo.views.FragmentStage;


public class MainActivity extends FragmentActivity {


    ///Game Server///
/*    private GameConnectivity gameConnectivity;*/

    private final String TAG = "MainActivity: ";

    ///UI///
    private Handler handler;
    private FragmentManager fm;

    boolean loadingDialogOn;
    private ProgressDialog progress;
    private SharedInstances sharedInstances;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedInstances = SharedInstances.ParameterData();

        initUI();

        /*gameConnectivity = new GameConnectivity(this, handler);*/

        showMenuScreen();


    }

    private void initUI() {
        handler = new Handler();
        loadingDialogOn = false;
        fm = getSupportFragmentManager();


    }

    public void showStageScreen(Game game) {

        FragmentStage newfragmentStage = new FragmentStage(this, game);
        sharedInstances.setFragmentStage(newfragmentStage);

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.mainFrame, newfragmentStage);
        ft.commit();

        Log.d(TAG, "showStageScreen was called");
    }


    public void showMenuScreen() {
        Log.d(TAG, "showMenuScreen was called");
        FragmentMenu fragmentMenu = new FragmentMenu(this);
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.mainFrame, fragmentMenu);
        ft.commit();
        sharedInstances.setFragmentMenu(fragmentMenu);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
/*        gameConnectivity.disconnect();*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    public void showLoadingDialog(boolean on) {
        if (on) {
            if (!loadingDialogOn) {
                loadingDialogOn = true;
                progress = ProgressDialog.show(this, "Loading",
                        "Looking for worthy opponent", true);
                progress.setCancelable(false);
            }
        } else {
            if (loadingDialogOn) {
                loadingDialogOn = false;
                progress.dismiss();
            }
        }

    }


    public void startNewGame(Opponent opponent, Player player, boolean playerStarts) {
        showLoadingDialog(false);
        Game game = new Game(playerStarts, opponent, player);
        sharedInstances.setInMatch(true);
        showStageScreen(game);
    }

    public void endGame() {


    }
}
