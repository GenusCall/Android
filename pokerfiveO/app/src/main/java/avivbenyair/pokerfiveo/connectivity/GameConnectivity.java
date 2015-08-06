package avivbenyair.pokerfiveo.connectivity;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import avivbenyair.pokerfiveo.bricks.Move;

/**
 * Created by and-dev on 02/08/15.
 */
public class GameConnectivity {


    private final String GAME_REQUEST_KEY = "gameRequest";
    private final String GAME_REQUEST_APPROVE_KEY = "gameRequestApprove";
    private final String MOVE_KEY = "move";


    private final String TAG = "GameConnectivity: ";
    private String currentGameID;

    private String opponentID;


    public GameConnectivity(String opponentID) {
        this.opponentID = opponentID;
    }


    public void createRoom() {
        final ParseObject newRoom = new ParseObject("Games");
        newRoom.put("open", true);
        newRoom.addAllUnique("players", Arrays.asList(ParseUser.getCurrentUser().getObjectId()));
        newRoom.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {
                    currentGameID = newRoom.getObjectId();

                    Log.d(TAG, "created new room" + currentGameID + ", waiting for 1 more player...");
                } else {
                    Log.d(TAG, "problem with creating new room /n" + e.getMessage());
                }

            }
        });
    }

    public void joinRoom(final String gameID) {
        Log.d(TAG, "joinRoom zzzzzzzzzzzzzzzzzzzzz: " + gameID);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Games");
        query.getInBackground(gameID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    parseObject.put("open", false);
                    parseObject.add("players", ParseUser.getCurrentUser().getObjectId());
                    parseObject.saveInBackground();
                    Log.d(TAG, "joinRoom zzzzzzzzzzzzzzzzzzzzz: " + gameID);
                } else {
                    Log.d(TAG, "joinRoom Error: " + e.getMessage());
                }

            }
        });
    }


    public void lookForRoom() {

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Games");
        query.whereEqualTo("open", true);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                boolean foundMatch = false;

                if (e == null) {
                    if (list.size() > 0) {
                        try {
                            String gameCreator = list.get(0).getJSONArray("players").get(0) + "";
                            currentGameID = list.get(0).getObjectId();
                            sendRequestForGame(gameCreator);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        createRoom();
                    }
                    ;
                } else {

                }

            }
        });
    }

    private void sendRequestForGame(String userObjectID) {
        Log.d(TAG, "sendRequestForGame to " + userObjectID);

        JSONObject data = new JSONObject();
        try {
            data.put("type", GAME_REQUEST_KEY);
            data.put("userName", ParseUser.getCurrentUser().getUsername());
            data.put("userObjectID", ParseUser.getCurrentUser().getObjectId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ParsePush push = new ParsePush();
        push.setChannel(userObjectID);
        push.setData(data);
        try {
            push.send();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    public void sendMoveToOpponent(Move move) throws JSONException {

        int value = move.getCard().getValue();
        int suit = move.getCard().getSuit();
        int positionX = move.getPositionX();

        JSONObject moveJsonObJ = new JSONObject();
        moveJsonObJ.put("type", MOVE_KEY);
        moveJsonObJ.put("value", value);
        moveJsonObJ.put("suit", suit);
        moveJsonObJ.put("positionX", positionX);

        ParsePush push = new ParsePush();
        push.setChannel(opponentID);
        push.setData(moveJsonObJ);
        try {
            push.send();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public void sendApproveRequest(String userObjectID, boolean whosStarts) {
        Log.d(TAG, "sendApproveRequest has need called");
        JSONObject data = new JSONObject();
        try {
            data.put("type", GAME_REQUEST_APPROVE_KEY);
            data.put("whosStarts", whosStarts);
            data.put("userName", ParseUser.getCurrentUser().getUsername());
            data.put("userObjectID", ParseUser.getCurrentUser().getObjectId());
            data.put("gameID", currentGameID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ParsePush push = new ParsePush();
        push.setChannel(userObjectID);
        push.setData(data);
        try {
            push.send();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public void setCurrentGameID(String currentGameID) {
        this.currentGameID = currentGameID;
    }
}
