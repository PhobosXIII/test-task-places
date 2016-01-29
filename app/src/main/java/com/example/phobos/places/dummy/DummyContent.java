package com.example.phobos.places.dummy;

import com.example.phobos.places.Place;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {
    public static final List<Place> ITEMS = new ArrayList<>();
    public static final Map<String, Place> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static {
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(Place item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getText(), item);
    }

    private static Place createDummyItem(int position) {
        return new Place(Double.valueOf(position), Double.valueOf(position), "City " + position, "n/a", new Date());
    }
}
