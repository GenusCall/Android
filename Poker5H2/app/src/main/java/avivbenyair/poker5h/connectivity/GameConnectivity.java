package avivbenyair.poker5h.connectivity;

import android.os.Handler;
import android.util.Log;

import com.smartfoxserver.v2.exceptions.SFSException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import avivbenyair.poker5h.R;
import avivbenyair.poker5h.bricks.Move;
import avivbenyair.poker5h.logic.Card;
import avivbenyair.poker5h.main.MainActivity;
import avivbenyair.poker5h.views.FragmentStage;
import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.entities.Room;
import sfs2x.client.entities.SFSUser;
import sfs2x.client.requests.CreateRoomRequest;
import sfs2x.client.requests.JoinRoomRequest;
import sfs2x.client.requests.LoginRequest;
import sfs2x.client.requests.PublicMessageRequest;
import sfs2x.client.requests.RoomSettings;

/**
 * Created by and-dev on 29/07/15.
 */
public class GameConnectivity implements IEventListener {


    private FragmentStage fragmentStage;


    private final String TAG = this.getClass().getSimpleName();
    private final static boolean DEBUG_SFS = true;
    private final static boolean VERBOSE_MODE = true;
    private final static String DEFAULT_SERVER_ADDRESS = "10.0.2.2";
    private final static String DEFAULT_SERVER_PORT = "9933";


    private SmartFox sfsClient;


    private MainActivity mainActivity;
    private Handler handler;

    private int gameUserID;

    public GameConnectivity(MainActivity mainActivity, Handler handler) {
        this.mainActivity = mainActivity;
        this.handler = handler;
        initSmartFox();


        getGameUserID();

    }

    private void getGameUserID() {

        Random rand = new Random();
        int randomNum = rand.nextInt((10000 - 1) + 1) + 1;

        gameUserID = randomNum;

    }

    public void connect(String serverIP, String serverPort) {

        mainActivity.showLoadingDialog(true);

        // if the user have entered port number it uses it...
        if (serverPort.length() > 0) {
            int serverPortValue = Integer.parseInt(serverPort);
            sfsClient.connect(serverIP, serverPortValue);
        }
        // ...otherwise uses the default port number
        else {
            sfsClient.connect(serverIP);
        }
    }


    private void initSmartFox() {

        // Instantiate SmartFox client
        sfsClient = new SmartFox(DEBUG_SFS);

        // Add event listeners
        sfsClient.addEventListener(SFSEvent.CONNECTION, this);
        sfsClient.addEventListener(SFSEvent.CONNECTION_LOST, this);
        sfsClient.addEventListener(SFSEvent.LOGIN, this);
        sfsClient.addEventListener(SFSEvent.LOGIN_ERROR, this);
        sfsClient.addEventListener(SFSEvent.ROOM_JOIN, this);
        sfsClient.addEventListener(SFSEvent.HANDSHAKE, this);
        sfsClient.addEventListener(SFSEvent.SOCKET_ERROR, this);
        sfsClient.addEventListener(SFSEvent.USER_ENTER_ROOM, this);
        sfsClient.addEventListener(SFSEvent.USER_EXIT_ROOM, this);
        sfsClient.addEventListener(SFSEvent.PUBLIC_MESSAGE, this);
        sfsClient.addEventListener(SFSEvent.ROOM_ADD, this);
        sfsClient.addEventListener(SFSEvent.USER_COUNT_CHANGE, this);
        sfsClient.addEventListener(SFSEvent.ROOM_CREATION_ERROR, this);
        sfsClient.enableLagMonitor(false);

    }


    private void CheckForRooms() {

        Log.v(TAG, "CheckForRooms CheckForRooms CheckForRooms CheckForRooms");


        ArrayList<Room> rooms = new ArrayList<Room>();

        for (int i = 0; i < sfsClient.getRoomList().size(); i++) {
            if (sfsClient.getRoomList().get(i).getUserCount() != 2) {
                rooms.add(sfsClient.getRoomList().get(i));
            }
        }

        if (rooms.size() != 0) {

            Log.d("found " + rooms.size() + " Rooms", "NO ROOMS, creating new room");
            sfsClient.send(new JoinRoomRequest(rooms.get(0).getName()));


        } else {
            Log.d("No Rooms", "NO ROOMS, creating new room");
            createNewRoom();

        }


    }
    //////////activities///////////////

    private void loginRoom(String roomName, String userNick) {
        Log.d("login Room", "login Room");

        String zoneName = mainActivity.getApplicationContext().getString(R.string.example_zone);
        sfsClient.send(new JoinRoomRequest(roomName));


    }


    private void createNewRoom() {

        sfsClient.addEventListener(SFSEvent.ROOM_ADD, new IEventListener() {
            public void dispatch(BaseEvent evt) throws SFSException {
                if ((Room) evt.getArguments().get("room") != null) {
                    System.out.println("Room created: " + (Room) evt.getArguments().get("room"));

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loginRoom("game1", "Aviv");

                        }
                    }, 500);


                }

            }
        });


        // Define the settings of a new chat Room
        RoomSettings settings = new RoomSettings("game1");
        settings.setMaxUsers(2);


        // Create the Room
        sfsClient.send(new CreateRoomRequest(settings));
    }


    /**
     * Disconnect the client from the server
     */
    public void disconnect() {
        if (VERBOSE_MODE) Log.v(TAG, "Disconnecting");
        if (sfsClient.isConnected()) {
            if (VERBOSE_MODE) Log.v(TAG, "Disconnect: Disconnecting client");
            sfsClient.disconnect();
            if (VERBOSE_MODE) Log.v(TAG, "Disconnect: Disconnected ? " + !sfsClient.isConnected());
        }
        sfsClient.removeAllEventListeners();
    }

    ////////////////////////


    @Override
    public void dispatch(final BaseEvent event) throws SFSException {

        if (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION)) {
            Random rand = new Random();
            int randomNum = rand.nextInt((100 - 1) + 1) + 1;
            sfsClient.send(new LoginRequest("zz" + randomNum, "", "BasicExamples"));

        } else if (event.getType().equalsIgnoreCase(SFSEvent.ROOM_CREATION_ERROR)) {
            System.out.println("Room creation failure: " + event.getArguments().get("errorMessage"));

        } else if (event.getType().equalsIgnoreCase(SFSEvent.ROOM_JOIN)) {
            Room room = ((Room) event.getArguments().get("room"));
            if (room.getUserCount() == 2) {
                mainActivity.showLoadingDialog(false);
                mainActivity.showStageScreen();
            } else {
                System.out.println("Just 1 user at the moment, waiting for 1 more");
            }

        } else if (event.getType().equalsIgnoreCase(SFSEvent.USER_COUNT_CHANGE)) {
            if (((Room) event.getArguments().get("room")).getUserCount() == 2) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        mainActivity.showLoadingDialog(false);
                        mainActivity.showStageScreen();
                    }
                });
            }

        } else if (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION_LOST)) {
            disconnect();
            mainActivity.showLoadingDialog(false);

        } else if (event.getType().equalsIgnoreCase(SFSEvent.LOGIN)) {
            CheckForRooms();

        } else if (event.getType().equalsIgnoreCase(SFSEvent.LOGIN_ERROR)) {
            Log.d("Login errorMessage: ", event.getArguments().get("errorMessage").toString());
        } else if (event.getType().equals(SFSEvent.PUBLIC_MESSAGE)) {

            Object temp1 = event.getArguments();
            Object temp2 = event.getArguments().get("sender");
            SFSUser temp2User = (SFSUser)(temp2);

                Log.d("2222222"+temp2User.isItMe(),"temp2: "+temp2.getClass().getName());




            String senderStr = event.getArguments().get("sender").toString();

            Log.d("getMySelf: ", sfsClient.getMySelf().getId() + "zzzzzzzzzz999999999999999999999999999999999999999999999zzz");
            Log.d("senderStrqqqqqq: ", senderStr + "zzzzzzzz9999999999999999999999999999999999999999999zzzz");


            if (!(senderStr.equalsIgnoreCase(sfsClient.getMySelf().getId() + ""))) {

              /*  Log.d("new Message= ", " sender: " + senderStr + ", message: " + event.getArguments().get("message").toString());*/
                String messageJsonStr = event.getArguments().get("message").toString();

                try {
                    JSONObject jsonObject = new JSONObject(messageJsonStr);
                    String type = jsonObject.getString("type");


                    if (type.equalsIgnoreCase("move")) {

                        int opponent = jsonObject.getInt("userid");
                        int value = jsonObject.getInt("value");
                        int suit = jsonObject.getInt("suit");
                        int hand = jsonObject.getInt("hand");
                        int position = jsonObject.getInt("position");

                        final Move move = new Move(new Card(value, suit), hand, position);
                        if (gameUserID != opponent) {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mainActivity.opponentNewMove(move);
                                }
                            });

                        }


                    } else {

                        Log.d(TAG, " Error: message has arraived but with no clear 'type'");

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                // If my id and the sender id are different is a incoming
                // message
            /*message.setIncomingMessage(senderStr != sfsClient.getMySelf().getId());*/


            } else {

                Log.d("gggggggggggg", "senderStr.equalsIgnoreCase(sfsClient.getMySelf().getId() + \"\")");
            }

/*

            Log.d("new Message= ", " sender: " + senderStr + ", message: " + event.getArguments().get("message").toString());
            String messageJsonStr = event.getArguments().get("message").toString();

            try {
                JSONObject jsonObject = new JSONObject(messageJsonStr);
                String type = jsonObject.getString("type");


                if (type.equalsIgnoreCase("move")) {

                    int value = jsonObject.getInt("value");
                    int suit = jsonObject.getInt("suit");
                    int hand = jsonObject.getInt("hand");
                    int position = jsonObject.getInt("position");

                    Move move = new Move(new Card(value, suit), hand, position);
                    mainActivity.opponentNewMove(move);
                } else {

                    Log.d(TAG, " Error: message has arraived but with no clear 'type'");

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
*/


            // If my id and the sender id are different is a incoming
            // message
            /*message.setIncomingMessage(senderStr != sfsClient.getMySelf().getId());*/


        }
    }


    public void playerMoveHandler(Move move) {
        Log.d(TAG, "playerMoveHandler has been called");
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("type", "move");
            jsonObj.put("gameUserID", gameUserID);
            jsonObj.put("value", move.getCard().getValue());
            jsonObj.put("suit", move.getCard().getSuit());
            jsonObj.put("hand", move.getHand());
            jsonObj.put("position", move.getPosition());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sfsClient.send(new PublicMessageRequest(jsonObj.toString()));

    }
}
