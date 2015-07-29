package avivbenyair.poker5h;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.smartfoxserver.v2.exceptions.SFSException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.entities.Room;
import sfs2x.client.requests.CreateRoomRequest;
import sfs2x.client.requests.JoinRoomRequest;
import sfs2x.client.requests.LoginRequest;
import sfs2x.client.requests.PublicMessageRequest;
import sfs2x.client.requests.RoomSettings;


public class ActivityConnection extends FragmentActivity implements IEventListener {


    /////////server params///////////

    private final String TAG = this.getClass().getSimpleName();

    private final static boolean DEBUG_SFS = true;
    private final static boolean VERBOSE_MODE = true;

    private final static String DEFAULT_SERVER_ADDRESS = "10.0.2.2";
    private final static String DEFAULT_SERVER_PORT = "9933";
    private final static SimpleDateFormat logDateFormater = new SimpleDateFormat("h:mm:ss",
            Locale.US);
    private final static int COLOR_GREEN = Color.parseColor("#99FF99");
    private final static int COLOR_BLUE = Color.parseColor("#99CCFF");
    private final static int COLOR_GRAY = Color.parseColor("#cccccc");
    private final static int COLOR_RED = Color.parseColor("#FF0000");
    private final static int COLOR_ORANGE = Color.parseColor("#f4aa0b");


    private enum Status {
        DISCONNECTED, CONNECTED, CONNECTING, CONNECTION_ERROR, CONNECTION_LOST, LOGGED, IN_A_ROOM
    }

    Status currentStatus = null;

    SmartFox sfsClient;

//////////////////UI//////////////////////////////////

    private Button connectBTN, sendMessage;

    private Handler handler;

    //////////////Windows////////////////

    boolean loadingDialogOn;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_menu);

        handler = new Handler();
        loadingDialogOn = false;
        initSmartFox();
        //initUI();

        connectBTN = (Button) findViewById(R.id.connectBTN);
        sendMessage = (Button) findViewById(R.id.send);

        connectBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                connect(DEFAULT_SERVER_ADDRESS, DEFAULT_SERVER_PORT);


            }
        });
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*sendMessage();*/
                createNewRoom();
            }
        });


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
        sfsClient.addEventListener(SFSEvent.ROOM_CREATION_ERROR, this);
        sfsClient.enableLagMonitor(false);


        if (VERBOSE_MODE)
            Log.v(TAG, "SmartFox created:" + sfsClient.isConnected() + " BlueBox enabled="
                    + sfsClient.useBlueBox());
    }

    @Override
    public void dispatch(final BaseEvent event) throws SFSException {
        if (VERBOSE_MODE)
            Log.v(TAG, "Dispatching " + event.getType() + " (arguments=" + event.getArguments()
                    + ")");
        if (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION)) {
            if (event.getArguments().get("success").equals(true)) {
                // Login as guest in current zone
                sfsClient.send(new LoginRequest("zz", "", "BasicExamples"));



             /*   loginRoom();*/

            } else {
                Log.d("Connection success: ", event.getArguments().get("success").toString());
            }
        } else if (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION_LOST)) {

            disconnect();
        } else if (event.getType().equalsIgnoreCase(SFSEvent.LOGIN)) {
   /*         setStatus(Status.LOGGED, sfsClient.getCurrentZone());
            sfsClient.send(new JoinRoomRequest(getString(R.string.example_lobby)));*/



      /*      sfsClient.send(new JoinRoomRequest(getString(R.string.example_lobby)));
            Log.d("Login into: ",event.getArguments().get("name").toString());*/
            CheckForRooms();

        } else if (event.getType().equalsIgnoreCase(SFSEvent.LOGIN_ERROR)) {
   /*         setStatus(Status.LOGGED, sfsClient.getCurrentZone());
            sfsClient.send(new JoinRoomRequest(getString(R.string.example_lobby)));*/

            Log.d("Login errorMessage: ", event.getArguments().get("errorMessage").toString());


        } /*else if (event.getType().equalsIgnoreCase(SFSEvent.ROOM_JOIN)) {
            Log.d("SFSEvent.ROOM_JOIN: ", sfsClient.getLastJoinedRoom().getName());
        }*/ else if (event.getType().equals(SFSEvent.PUBLIC_MESSAGE)) {

            String senderStr = event.getArguments().get("sender").toString();
            Log.d("Message= ", " sender: " + senderStr + ", message: " + event.getArguments().get("message").toString());
            // If my id and the sender id are different is a incoming
            // message
            /*message.setIncomingMessage(senderStr != sfsClient.getMySelf().getId());*/


        }
    }


    // sfsClient.send(new JoinRoomRequest(sfsClient.getRoomList().get(i).getName()));
    private void CheckForRooms() {


        ArrayList<Room> rooms = new ArrayList<Room>();

        for (int i = 0; i < sfsClient.getRoomList().size(); i++) {
            if (sfsClient.getRoomList().get(i).getUserCount() != 2) {
                rooms.add(sfsClient.getRoomList().get(i));
            }
        }

        if (rooms.size() != 0) {

            sfsClient.addEventListener(SFSEvent.ROOM_JOIN, new IEventListener() {
                public void dispatch(BaseEvent evt) throws SFSException {
                    System.out.println("Room joined successfully: " + (Room) evt.getArguments().get("room"));
                }
            });
            sfsClient.addEventListener(SFSEvent.ROOM_JOIN, new IEventListener() {
                public void dispatch(BaseEvent evt) throws SFSException {
                    System.out.println("Room joining failed: " + evt.getArguments().get("errorMessage"));
                }
            });


            sfsClient.send(new JoinRoomRequest(rooms.get(0).getName()));


        } else {
            Log.d("No Rooms", "NO ROOMS, creating new room");
            createNewRoom();

        }


    }

    private void loginRoom(String roomName, String userNick) {
        Log.d("login Room", "login Room");

        String zoneName = getString(R.string.example_zone);
        if (VERBOSE_MODE) Log.v(TAG, "Login as '" + userNick + "' into " + zoneName);

        sfsClient.send(new JoinRoomRequest(roomName));

        sfsClient.addEventListener(SFSEvent.ROOM_JOIN, new IEventListener() {
            public void dispatch(BaseEvent evt) throws SFSException {
                System.out.println("succsses: " + evt.getArguments().get("room"));
            }
        });


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

        sfsClient.addEventListener(SFSEvent.ROOM_CREATION_ERROR, new IEventListener() {
            public void dispatch(BaseEvent evt) throws SFSException {
                if (evt.getArguments().get("errorMessage") != null) {
                    System.out.println("Room creation failure: " + evt.getArguments().get("errorMessage"));
                }

            }
        });

        // Define the settings of a new chat Room
        RoomSettings settings = new RoomSettings("game1");
        settings.setMaxUsers(2);


        // Create the Room
        sfsClient.send(new CreateRoomRequest(settings));





/*        var settings:RoomSettings = new RoomSettings("Piggy's Chat Room")
        settings.maxUsers = 40
        settings.groupId = "ChatGroup"

        smartFox.send(new CreateRoomRequest(settings)*//*
        RoomSettings roomSettings = new RoomSettings("Aviv1");
        roomSettings.setGroupId("aviv1");
        roomSettings.setMaxUsers(2);
        roomSettings.setGame(true);
        sfsClient.send(new CreateRoomRequest(roomSettings));*/


    }


    private void sendMessage() {
        sfsClient.send(new PublicMessageRequest("Message From Aviv"));

    }


    private void connect(String serverIP, String serverPort) {
        // if the user have entered port number it uses it...
        if (serverPort.length() > 0) {
            int serverPortValue = Integer.parseInt(serverPort);
            if (VERBOSE_MODE) Log.v(TAG, "Connecting to " + serverIP + ":" + serverPort);
            sfsClient.connect(serverIP, serverPortValue);
        }
        // ...otherwise uses the default port number
        else {
            if (VERBOSE_MODE) Log.v(TAG, "Connecting to " + serverIP);
            sfsClient.connect(serverIP);
        }
    }

    /**
     * Frees the resources.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
        if (VERBOSE_MODE) Log.v(TAG, "Removing Listeners");
        sfsClient.removeAllEventListeners();
    }

    /**
     * Disconnect the client from the server
     */
    private void disconnect() {
        if (VERBOSE_MODE) Log.v(TAG, "Disconnecting");

        if (sfsClient.isConnected()) {
            if (VERBOSE_MODE) Log.v(TAG, "Disconnect: Disconnecting client");
            sfsClient.disconnect();
            if (VERBOSE_MODE) Log.v(TAG, "Disconnect: Disconnected ? " + !sfsClient.isConnected());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_connection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    public void loadingShow() {
        if (!loadingDialogOn) {
            loadingDialogOn=true;
            progress = ProgressDialog.show(this, "Loading",
                    "Looking for worthy opponent", true);
            progress.setCancelable(false);
        } else {
            loadingDialogOn=false;
            progress.dismiss();
        }
    }


}























/*




    */
/**
 * Update the current status, the status message and log the change
 *
 * @param status
 * @param params
 *//*

    private void setStatus(Status status, String... params) {
        if (status == currentStatus) {
            // If there is no status change ignore it
            return;
        }

        if (VERBOSE_MODE) Log.v(TAG, "New status= " + status);
        currentStatus = status;
        final String message;
        final int messageColor;
        final boolean connectButtonEnabled, disconnectButtonEnabled;
        switch (status) {
            case CONNECTING:
                message = getString(R.string.connecting);
                messageColor = COLOR_BLUE;
                connectButtonEnabled = false;
                disconnectButtonEnabled = true;
                break;
            case DISCONNECTED:
                message = getString(R.string.disconnected);
                messageColor = COLOR_GRAY;
                connectButtonEnabled = true;
                disconnectButtonEnabled = false;
                break;
            case CONNECTION_ERROR:
                message = getString(R.string.connection_error);
                messageColor = COLOR_RED;
                connectButtonEnabled = true;
                disconnectButtonEnabled = false;
                break;
            case CONNECTED:
                message = getString(R.string.connected) + ": " + params[0];
                messageColor = COLOR_GREEN;
                connectButtonEnabled = false;
                disconnectButtonEnabled = true;
                break;
            case CONNECTION_LOST:
                message = getString(R.string.connection_lost);
                messageColor = COLOR_ORANGE;
                connectButtonEnabled = true;
                disconnectButtonEnabled = false;
                break;
            case LOGGED:
                message = getString(R.string.logged_into) + "'" + params[0] */
/*
                                                                         * zone name
																		 *//*

                        + "' zone";
                messageColor = COLOR_GREEN;
                connectButtonEnabled = false;
                disconnectButtonEnabled = true;
                break;
            case IN_A_ROOM:
                message = getString(R.string.joined_to_room) + params[0] */
/* room name *//*

                        + "'";
                messageColor = COLOR_GREEN;
                connectButtonEnabled = false;
                disconnectButtonEnabled = true;
                break;
            default:
                connectButtonEnabled = true;
                disconnectButtonEnabled = true;
                messageColor = 0;
                message = null;
        }
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                Log.d("Message", message);
                //buttonConnect.setEnabled(connectButtonEnabled);
                // buttonDisconnect.setEnabled(disconnectButtonEnabled);
            }
        });

    }*/
