package avivbenyair.pokerfiveo.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import avivbenyair.pokerfiveo.bricks.Move;
import avivbenyair.pokerfiveo.logic.Card;
import avivbenyair.pokerfiveo.main.SharedInstances;

/**
 * Created by and-dev on 02/08/15.
 */
public class GcmBroadcastReceiverPoker extends BroadcastReceiver {

    private final String TAG = "GcmBroadcastReceiverPoker";
    public static final String PARSE_DATA_KEY = "com.parse.Data";
    private final String PUSH_RECEIVED = "com.parse.push.intent.RECEIVE";

    private final String GAME_REQUEST_KEY = "gameRequest";
    private final String GAME_REQUEST_APPROVE_KEY = "gameRequestApprove";
    private final String MOVE_KEY = "move";

    private String pushType;

    private SharedInstances sharedInstances;


    @Override
    public void onReceive(Context context, Intent intent) {

        sharedInstances = SharedInstances.ParameterData();
        String intentAction = intent.getAction();

        if (intentAction.equals(PUSH_RECEIVED)) {


            JSONObject data = getDataFromIntent(intent);
            String pushType = null;
            try {
                pushType = data.getString("type");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "push data: " + intentAction);


            if (sharedInstances.isInMatch()) {
                Log.d(TAG, "isInMatch=  true, type: " + pushType);
                if (pushType.equals(MOVE_KEY)) {
                    Move move = null;
                    try {
                        int value = data.getInt("value");
                        int suit = data.getInt("suit");
                        int positionX = data.getInt("positionX");

                        move = new Move(new Card(value, suit), positionX);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    sharedInstances.getFragmentStage().opponenMoveHandler(move);
                }

            } else if (!(sharedInstances.isInMatch())) {
                Log.d(TAG, "isInMatch=  false, type: " + pushType);
                if (pushType.equals(GAME_REQUEST_KEY)) {
                    Log.d(TAG, "GAME_REQUEST_KEY");
                    gameRequestHandler(data);

                } else if (pushType.equals(GAME_REQUEST_APPROVE_KEY)) {
                    Log.d(TAG, "GAME_REQUEST_APPROVE_KEYzzzzzzzzzzzzzzzzzzzzz");
                    gameRequestApproveHandler(data);

                }
            }


        }


    }

    private void gameRequestApproveHandler(JSONObject data) {
        Log.d(TAG, "gameRequestApproveHandlerzzzzzzzzzzzzzzzzzzzzz");
        try {
            sharedInstances.getFragmentMenu().joinRoom(data.getString("gameID"));
            Log.d(TAG, "gameRequestApproveHandler was called | userObjectID: " + data.getString("userObjectID"));
            sharedInstances.getFragmentMenu().StartsNewGame(data);
        } catch (JSONException e) {
            e.printStackTrace();

        }


    }

    private void gameRequestHandler(JSONObject data) {
        Log.d(TAG, "gameRequestHandler was called");
        try {
            sendApproveRequest(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void sendApproveRequest(JSONObject data) throws JSONException {
        Log.d(TAG, "sendApproveRequest was called");
        String userObjectID = data.getString("userObjectID");
        sharedInstances.getFragmentMenu().sendApproveRequest(data);
    }


    private JSONObject getDataFromIntent(Intent intent) {
        JSONObject data = null;
        try {
            data = new JSONObject(intent.getExtras().getString(PARSE_DATA_KEY));
        } catch (JSONException e) {
            // Json was not readable...
        }
        return data;
    }
}
