package avivbenyair.pokerfiveo.bricks;

import java.util.ArrayList;

import avivbenyair.pokerfiveo.logic.Card;

/**
 * Created by and-dev on 02/08/15.
 */
public class Player {

    private String objectID;
    private String userName;
    private ArrayList<ArrayList<Card>> Cards;


    public Player(String objectID, String userName) {

        this.objectID = objectID;
        this.userName = userName;

        Cards = new ArrayList<>();

        Cards.add(new ArrayList<Card>());
        Cards.add(new ArrayList<Card>());
        Cards.add(new ArrayList<Card>());
        Cards.add(new ArrayList<Card>());
        Cards.add(new ArrayList<Card>());
    }


    public void insertToPlayerCards(Move move) {
        Cards.get(move.getPositionX()).add(move.getCard());
    }

    public ArrayList<ArrayList<Card>> getCards() {
        return Cards;
    }

    public void setCards(ArrayList<ArrayList<Card>> cards) {
        Cards = cards;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}