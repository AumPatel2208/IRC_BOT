import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Room {

    String name;
    String description;
    String altDescription;
    HashMap<String, String> exits; // <direction , nameOfRoom>
    Item item;
    boolean itemClear = false;

    public Room(String name, String description, String altDescription) {
        this.name = name;
        this.description = description;
        this.altDescription = altDescription;

        if (this.name.equals("mid")) {
            item = new Item("shovel");
            description = "You are at a fork in the road, someone has left a SHOVEL by the tree, there are 4 ways you can go:\n You can enter a cave <North>, \n Walk through an opening in the bush <South> \n A tall Building <East> \n Back <West>";
            altDescription = "You are at a fork in the road, you have taken the shovel, there are 4 ways you can go:\n You can enter a cave <North>, \n Walk through an opening in the bush <South> \n A tall Building <East> \n Back <West>";
        } else if (this.name.equals("moss")) {
            item = new Item("meat");
            description = "The ground here feels soft, only one way to go, Back <North>";
            altDescription = "After digging with shovel, you see that someone has left a pile of raw MEAT here";
        } else if (this.name.equals("cave")) {
            item = new Item("key");
            description = "You enter the cave, you see a bear in front of you.\n You can still go Back <south>";
            altDescription = "There is a KEY behind the bear, the bear is distracted, now is your chance.";
        } else if (this.name.equals("building")) {
            description = "You are in front of the tall building. You pull the door, it is locked, you need to find a key. \n You can still go Back <west>";
            altDescription = "You can use the key you found";
        } else if (this.name.equals("start")) {

        }
    }

    // Add command to eat the meat at any point
    // Add direction command as well
    // Add look function to all as well
    public String options(String command) {

        // First level if for level
        if (name.equals("moss")) {
            // second level if for the command
            if (command.contains("dig")) {
                if (command.contains("shovel")) {
                    itemClear = true;
                    return altDescription;
                } else {
                    return "Tried digging with hands, not effective";
                }
            } else if (command.contains("take") && command.contains(item.getName())) {
                if (itemClear) {
                    item.itemTaken();
                    return (item.getName() + " Taken");
                } else {
                    return ("There is nothing to take.");
                }

            } else {
                return "Not valid option";
            }
        } else if (name.equals("cave")) {
            if (command.contains("fight") || command.contains("kiss") || command.contains("bear")) {
                return "You were being stupid and died! " + "GAME OVER";
            } else if ((command.contains("drop") || command.contains("throw") || command.contains("give"))
                    && command.contains("meat")) {// only happens if meat is dropped
                itemClear = true;
                return "You dropped the meat, Bear is distracted and is eating the meat. " + altDescription;
            } else if (command.contains("take") && command.contains(item.getName())) {
                if (itemClear) {
                    if (item.isTaken()) {
                        return ("You already took the key");
                    } else {
                        return (item.getName() + " Taken");
                    }
                } else {
                    return "The bear saw you, and easily killed you! You should find a way to distract the bear first."
                            + "GAME OVER";
                }
            }
        } else if (name.equals("mid")) {

        }

        return "Not valid option";

    }

    public Set<String> possibleRoutes() {
        return exits.keySet();
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the altDescription
     */
    public String getaltDescription() {
        return altDescription;
    }

    /**
     * @return the item
     */
    public Item getItem() {
        return item;
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