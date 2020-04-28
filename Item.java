
public class Item {
    private boolean taken = false;

    private String name;

    public Item(String name) {
        this.name = name;
    }

    public void itemTaken() {
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