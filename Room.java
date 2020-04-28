import java.util.HashMap;

public class Room {

    String name;
    String description;
    HashMap<String, String> exits; // <direction , nameOfRoom>

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the exits
     */
    public HashMap<String, String> getExits() {
        return exits;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

}