package avivbenyair.pokerfiveo.bricks;

import avivbenyair.pokerfiveo.logic.Card;
import avivbenyair.pokerfiveo.logic.Deck;

/**
 * Created by and-dev on 03/08/15.
 */
public class Game {


    private Player player;
    private Opponent opponent;
    private int handPositionY;
    private Deck deck;
    private boolean playerStarts;


    public Game(boolean playerStarts, Opponent opponent, Player player) {

        deck = new Deck();
        handPositionY = 0;
        this.playerStarts = playerStarts;
        this.opponent = opponent;
        this.player = player;
    }


    public Card drawNewCard() {
        return deck.getTop();
    }

    public void removeCardFromDeck(Card card) {
        deck.removeCard(card);
    }


    public Deck getDeck() {
        return deck;
    }

    public int getHandPositionY() {
        return handPositionY;
    }

    public void setHandPositionY(int handPositionY) {
        this.handPositionY = handPositionY;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public Opponent getOpponent() {
        return opponent;
    }

    public void setOpponent(Opponent opponent) {
        this.opponent = opponent;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isPlayerStarts() {
        return playerStarts;
    }

    public void setPlayerStarts(boolean playerStarts) {
        playerStarts = playerStarts;
    }
}
