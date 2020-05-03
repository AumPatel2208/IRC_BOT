// Item object used in the game.
public class Item {
    private boolean taken = false; // boolean to show whether the item has been take or not

    private String name; // name of the item

    public Item(String name) {
        this.name = name; // initialize the name
    }

    public void itemTaken() { // if the item is taken then make taken true
        taken = true;
    }

    /**
     * @return the taken
     */
    public boolean isTaken() {
        return taken;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}