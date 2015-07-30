package avivbenyair.poker5h.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import avivbenyair.poker5h.R;
import avivbenyair.poker5h.bricks.Move;
import avivbenyair.poker5h.logic.Card;
import avivbenyair.poker5h.main.MainActivity;

/**
 * Created by and-dev on 30/07/15.
 */
public class FragmentStage extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    private TextView log;
    private MainActivity mainActivity;
    private Button sendMoveBTN;

    public FragmentStage(MainActivity mainActivity) {

        this.mainActivity = mainActivity;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_stage, container, false);

        log = (TextView) v.findViewById(R.id.log);
        sendMoveBTN = (Button) v.findViewById(R.id.sendMoveBTN);
        sendMoveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.setText("sended Move");
                mainActivity.playerNewMove(new Move(new Card(2,3),1,1));
            }
        });

        return v;
    }

    public void opponenMoveHandler(Move move) {

        Log.d(TAG, "opponenMoveHandler has been called");
        log.setText("got new Move, value: "+move.getCard().getValue());
    }


}
