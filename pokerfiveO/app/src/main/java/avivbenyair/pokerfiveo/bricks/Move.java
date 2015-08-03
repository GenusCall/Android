package avivbenyair.pokerfiveo.bricks;


import avivbenyair.pokerfiveo.logic.Card;

/**
 * Created by and-dev on 30/07/15.
 */
public class Move {

    private Card card;
    private int positionX;


    public Move() {
    }

    public Move(Card card, int positionX) {
        this.card = card;
        this.positionX = positionX;

    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }
}
