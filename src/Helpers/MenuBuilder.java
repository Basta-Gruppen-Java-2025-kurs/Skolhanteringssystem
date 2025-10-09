package Helpers;

import java.util.ArrayList;

public class MenuBuilder {
    private final ArrayList<String> items = new ArrayList<>();
    private final ArrayList<Runnable> callbacks = new ArrayList<>();
    private String header = "Please select an option.";
    private boolean singleShot = false;

    public MenuBuilder(String header, String exitItem) {
        this.header = header;
        items.add(exitItem);
    }

    public MenuBuilder(String exitItem) {
        items.add(exitItem);
    }

    public MenuBuilder() {
        items.add("Exit");
    }

    public MenuBuilder setHeader(String header) {
        this.header = header;
        return this;
    }

    public MenuBuilder setSingleShot(boolean singleShot) {
        this.singleShot = singleShot;
        return this;
    }

    public MenuBuilder addItem(String item, Runnable callback) {
        items.add(item);
        callbacks.add(callback);
        return this;
    }

    public void runMenu() {
        TextMenu.menuLoop(header, items.toArray(new String[0]), callbacks.toArray(new Runnable[0]), singleShot);
    }

}
