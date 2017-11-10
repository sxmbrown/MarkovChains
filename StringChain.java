/**
 * Samuel Brown
 * November 6, 2017
 * UNM CS251L Lab 7 - Markov Text Generation
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Random;

public class StringChain {
    /**
     * member variables 
     * chainMap -- map of the prefixes to the suffixes to build
     * stringchains from input text. 
     * NONWORD -- a way to pad the text to continue
     * outputting text when it would've otherwise stopped.
     */
    private final int order;
    private Map<chainMapKey, probability> chainMap;
    private static final String nonword = "NONWORD";

    /**
     * creates a new hashmap with the keys and values.
     * 
     * @param order
     */
    public StringChain(int order) {
        this.order = order;
        chainMap = new HashMap<chainMapKey, probability>();
    }

    /**
     * updates the value part of the map to be consistent with the number of times a
     * suffix appears. Just sums up the integer values of the suffixes before it to eventually
     * give us a range with which to pull a random value.
     */
    public void valuesuffix() {
        for (probability suffix : chainMap.values()) {
            Iterator<Entry<String, Integer>> iter = suffix.map().entrySet().iterator();
            int count = 0;
            while (iter.hasNext()) {
                Entry<String, Integer> entry = iter.next();
                entry.setValue(entry.getValue() + count);
                count = entry.getValue();
            }
        }
    }

    /**
     * key class for the chainMap. Utilizes ArrayList in order to use the API
     * implementations of equals and hashCode methods.
     * 
     * @author Samuel Brown
     */
    private class chainMapKey {

        ArrayList<String> stringArray;

        public chainMapKey(int order) {
            stringArray = new ArrayList<>(order);
            for (int i = 0; i < stringArray.size(); i++) {
                stringArray.add(i, StringChain.nonword);
            }
        }

        public chainMapKey(chainMapKey key, String str) {
            this(key.stringArray.size());
            for (int i = 1; i < stringArray.size(); i++) {
                stringArray.add((i), key.stringArray.get(i));
            }
            stringArray.add(stringArray.size(), str);
        }
        /**
         * default ArrayList API equals method.
         */
        public boolean equals(Object o) {
            return o.equals(stringArray);
        }

        /**
         * Default ArrayList API hashCode method.
         */
        public int hashCode() {
            return stringArray.hashCode();
        }
    }

    /**
     * The value class for our hashMap. Will provide values for the chainMap.
     * 
     * @author Samuel Brown
     *
     */
    private class probability {

        LinkedHashMap<String, Integer> suffixMap;
        int count = 0;

        public probability() {
            suffixMap = new LinkedHashMap<String, Integer>();
        }

        /**
         * adds a string - integer pair to the linkedhashmap, this is how we are keeping
         * track of frequency of occurrence of our suffixes.
         * 
         * @param string
         *            use this parameter to check if the suffix is already in the map.
         */
        public void addToMap(String string) {

            if (suffixMap.containsKey(string)) {
                suffixMap.put(string, suffixMap.get(string) + 1);
            } else {
                suffixMap.put(string, 1);
            }
            count = count + 1;
        }

        public LinkedHashMap<String, Integer> map() {
            return (LinkedHashMap<String, Integer>) suffixMap;
        }

    }

    /**
     * adding the mapped "key - probability" pairs to our chainMap. In the event
     * that the key isn't there, it will add a new "key - probability" mapping. If
     * it does exist, it will map a new suffix to the given key
     * 
     * @param itemIter
     */
    public void addItems(Iterator<String> itemIter) {
        chainMapKey key = new chainMapKey(order);
        String string = null;
        while (itemIter.hasNext()) {
            string = itemIter.next();
            if (chainMap.containsKey(key) == false) {
                probability suffix = new probability(); // create a new probability mapping object.
                chainMap.put(key, suffix); // add our new key - probability pair to the map.
            }
            chainMap.get(key).addToMap(string);
            key = new chainMapKey(key, string);
        }
        for (int i = 0; i < order; i++) {
            if (!chainMap.containsKey(key)) {
                chainMap.put(key, new probability());
            }
            chainMap.get(key).addToMap(nonword);
            key = new chainMapKey(key, nonword);
        }
        valuesuffix(); // method at the top.
    }

    /**
     * creating a list of strings which should by getting keys from the map, and
     * picking a suffix for the prefix key based on random number generation within
     * the bounds of the added up number of times a suffix occurs given it's mapped
     * to prefix.
     * 
     * @param n
     *            the number of words we want to use
     * @param rand
     *            a random number generator
     * @return the string list "strings"
     */
    public List<String> generate(int n, Random rand) {
        List<String> strings = new ArrayList<String>(n);
        chainMapKey key = new chainMapKey(order);
        for (int i = 0; i < n; i++) {
            probability suffix = chainMap.get(key);
            int random = rand.nextInt(suffix.count);
            for (Map.Entry<String, Integer> newEntry : suffix.map().entrySet()) {
                if (random < newEntry.getValue()) {
                    strings.add(newEntry.getKey());
                    key = new chainMapKey(key, newEntry.getKey());
                    break;
                }
            }
        }
        return strings;
    }

}
