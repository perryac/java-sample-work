package com.example.android.common.dummydata;

/**
 * Created by play on 10/12/2016.
 */


import java.util.ArrayList;

/** Simple wrapper to abstract away the list implementation
 * Caller selects which list to use
 */
public class RandomStuff {
    /** Available list types
     *
     */
    public enum ListType { CHEESES, CARDS }

    /** Return a list of @param count items of specified @param type
     *
     * @param type
     * @param count
     * @return
     */
    public static ArrayList<String> randomList(ListType type, int count) {
        ArrayList<String> rlist;

        switch (type) {
            case CARDS:
                rlist = Cards.dealHand(count);
                break;
            case CHEESES:
            default:
                rlist = Cheeses.randomList(count);
        }
        return rlist;
    }
}
