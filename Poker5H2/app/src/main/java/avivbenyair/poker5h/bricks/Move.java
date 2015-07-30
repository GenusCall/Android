package avivbenyair.poker5h.bricks;

import avivbenyair.poker5h.logic.Card;

/**
 * Created by and-dev on 30/07/15.
 */
public class Move {

    private Card card;
    private int Hand;
    private int position;

    public Move() {
    }

    public Move(Card card, int hand, int position) {
        this.card = card;
        Hand = hand;
        this.position = position;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public int getHand() {
        return Hand;
    }

    public void setHand(int hand) {
        Hand = hand;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
