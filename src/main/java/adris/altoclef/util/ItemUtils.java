package adris.altoclef.util;

import adris.altoclef.tasks.container.SmeltInFurnaceTask;
import adris.altoclef.util.SmeltTarget;
import adris.altoclef.util.ItemTarget;
import adris.altoclef.tasksystem.Task;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;


class ItemMatcher {
    public String name;
    public Item[] items;
    public Function<ItemMatcher, Task> taskGenerator;
    public ItemMatcher(String name, Item[] items, Function<ItemMatcher, Task> taskGenerator) {
        this.name = name;
        this.items = items;
        this.taskGenerator = taskGenerator;
    }
}
/* Utility to handle items and item groups
 * Replaces TaskCatalogue and ItemHelper
 */

public class ItemUtils {
    public static final ItemMatcher BASE_ITEMS[] = new ItemMatcher[]{
        new ItemMatcher("stone", new Item[]{Items.STONE}, self -> smeltTask(self, Items.COBBLESTONE))
    };
    private static Task smeltTask(ItemMatcher matcher, Item source) {
        return new SmeltInFurnaceTask(new SmeltTarget(
            new ItemTarget(matcher.items),
            new ItemTarget(source),
            Items.COBBLESTONE
        ));
    }
    public static Item[] getItemMatches(String name) {
        for (ItemMatcher matcher : BASE_ITEMS) {
            if (matcher.name.equals(name)) {
                return matcher.items;
            }
        }
        return new Item[0];
    }
    public static Task getTask(String name) {
        for (ItemMatcher matcher : BASE_ITEMS) {
            if (matcher.name.equals(name)) {
                return matcher.taskGenerator.apply(matcher);
            }
        }
        return null;
    }
}
