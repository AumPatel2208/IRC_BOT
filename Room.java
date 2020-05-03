
// Room object that contains most of the logic for the game and defines one room in the game
public class Room {

    // Name of the room
    String name;
    // Description of the room -- what is shown when entering the room or when
    // looking around
    String description;
    // Alternate description that describes the room after the state has changed
    String altDescription;

    // The item for the room
    Item item;
    // whether the item is clear to take by the player
    boolean itemClear = false;
    final static String RED = "\u0003\u0030\u0035";

    public Room(String name) {
        this.name = name;

        if (this.name.equals("mid")) {
            item = new Item("shovel");
            itemClear = true;
            description = "You are at a fork in the road, someone has left a SHOVEL by the tree, there are 4 ways you can go:/newln    You can enter a cave "
                    + RED + "North, /newln    Walk through an opening in the bush " + RED
                    + "South /newln    A tall Building " + RED + "East /newln    Back where you came from " + RED
                    + "West";
            altDescription = "You are at a fork in the road, you have taken the shovel, there are 4 ways you can go:/newln You can enter a cave "
                    + RED + "North, /newln Walk through an opening in the bush " + RED + "South /newln A tall Building "
                    + RED + "East /newln Back " + RED + "West";
        } else if (this.name.equals("moss")) {
            item = new Item("meat");
            description = "The ground here feels soft, only one way to go, Back " + RED + "North";
            altDescription = "After digging with shovel, you see that someone has left a pile of raw MEAT here. You should pick up the meat./newln     You can go back "
                    + RED + "North";
        } else if (this.name.equals("cave")) {
            item = new Item("key");
            description = "You enter the cave, you see a bear in front of you./newln    You can still go Back " + RED
                    + "south";
            altDescription = "There is a KEY behind the bear, the bear is distracted, now is your chance./newln    You can still go Back "
                    + RED + "south";
        } else if (this.name.equals("building")) {
            description = "You are in front of the tall building. You pull the door, it is locked, you need to find a key. /newln    You can still go Back "
                    + RED + "west";
            altDescription = "You are in front of the tall building. You can use the key you found on the door.";
        } else if (this.name.equals("start")) {
            description = "Start of game. /newln    You see a only one clear path, it is in front of you and is leading "
                    + RED + "east.";
        }
    }

    // This function returns a string based on what command they enter into the
    // game. The Main class also passes through the inventory (as it is an array, it
    // acts as if it is being passed through by reference)
    public String options(String command, Item[] inventory) {

        if (command.contains("look")) {
            // If it is the mid room
            if (name.equals("mid")) {
                if (inventory[IrcMain.ITEM_SHOVEL] != null) { // if the shovel is in the inventory
                    return altDescription;
                }
            } else if (itemClear) {
                return altDescription; // if the item is clear then return the alt description
            } else if ((name.equals("building")) && (inventory[IrcMain.ITEM_KEY] != null)) { // if the key is in the
                                                                                             // inventory and the room
                                                                                             // is buillding
                return altDescription;
            }
            return description;
        } else if (command.contains("inventory")) { // if the command has inventory, then show the inventory
            String inveString = "Inventory: ";
            for (Item item : inventory) {
                if (item != null)
                    inveString += item.getName() + " ";
            }
            return inveString;
        }

        if (command.contains("die") || command.contains("suicide") || command.contains("oof")) { // if the player tries
                                                                                                 // to commit suicide
            if (command.contains("die"))
                return "You just voluntarily gave yourself a heart attack! GAME OVER";
            if (command.contains("suicide"))
                return "You just commited suicide by dying! GAME OVER";
            if (command.contains("oof"))
                return "You've just oofed yourself! GAME OVER";
        }

        // if dig is typed in any other level
        if (!name.equals("moss") && command.contains("dig")) { // if the level is not moss and player tries to dig
            return "Tried digging, ground is too hard";
        }

        if (command.contains(" eat ")) { // if the player tries to eat the shovel or the key
            if (command.contains("shovel") && inventory[IrcMain.ITEM_SHOVEL] != null) {
                return "You forced a shovel down you're throat! Very smart idea, now you're dead... " + "GAME OVER";
            } else if (command.contains("meat") && inventory[IrcMain.ITEM_MEAT] != null) {
                return "You just ate raw meat! It was an alright idea, buuuuuuuuuuut, now your dead... " + "GAME OVER";
            }
        }

        // first level if -- for the level/room
        if (name.equals("moss")) { // if the level is moss
            // second level if -- for the command
            if (command.contains("dig")) { // if the player tries to dig
                if (inventory[IrcMain.ITEM_SHOVEL] != null) {
                    itemClear = true;
                    return altDescription;
                } else {
                    return "Tried digging with hands, not effective";
                }

            } else if ((command.contains("take") || command.contains("pick")) && command.contains(item.getName())) {
                // Take Meat
                if (itemClear) {
                    item.itemTaken();
                    inventory[IrcMain.ITEM_MEAT] = item;
                    return (item.getName() + " Taken");
                } else {
                    return ("There is nothing to take.");
                }
            } else if (command.contains(IrcMain.CMD_PLAY_NORTH)) { // move north
                return "MOVE " + IrcMain.ROOM_MID;

            } else {// other directions not valid
                return "Not valid option";
            }
        } else if (name.equals("cave")) {
            // Fighting the bear
            if (command.contains("fight") || command.contains("kiss") || command.contains("bear")) {
                return "You were being stupid and died! /newln" + "GAME OVER";
            } else if (((command.contains("drop") || command.contains("throw") || command.contains("give"))
                    && command.contains("meat")) && inventory[IrcMain.ITEM_MEAT] != null) {
                // only happens if meat is dropped and is contained in the inventory
                inventory[IrcMain.ITEM_MEAT] = null;
                itemClear = true;
                return "You dropped the meat, Bear is distracted and is eating the meat. " + altDescription;
            } else if ((command.contains("take") || command.contains("pick")) && command.contains(item.getName())) {
                if (itemClear) {
                    if (item.isTaken()) {
                        return ("You already took the key");
                    } else {
                        // do when the play takes the key
                        inventory[IrcMain.ITEM_KEY] = item;
                        altDescription = "You have taken the key, there is nothing but a bear here. Best go back " + RED
                                + "south";
                        return (item.getName() + " Taken");
                    }
                } else {
                    // if the player tries to take the key without the distracting the bear.
                    return "The bear saw you, and easily killed you! You should find a way to distract the bear first."
                            + "/newln" + RED + "GAME OVER";
                }
            } else if (command.contains(IrcMain.CMD_PLAY_SOUTH)) { // move south
                return "MOVE " + IrcMain.ROOM_MID;

            } else { // other directions not valid
                return "Not valid option";
            }
        } else if (name.equals("mid")) { // if the player is in the mid room

            if ((command.contains("take") || command.contains("pick")) && command.contains("shovel")) {
                if (item.isTaken()) {
                    return ("You already took the shovel");
                } else {
                    // do when the play takes the shovel
                    inventory[IrcMain.ITEM_SHOVEL] = item;
                    item.itemTaken();
                    return item.getName() + " Taken";
                }
            } else if (command.contains(IrcMain.CMD_PLAY_EAST)) { // move east
                return "MOVE " + IrcMain.ROOM_BUILDING;
            } else if (command.contains(IrcMain.CMD_PLAY_WEST)) {// move west
                return "MOVE " + IrcMain.ROOM_START;
            } else if (command.contains(IrcMain.CMD_PLAY_NORTH)) {// move north
                return "MOVE " + IrcMain.ROOM_CAVE;
            } else if (command.contains(IrcMain.CMD_PLAY_SOUTH)) {// move south
                return "MOVE " + IrcMain.ROOM_MOSS;
            } else {
                return "Not valid option";
            }

        } else if (name.equals("building")) {
            // if key is taken
            if (command.contains("open") || command.contains("unlock") || command.contains("door")) {
                if (inventory[IrcMain.ITEM_KEY] != null) { // Unlock the door and finish the game successfully
                    return "The key worked! You walk into the building and you hear a quiet whispers in the distance. There are people in here looking to catch you again. RUN! /newln TO BE CONTINUED... /newln /newln Thank you for playing.";
                } else {
                    return description;
                }
            } else if (command.contains(IrcMain.CMD_PLAY_WEST)) { // move west
                return "MOVE " + IrcMain.ROOM_MID;
            } else {
                return "Not valid option"; // invalid option
            }
        } else if (name.equals("start")) { // if it is the start of the game
            if (command.contains(IrcMain.CMD_PLAY_EAST)) { // move east
                // return "MOVE " + IrcMain.ROOM_MID;
                return "MOVE " + IrcMain.ROOM_MID;
            } else {
                return "Not valid option"; // invalid option
            }
        }

        return "Not valid option"; // if anything else, itnvalid option

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
     * @return the name
     */
    public String getName() {
        return name;
    }

}