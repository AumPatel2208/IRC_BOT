import java.util.HashMap;
import java.util.Set;

public class Room {

    String name;
    String description;
    String altDescription;
    HashMap<String, String> exits; // <direction , nameOfRoom>
    Item item;
    boolean itemClear = false;
    final String RED = "\u0003\u0030\u0035";

    public Room(String name) {
        this.name = name;
        // this.description = description;
        // this.altDescription = altDescription;

        if (this.name.equals("mid")) {
            item = new Item("shovel");
            itemClear = true;
            description = "You are at a fork in the road, someone has left a SHOVEL by the tree, there are 4 ways you can go:/newln You can enter a cave "
                    + RED + "North, /newln Walk through an opening in the bush " + RED + "South /newln A tall Building "
                    + RED + "East /newln Back " + RED + "West";
            altDescription = "You are at a fork in the road, you have taken the shovel, there are 4 ways you can go:/newln You can enter a cave "
                    + RED + "North, /newln Walk through an opening in the bush " + RED + "South /newln A tall Building "
                    + RED + "East /newln Back " + RED + "West";
        } else if (this.name.equals("moss")) {
            item = new Item("meat");
            description = "The ground here feels soft, only one way to go, Back " + RED + "North";
            altDescription = "After digging with shovel, you see that someone has left a pile of raw MEAT here";
        } else if (this.name.equals("cave")) {
            item = new Item("key");
            description = "You enter the cave, you see a bear in front of you./newln You can still go Back " + RED
                    + "south";
            altDescription = "There is a KEY behind the bear, the bear is distracted, now is your chance.";
        } else if (this.name.equals("building")) {
            description = "You are in front of the tall building. You pull the door, it is locked, you need to find a key. /newln You can still go Back "
                    + RED + "west";
            altDescription = "You are in front of the tall building. You can use the key you found on the door.";
        } else if (this.name.equals("start")) {
            description = "Start of game. /newln You see a path in front of you leading " + RED + "east.";
        }
    }

    public String enterArea() { // fix so it matches game state //MAY NEED TO HANDLE FROM MAIN CLASS
        return description;
    }

    // Add command to eat the meat at any point
    // Add direction command as well
    // Add look function to all as well
    // Pass inventory through, update inventory as well
    public String options(String command, Item[] inventory) {

        if (command.contains("look")) { // Fix this so it changes on alt description and stuff
            if (name.equals("mid")) {
                if (inventory[IrcMain.ITEM_SHOVEL] != null) {
                    return altDescription;
                }
            } else if (itemClear) {
                return altDescription;
            } else if ((name.equals("building")) && (inventory[IrcMain.ITEM_KEY] != null)) {
                return altDescription;
            }
            return description;
        } else if (command.contains("inventory")) { // Fix this so it changes on alt description and stuff
            String inveString = "Inventory: ";
            for (Item item : inventory) {
                if (item != null)
                    inveString += item.getName() + " ";
            }
            return inveString;
        }

        if (command.contains("die") || command.contains("suicide") || command.contains("oof")) {
            if (command.contains("die"))
                return "You just killed yourself by voluntarily giving yourself a heart attack! GAME OVER";
            if (command.contains("suicide"))
                return "You just commited suicide by dying! GAME OVER";
            if (command.contains("oof"))
                return "You've just oofed yourself! GAME OVER";
        }

        // if dig is typed in any other level
        if (!name.equals("moss") && command.contains("dig")) {
            return "Tried digging, ground is too hard";
        }

        if (command.contains(" eat ")) {
            if (command.contains("shovel") && inventory[IrcMain.ITEM_SHOVEL] != null) {
                return "You forced a shovel down you're throat! Very smart idea, now you're dead... " + "GAME OVER";
            } else if (command.contains("meat") && inventory[IrcMain.ITEM_MEAT] != null) {
                return "You just ate raw meat! It was an alright idea, buuuuuuuuuuut, now your dead... " + "GAME OVER";
            }
        }

        // First level if for level
        if (name.equals("moss")) {
            // second level if for the command
            if (command.contains("dig")) {
                if (inventory[IrcMain.ITEM_SHOVEL] != null) {
                    itemClear = true;
                    return altDescription;
                } else {
                    return "Tried digging with hands, not effective";
                }
            } else if ((command.contains("take") || command.contains("pick")) && command.contains(item.getName())) { // Take
                                                                                                                     // Shovel
                if (itemClear) {
                    item.itemTaken();
                    inventory[IrcMain.ITEM_MEAT] = item;
                    return (item.getName() + " Taken");
                } else {
                    return ("There is nothing to take.");
                }
            } else if (command.contains(IrcMain.CMD_PLAY_NORTH)) {
                return "MOVE " + IrcMain.ROOM_MID;

            } else {
                return "Not valid option";
            }
        } else if (name.equals("cave")) {
            // Fighting the bear
            if (command.contains("fight") || command.contains("kiss") || command.contains("bear")) {
                return "You were being stupid and died! /newln" + "GAME OVER";
            } else if (((command.contains("drop") || command.contains("throw") || command.contains("give"))
                    && command.contains("meat")) && inventory[IrcMain.ITEM_MEAT] != null) {// only happens if meat is
                                                                                           // dropped and is contained
                                                                                           // in the inventory
                inventory[IrcMain.ITEM_MEAT] = null;
                itemClear = true;
                return "You dropped the meat, Bear is distracted and is eating the meat. " + altDescription;
            } else if ((command.contains("take") || command.contains("pick")) && command.contains(item.getName())) {
                if (itemClear) {
                    if (item.isTaken()) {
                        return ("You already took the key");
                    } else {
                        inventory[IrcMain.ITEM_KEY] = item;
                        altDescription = "You have taken the key, there is nothing but a bear here. Best go back " + RED
                                + "south";
                        return (item.getName() + " Taken");
                    }
                } else {
                    return "The bear saw you, and easily killed you! You should find a way to distract the bear first."
                            + "/newln" + RED + "GAME OVER";
                }
            } else if (command.contains(IrcMain.CMD_PLAY_SOUTH)) {
                return "MOVE " + IrcMain.ROOM_MID;

            } else {
                return "Not valid option";

            }
        } else if (name.equals("mid")) {

            if ((command.contains("take") || command.contains("pick")) && command.contains("shovel")) {
                if (item.isTaken()) {
                    return ("You already took the shovel");
                } else {
                    inventory[IrcMain.ITEM_SHOVEL] = item;
                    item.itemTaken();
                    return item.getName() + " Taken";
                }
            } else if (command.contains(IrcMain.CMD_PLAY_EAST)) {
                return "MOVE " + IrcMain.ROOM_BUILDING;
            } else if (command.contains(IrcMain.CMD_PLAY_WEST)) {
                return "MOVE " + IrcMain.ROOM_START;
            } else if (command.contains(IrcMain.CMD_PLAY_NORTH)) {
                return "MOVE " + IrcMain.ROOM_CAVE;
            } else if (command.contains(IrcMain.CMD_PLAY_SOUTH)) {
                return "MOVE " + IrcMain.ROOM_MOSS;
            } else {
                return "Not valid option";
            }

        } else if (name.equals("building")) {
            // if key is taken
            if (command.contains("open") || command.contains("unlock") || command.contains("door")) {
                if (inventory[IrcMain.ITEM_KEY] != null) {
                    return "The key worked! You walk into the building and you hear a quiet whispers in the distance. There are people in here looking to catch you again. RUN! /newln TO BE CONTINUED... /newln /newln Thank you for playing.";
                } else {
                    return description;
                }
            } else if (command.contains(IrcMain.CMD_PLAY_WEST)) {
                return "MOVE " + IrcMain.ROOM_MID;
            } else {
                return "Not valid option";
            }
        } else if (name.equals("start")) {
            if (command.contains(IrcMain.CMD_PLAY_EAST)) {
                // return "MOVE " + IrcMain.ROOM_MID;
                return "MOVE " + IrcMain.ROOM_MID;
            } else {
                return "Not valid option";
            }
        }

        return "Not valid option";

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