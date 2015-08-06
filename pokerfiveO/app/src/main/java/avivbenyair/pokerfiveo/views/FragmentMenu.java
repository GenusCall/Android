package avivbenyair.pokerfiveo.views;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParsePush;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import avivbenyair.pokerfiveo.R;
import avivbenyair.pokerfiveo.bricks.Opponent;
import avivbenyair.pokerfiveo.bricks.Player;
import avivbenyair.pokerfiveo.connectivity.GameConnectivity;
import avivbenyair.pokerfiveo.main.MainActivity;


/**
 * Created by and-dev on 29/07/15.
 */
public class FragmentMenu extends Fragment implements View.OnClickListener {


    private final String TAG = "FragmentMenu: ";

    private Button lookForGameBTN;
    private GameConnectivity gameConnectivity;
    private MainActivity mainActivity;

    private TextView menuTitle;

    public FragmentMenu(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_menu, container, false);


        menuTitle = (TextView) v.findViewById(R.id.menuTitle);


        ParsePush.subscribeInBackground(ParseUser.getCurrentUser().getObjectId());
        Log.d(TAG, "subscribe to channel " + ParseUser.getCurrentUser().getObjectId());


        lookForGameBTN = (Button) v.findViewById(R.id.lookForGameBTN);


        lookForGameBTN.setOnClickListener(this);
        gameConnectivity = new GameConnectivity("aa");

        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lookForGameBTN:
                lookForGame();

             /*   Log.d(TAG, "sendApproveRequest has need called");
                JSONObject data = new JSONObject();
                try {
                    data.put("type", "gameRequestApprove");
                    data.put("whosStarts", true);
                    data.put("userName", ParseUser.getCurrentUser().getUsername());
                    data.put("userObjectID", ParseUser.getCurrentUser().getObjectId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ParsePush push = new ParsePush();
                push.setChannel("uLSLpEvjXg");
                push.setData(data);
                try {
                    push.send();
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/

                break;
        }


    }

    private void lookForGame() {
       /* gameConnectivity.lookForRoom();
        mainActivity.showLoadingDialog(true);*/
        getKeyHash();
    }


    public void sendApproveRequest(JSONObject data) throws JSONException {
        Log.d(TAG, "sendApproveRequest was called");

        String opponentUserID = data.getString("userObjectID");
        Random random = new Random();
        int whosStats = random.nextInt(2 - 1 + 1) + 1;
        boolean opponentStarts = false;
        if (whosStats == 1) {
            opponentStarts = false;
            Log.d(TAG,"opponentStarts =false");
        } else {
            Log.d(TAG,"opponentStarts =true");
            opponentStarts = true;
        }

        data.put("whosStarts", !(opponentStarts));
        gameConnectivity.sendApproveRequest(opponentUserID, opponentStarts);

        StartsNewGame(data);

    }

    public void StartsNewGame(JSONObject data) throws JSONException {


        String opponentObjectID = data.getString("userObjectID");
        String opponentUserName = data.getString("userName");
        boolean whosStarts = data.getBoolean("whosStarts");

        Log.d(TAG, "player Starts: " + whosStarts);

        Opponent opponent = new Opponent(opponentObjectID, opponentUserName);
        Player player = new Player(ParseUser.getCurrentUser().getObjectId(), ParseUser.getCurrentUser().getUsername());
        mainActivity.startNewGame(opponent, player, !whosStarts);
    }

    public void joinRoom(String gameID) {

        gameConnectivity.joinRoom(gameID);
    }

    private void getKeyHash(){

        PackageInfo info;
        try {
            info = getActivity().getPackageManager().getPackageInfo("avivbenyair.pokerfiveo", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }

    }


}
