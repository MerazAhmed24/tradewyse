package com.info.commons;

import java.util.HashMap;
import java.util.HashSet;

public class Demo {

    private void filterNotAvailableFollowItems(){
             //map 1 has 3 keys
        HashMap<Integer, String> map1 = new HashMap<>();
        map1.put(1, "A");
        map1.put(2, "B");
        map1.put(3, "C");

//map 2 has 4 keys
        HashMap<Integer, String> map2 = new HashMap<>();
        map2.put(1, "A");
        map2.put(2, "B");
        map2.put(3, "C");
        map2.put(4, "C");

//Union of keys from both maps
        HashSet<Integer> unionKeys = new HashSet<>(map1.keySet());
        unionKeys.addAll(map2.keySet());

        unionKeys.removeAll(map1.keySet());

        System.out.println(unionKeys);
    }

}
