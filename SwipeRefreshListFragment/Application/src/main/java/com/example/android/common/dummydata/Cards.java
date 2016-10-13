package com.example.android.common.dummydata;

/**
 * Created by play on 10/12/2016.
 */

import java.util.ArrayList;
import java.util.Random;

/** Dummy data using a deck of cards
 *
 */
public class Cards {
    static final String[] Ranks = {
            "Deuce",
            "Three",
            "Four",
            "Five",
            "Six",
            "Seven",
            "Eight",
            "Nine",
            "Ten",
            "Jack",
            "Queen",
            "King",
            "Ace"
    };

    static final String[] Suits = {
            "Clubs",
            "Diamonds",
            "Hearts",
            "Spades"
    };

    public static ArrayList<String> asList() {
        ArrayList<String> deck = new ArrayList<String>();
        for (String r: Ranks) {
            for (String s: Suits) {
                deck.add(r + " of " + s);
            }
        }
        return deck;
    }

    /**
     * Return a shuffled deck
     */
    public static ArrayList<String> shuffledDeck() {
        ArrayList<String> shuffled = new ArrayList<String>();
        Random rng = new Random();
        ArrayList<String> deck = asList();
        int pick;

        // remove a random card from deck, add to shuffled
        // rng bounds are automatically adjusted to current size of deck

        while (deck.size() > 0) {
            pick = rng.nextInt(deck.size());
            shuffled.add(deck.get(pick));
            deck.remove(pick);
        }

        return shuffled;
    }

    /**
     * Deal a hand of @param count from the shuffled deck
     */
    public static ArrayList<String> dealHand(int count) {
        ArrayList<String> hand = new ArrayList<String>();
        ArrayList<String> sdeck = shuffledDeck();

        count = Math.min(count, sdeck.size()); // don't ask for more cards than are in the deck

        for (int i = 0; i < count; ++i) {
            hand.add(sdeck.get(i));
        }

        return hand;
    }
}
