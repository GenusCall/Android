package avivbenyair.pokerfiveo.logic;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    public static final int SIZE = 52;

    private int[] suits = {
            Card.HEART,
            Card.DIAMOND,
            Card.CLUB,
            Card.SPADE
    };

    private ArrayList<Card> deck;

    public Deck() {
        this.deck = new ArrayList<Card>();

        for (int suit : this.suits) {
            for (int value = Card.LOW; value <= Card.HIGH; value++) {
                this.deck.add(new Card(value, suit));
            }
        }

        Collections.shuffle(this.deck);
    }

    public void removeCard(Card card) {

        for (int i = 0; i < deck.size(); i++) {
            if (deck.get(i).getValue() == card.getValue() && deck.get(i).getSuit() == card.getSuit()) {
                deck.remove(i);
                Log.d("updateCardLeft", "Card position: " + i);
                i = deck.size();
            }
        }


    }

    public void shuffle() {
        Collections.shuffle(this.deck);
    }

    public Card getTop() {
        return this.deck.remove(0);
    }

    public int getSize() {
        return this.deck.size();
    }
}
