package avivbenyair.poker5h;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.smartfoxserver.v2.exceptions.SFSException;

import java.util.ArrayList;

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

/**
 * Created by and-dev on 29/07/15.
 */
public class GameConnectivity implements IEventListener {


    private final String TAG = this.getClass().getSimpleName();
    private final static boolean DEBUG_SFS = true;
    private final static boolean VERBOSE_MODE = true;
    private final static String DEFAULT_SERVER_ADDRESS = "10.0.2.2";
    private final static String DEFAULT_SERVER_PORT = "9933";


    private SmartFox sfsClient;


    private Context context;
    private Handler handler;

    public GameConnectivity(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        initSmartFox();

    }

    public void connect(String serverIP, String serverPort) {
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
    //////////activities///////////////

    private void loginRoom(String roomName, String userNick) {
        Log.d("login Room", "login Room");

        String zoneName = context.getString(R.string.example_zone);
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


    private void sendCradToOpponent() {
        sfsClient.send(new PublicMessageRequest("Message From Aviv"));

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
}
