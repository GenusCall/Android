package avivbenyair.pokerfiveo.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONException;

import avivbenyair.pokerfiveo.R;
import avivbenyair.pokerfiveo.bricks.Game;
import avivbenyair.pokerfiveo.bricks.Move;
import avivbenyair.pokerfiveo.connectivity.GameConnectivity;
import avivbenyair.pokerfiveo.logic.Card;
import avivbenyair.pokerfiveo.main.MainActivity;


/**
 * Created by and-dev on 30/07/15.
 */
public class FragmentStage extends Fragment {

    private final String TAG = this.getClass().getSimpleName();
    private MainActivity mainActivity;
    private GameConnectivity gameConnectivity;

    //////debug junk//////
    private Button sendMoveBTN;

    private RadioButton playerTurn;
    private TextView playerCardsTXT, opponentCardsTXT, cardLeft;
    private Game game;


    public FragmentStage(MainActivity mainActivity, Game game) {

        this.game = game;
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

        playerCardsTXT = (TextView) v.findViewById(R.id.playerCards);
        opponentCardsTXT = (TextView) v.findViewById(R.id.opponentCards);
        cardLeft = (TextView) v.findViewById(R.id.cardLeft);

        sendMoveBTN = (Button) v.findViewById(R.id.sendMoveBTN);


        gameConnectivity = new GameConnectivity(game.getOpponent().getObjectID());


        sendMoveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                turnHandler(false);
                Card card = drawCard();
                Move move = new Move(card, 1);
                actMove(move);
            }
        });


        turnHandler(game.isPlayerStarts());


        return v;
    }

    private void turnHandler(boolean isPlayerTurn) {

        if(isPlayerTurn){
            enableButtons(true);

        }else{
            enableButtons(false);
        }

    }


    public void enableButtons(boolean onOff) {
        sendMoveBTN.setEnabled(onOff);
    }

    public void actMove(Move move) {
        addUsedCard(true, move);
    }

    private void sendMoveToOpponent(Move move) {
        try {
            gameConnectivity.sendMoveToOpponent(move);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void updateCardLeft(Card card) {
        game.removeCardFromDeck(card);
        cardLeft.setText("Cards Left: " + game.getDeck().getSize());
    }

    private Card drawCard() {
        Card card = game.drawNewCard();
        return card;
    }

    private void addUsedCard(boolean isPlayer, Move move) {
        Log.d(TAG, "addUsedCard has been called on card: " + move.getCard().getValue());

        if (isPlayer) {
            sendMoveToOpponent(move);
            game.getPlayer().insertToPlayerCards(move);
            playerCardsTXT.append("(CARD value:" + move.getCard().getValue() + " suit:" + move.getCard().getSuit() + " ) ");

            if(game.getDeck().getSize()<=12){

            }else if(game.getDeck().getSize()<=2&&game.getDeck().getSize()!=0){

            }

        } else {

            game.getOpponent().insertToOpponentCards(move);
            opponentCardsTXT.append("(CARD value:" + move.getCard().getValue() + " suit:" + move.getCard().getSuit() + " ) ");

            if(game.getDeck().getSize()<=12&&game.getDeck().getSize()>2){

            }else if(game.getDeck().getSize()<=2&&game.getDeck().getSize()!=0){

            }


        }

        updateCardLeft(move.getCard());


        if(game.getDeck().getSize()==0){
            endGame();
        }




    }

    private void endGame() {


    }


    public void opponenMoveHandler(Move move) {
        addUsedCard(false, move);
        turnHandler(true);
    }


}
