package avivbenyair.poker5h.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import avivbenyair.poker5h.R;
import avivbenyair.poker5h.connectivity.GameConnectivity;

/**
 * Created by and-dev on 29/07/15.
 */
public class FragmentMenu extends Fragment implements View.OnClickListener {


    private final static String DEFAULT_SERVER_ADDRESS = "10.0.2.2";
    private final static String DEFAULT_SERVER_PORT = "9933";

    private Button lookForGameBTN;
    private GameConnectivity gameConnectivity;


    public FragmentMenu(GameConnectivity gameConnectivity) {
        this.gameConnectivity = gameConnectivity;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_menu, container, false);


        lookForGameBTN = (Button) v.findViewById(R.id.lookForGameBTN);


        lookForGameBTN.setOnClickListener(this);


        return v;
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lookForGameBTN:
                gameConnectivity.connect(DEFAULT_SERVER_ADDRESS, DEFAULT_SERVER_PORT);
                break;

        }


    }
}
