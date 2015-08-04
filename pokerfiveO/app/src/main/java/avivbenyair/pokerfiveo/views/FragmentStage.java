package avivbenyair.pokerfiveo.views;

import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;

import avivbenyair.pokerfiveo.R;
import avivbenyair.pokerfiveo.bricks.Game;
import avivbenyair.pokerfiveo.bricks.Move;
import avivbenyair.pokerfiveo.connectivity.GameConnectivity;
import avivbenyair.pokerfiveo.logic.Card;
import avivbenyair.pokerfiveo.logic.Hand;
import avivbenyair.pokerfiveo.logic.PokerLogic;
import avivbenyair.pokerfiveo.logic.TooFewCardsException;
import avivbenyair.pokerfiveo.logic.TooManyCardsException;
import avivbenyair.pokerfiveo.main.MainActivity;


/**
 * Created by and-dev on 30/07/15.
 */
public class FragmentStage extends Fragment {

    private final String TAG = this.getClass().getSimpleName();
    private MainActivity mainActivity;
    private GameConnectivity gameConnectivity;
    private Handler handler;

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

        handler = new Handler();
        gameConnectivity = new GameConnectivity(game.getOpponent().getObjectID());


        sendMoveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (game.getDeck().getSize() > 0) {
                    turnHandler(false);
                    Card card = drawCard();


                    Move move = new Move(card, getValidHand());
                    actMove(move);

                } else {
                    try {
                        endGame();
                    } catch (TooManyCardsException e) {
                        e.printStackTrace();
                    } catch (TooFewCardsException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


        turnHandler(game.isPlayerStarts());


        return v;
    }

    private int getValidHand() {
        Log.d(TAG, "getValidHand getValidHand: getValidHand " + "getValidHand");
        boolean fullRow = true;
        int x = 0;
        for (int i = 0; i < game.getPlayer().getCards().size(); i++) {

            if (game.getPlayer().getCards().get(i).size() == game.getHandPositionY()) {
                x = i;
                Log.d(TAG, "game.getPlayer().getCards().get(i).size(): " + game.getPlayer().getCards().get(i).size());
                i = game.getPlayer().getCards().size();
                fullRow = false;

            }

            if (fullRow) {
                Log.d(TAG, "fullRow fullRow: " + true);
                game.setHandPositionY(game.getHandPositionY() + 1);
                return getValidHand();
            }

        }


        Log.d(TAG, "game.getHandPositionY(): " + game.getHandPositionY());
        Log.d(TAG, "Hand number: " + x);
        Log.d(TAG, "Hands number: " + game.getPlayer().getCards().size());
        return x;

    }

    private void putCardInDeck(boolean isPlayer, Card card) {

        boolean fullRow = true;

        if (isPlayer) {
            for (int i = 0; i < game.getPlayer().getCards().size(); i++) {

                if (game.getPlayer().getCards().get(i).size() == game.getHandPositionY()) {
                    game.getPlayer().getCards().get(i).add(card);
                    i = game.getPlayer().getCards().size();
                    fullRow = false;
                }

                if (fullRow) {
                    game.setHandPositionY(game.getHandPositionY() + 1);
                    putCardInDeck(isPlayer, card);
                }

            }
        } else {


            for (int i = 0; i < game.getOpponent().getCards().size(); i++) {

                if (game.getOpponent().getCards().get(i).size() == game.getHandPositionY()) {
                    game.getOpponent().getCards().get(i).add(card);
                    i = game.getOpponent().getCards().size();
                    fullRow = false;
                }

                if (fullRow) {
                    game.setHandPositionY(game.getHandPositionY() + 1);
                    putCardInDeck(isPlayer, card);
                }
            }
        }

    }


    private void turnHandler(boolean isPlayerTurn) {

        if (isPlayerTurn) {
            enableButtons(true);

        } else {
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

            if (game.getDeck().getSize() <= 12) {

            } else if (game.getDeck().getSize() <= 2 && game.getDeck().getSize() != 0) {

            }

        } else {

            game.getOpponent().insertToOpponentCards(move);
            opponentCardsTXT.append("(CARD value:" + move.getCard().getValue() + " suit:" + move.getCard().getSuit() + " ) ");

            if (game.getDeck().getSize() <= 12 && game.getDeck().getSize() > 2) {


            } else if (game.getDeck().getSize() <= 2 && game.getDeck().getSize() != 0) {

            }


        }

        updateCardLeft(move.getCard());


        if (game.getDeck().getSize() == 0) {
            try {
                endGame();
            } catch (TooManyCardsException e) {
                e.printStackTrace();
            } catch (TooFewCardsException e) {
                e.printStackTrace();
            }
        }


    }

    private void endGame() throws TooManyCardsException, TooFewCardsException {
        Log.d(TAG, "EndGame() EndGame() EndGame() EndGame() EndGame()");


        ArrayList<Boolean> playerWinsHands = new ArrayList<Boolean>();

        ArrayList<ArrayList<Card>> playerCards = game.getPlayer().getCards();
        ArrayList<ArrayList<Card>> opponentCards = game.getOpponent().getCards();

        ArrayList<Hand> playerHands = new ArrayList<Hand>();
        ArrayList<Hand> opponentHands = new ArrayList<Hand>();


        for (int i = 0; i < playerCards.size(); i++) {
            Hand hand = new Hand();
            for (int j = 0; j < playerCards.get(i).size(); j++) {
                Card card = new Card(playerCards.get(i).get(j).getValue(), playerCards.get(i).get(j).getSuit());
                hand.addCard(card);
            }
            playerHands.add(hand);
        }

        for (int i = 0; i < opponentCards.size(); i++) {
            Hand hand = new Hand();
            for (int j = 0; j < opponentCards.get(i).size(); j++) {
                Card card = new Card(opponentCards.get(i).get(j).getValue(), opponentCards.get(i).get(j).getSuit());
                hand.addCard(card);
            }
            opponentHands.add(hand);
        }


        int playerWins = 0;
        int opponentWins = 0;

        for (int i = 0; i < 5; i++) {
            boolean comparResult = PokerLogic.comparHands(playerHands.get(i), opponentHands.get(i));
            if (comparResult) {
                playerWins++;
            } else {
                opponentWins++;
            }
        }

        if (playerWins > opponentWins) {
            //Player won
            Log.d(TAG, "Player won Player won Player won Player won Player won Player won Player won ");
        } else {
            //opponent Won
            Log.d(TAG, "opponent Won opponent Won opponent Won opponent Won opponent Won opponent Won ");
        }


    }


    public void opponenMoveHandler(Move move) {


        if (game.getDeck().getSize() > 0) {
            addUsedCard(false, move);
            turnHandler(true);


//fast turns
            turnHandler(false);
            Card card = drawCard();
            Move move1 = new Move(card, getValidHand());
            actMove(move1);


        } else {
            try {
                endGame();
            } catch (TooManyCardsException e) {
                e.printStackTrace();
            } catch (TooFewCardsException e) {
                e.printStackTrace();
            }
        }


    }

    public void sendAutoCard(boolean isPlayer, Card card) {

    }


}
