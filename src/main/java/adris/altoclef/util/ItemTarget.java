package adris.altoclef.util;

import adris.altoclef.Debug;
import adris.altoclef.TaskCatalogue;
import adris.altoclef.util.helpers.ItemHelper;
import net.minecraft.item.Item;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * Defines an item and a count.
 *
 * Multiple Minecraft Items can meet the criteria of an "item" (ex. "wooden planks" can be satisfied by oak, acacia, spruce, jungle, etc.)
 */
public class ItemTarget {

    private static final int BASICALLY_INFINITY = 99999999;

    public static ItemTarget EMPTY = new ItemTarget(new Item[0], 0);
    private Item[] _itemMatches;
    private int _targetCount;
    private String _catalogueName = null;
    private boolean _infinite = false;

    public ItemTarget(Item[] items, int targetCount) {
        _itemMatches = items;
        _targetCount = targetCount;
        // Remove duplicates
        Set<Item> set = new HashSet<>();
        for (Item item : _itemMatches) {
            set.add(item);
        }
        _itemMatches = set.toArray(new Item[0]);
        if (items.length > 1) {
            Debug.logMessage("Friendly name for " + Arrays.toString(items) + " is not provided.");
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            new Throwable().printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            Debug.logMessage(sStackTrace);
        }
    }

    public ItemTarget(String catalogueName, int targetCount) {
        _catalogueName = catalogueName;
        _itemMatches = TaskCatalogue.getItemMatches(catalogueName);
        _targetCount = targetCount;
    }

    public ItemTarget(Item[] items, int targetCount, String catalogueName) {
        _itemMatches = items;
        _catalogueName = catalogueName;
        _targetCount = targetCount;
    }

    public ItemTarget(String catalogueName) {
        this(catalogueName, 1);
    }

    public ItemTarget(Item item, int targetCount) {
        this(new Item[]{item}, targetCount);
    }

    public ItemTarget(Item[] items) {
        this(items, 1);
    }

    public ItemTarget(Item[] items, String catalogueName) {
        this(items, 1, catalogueName);
    }

    public ItemTarget(Item item) {
        this(item, 1);
    }

    public ItemTarget(ItemTarget toCopy, int newCount) {
        if (toCopy._itemMatches != null) {
            _itemMatches = new Item[toCopy._itemMatches.length];
            System.arraycopy(toCopy._itemMatches, 0, _itemMatches, 0, toCopy._itemMatches.length);
        }
        _catalogueName = toCopy._catalogueName;
        _targetCount = newCount;
        _infinite = toCopy._infinite;
    }

    public ItemTarget(ItemTarget toCopy) {
        this(toCopy, toCopy._targetCount);
    }

    public ItemTarget infinite() {
        _infinite = true;
        return this;
    }

    public static boolean nullOrEmpty(ItemTarget target) {
        return target == null || target == EMPTY;
    }

    public static Item[] getMatches(ItemTarget... targets) {
        Set<Item> result = new HashSet<>();
        for (ItemTarget target : targets) {
            result.addAll(Arrays.asList(target.getMatches()));
        }
        return result.toArray(Item[]::new);
    }

    public Item[] getMatches() {
        return _itemMatches != null? _itemMatches : new Item[0];
    }

    public int getTargetCount() {
        if (_infinite) {
            return BASICALLY_INFINITY;
        }
        if (_targetCount == BASICALLY_INFINITY) {
            _infinite = true;
            return BASICALLY_INFINITY;
        }
        // Workaround for when the ItemTarget is initialized as infinite (properly)
        // but then another ItemTarget is created by copying the target count, not the infinite flag.
        return _targetCount;
    }

    public boolean matches(Item item) {
        if (_itemMatches != null) {
            for (Item match : _itemMatches) {
                if (match == null) continue;
                if (match.equals(item)) return true;
            }
        }
        return false;
    }

    public boolean isCatalogueItem() {
        return _catalogueName != null;
    }

    public String getCatalogueName() {
        return _catalogueName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ItemTarget other) {
            if (_infinite) {
                if (!other._infinite) return false;
            } else {
                // Neither are infinite
                if (_targetCount != other._targetCount) return false;
            }
            if ((other._itemMatches == null) != (_itemMatches == null)) return false;
            if (_itemMatches != null) {
                if (_itemMatches.length != other._itemMatches.length) return false;
                for (int i = 0; i < _itemMatches.length; ++i) {
                    if (other._itemMatches[i] == null) {
                        if ((other._itemMatches[i] == null) != (_itemMatches[i] == null)) return false;
                    } else {
                        if (!other._itemMatches[i].equals(_itemMatches[i])) return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return _itemMatches == null || _itemMatches.length == 0;
    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();
        if (isEmpty()) {
            result.append("(empty)");
        } else if (isCatalogueItem()) {
            result.append(_catalogueName);
        } else {
            int counter = 0;
            for (Item item : _itemMatches) {
                if (item == null) {
                    result.append("(null??)");
                } else {
                    result.append(ItemHelper.trimItemName(item.getTranslationKey()));
                }
                if (++counter != _itemMatches.length) {
                    result.append(",");
                }
            }
            if (_itemMatches.length > 1) {
                result.insert(0, "(");
                result.append(")");
            }
        }
        if (!_infinite && !isEmpty() && _targetCount > 1) {
            result.append(" x").append(_targetCount);
        } else if (_infinite) {
            result.append(" (attempt for ∞)");
        }

        return result.toString();
    }


}
