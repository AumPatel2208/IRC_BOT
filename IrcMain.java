
// import java.io.BufferedReader;
import java.io.IOException;
// import java.io.InputStreamReader;
import java.io.PrintWriter;
// import java.net.HttpURLConnection;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
// import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

class IrcMain {
    private static String nick;
    private static String trigger;
    private static String serverName;
    private static String userName;
    private static String realName;
    private static PrintWriter out;
    private static Scanner in;

    private static final String CMD_EXIT = "exit";
    private static final String CMD_HELLO = "hello";
    private static final String CMD_HACK = "hack";
    private static final String CMD_ATTACK = "attack";
    private static final String CMD_TIME = "time";
    private static final String CMD_TIME_ZONES = "zones";
    private static final String CMD_PLAY = "play";
    public static final String CMD_PLAY_NORTH = "north";
    public static final String CMD_PLAY_SOUTH = "south";
    public static final String CMD_PLAY_EAST = "east";
    public static final String CMD_PLAY_WEST = "west";
    public static final int ITEM_SHOVEL = 0;
    public static final int ITEM_KEY = 1;
    public static final int ITEM_MEAT = 2;

    public static final int ROOM_START = 0;
    public static final int ROOM_MID = 1;
    public static final int ROOM_CAVE = 2;
    public static final int ROOM_MOSS = 3;
    public static final int ROOM_BUILDING = 4;

    private static final String CHANNEL = "#thebois";
    // private static final String CHANNEL = "#Goethe";

    private static Room[] rooms;
    private static int currentRoom;

    public static void main(String[] args) throws IOException {
        Scanner console = new Scanner(System.in);

        Socket socket = new Socket("127.0.0.1", 6667);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new Scanner(socket.getInputStream());

        nick = "BobBot"; // Nick Name
        trigger = "bb"; // Nick Name

        write("Nick", nick);
        write("USER", "aumBot 8 * :aum's bot v1.0");
        // Hard Coded entry to the channel
        // write("JOIN", "#thebois");
        write("JOIN", CHANNEL);
        boolean active = false;
        boolean inGame = false;
        String[] names = new String[0]; // names

        Item[] inventory = new Item[3];
        rooms = new Room[5];
        rooms[ROOM_START] = new Room("start");
        rooms[ROOM_MID] = new Room("mid");
        rooms[ROOM_CAVE] = new Room("cave");
        rooms[ROOM_MOSS] = new Room("moss");
        rooms[ROOM_BUILDING] = new Room("building");

        while (in.hasNext()) {
            String serverMessage = in.nextLine();

            System.out.println("<<< " + serverMessage);

            // Names List
            if (serverMessage.contains("353")) {
                names = serverMessage.split(":")[2].trim().split(" "); // got the names
                // for (String name : names) {
                // System.out.println(name);
                // }
            }

            // Add new person that joins into the list
            if (serverMessage.contains("JOIN")) {
                String[] tempNames = new String[1 + names.length];

                for (int i = 0; i < names.length; i++) {
                    tempNames[i] = names[i];
                }
                tempNames[tempNames.length - 1] = serverMessage.split(":")[1].split("!")[0];
                names = tempNames;
            }

            // If person is kicked, update names list
            if (serverMessage.contains("KICK")) {

                String tempString = "";

                for (int i = 0; i < names.length; i++) {
                    if (names[i].equals(serverMessage.split(CHANNEL)[1].trim().split(" ")[0])) {
                        names[i] = "";
                    }
                    tempString += names[i] + " ";
                }
                names = tempString.split(" ");
            }

            // remove person that 'parts' from the list
            if (serverMessage.contains("PART")) {
                String tempString = "";

                for (int i = 0; i < names.length; i++) {
                    if (names[i].equals(serverMessage.split(":")[1].split("!")[0])) {
                        names[i] = "";
                    }
                    tempString += names[i] + " ";
                }
                names = tempString.split(" ");
            }

            if (serverMessage.contains("PING")) {
                writeMessage("USE ME");
            }
            if (serverMessage.contains("366")) // End of Names List
                active = true;

            if (active) {

                if (serverMessage.split(":").length >= 3) {
                    // Get the text input they have written
                    String[] messageSplit = serverMessage.split(":")[2].trim().split(" ");
                    if ((messageSplit[0].toLowerCase().equals(trigger.toLowerCase())
                            || messageSplit[0].toLowerCase().equals(nick.toLowerCase())) && messageSplit.length > 1) {
                        if (!inGame) {
                            if (messageSplit[1].toLowerCase().equals(CMD_EXIT)) {
                                // Break out of the while loop to exit
                                // tried using a boolean but the exit was delayed until another message was sent
                                // into the chat.
                                break;

                            } else if (messageSplit[1].toLowerCase().equals(CMD_HELLO)) {
                                // Say hello <USERNAME>
                                writeMessage("Hello " + serverMessage.split(":")[1].split("!")[0]);
                            } else if (messageSplit[1].toLowerCase().equals(CMD_HACK)) {

                                // Hacking visual
                                String dots = "Hacking .";
                                for (int i = 0; i < 5; i++) {
                                    writeMessage(dots);
                                    dots += ".";
                                    try {
                                        TimeUnit.MILLISECONDS.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                                // Maybe return what IP Addresxs and users are in the IRC chat
                                writeMessage("Server Hack successful");

                                // Write names in the server
                                writeMessage("People in this channel: ");
                                String tempMessage = "";
                                for (String name : names) {
                                    tempMessage += name + " ";
                                }
                                writeMessage(tempMessage);
                                writeMessage("Please select a person to attack (type 'bobbot attack <name>' ): ");
                            } else if (messageSplit[1].toLowerCase().equals(CMD_ATTACK)) {
                                // What to do when attacking
                                if (messageSplit.length >= 3) {
                                    writeMessage("I banish you from the server " + messageSplit[2]);
                                }
                            } else if (messageSplit[1].toLowerCase().equals(CMD_PLAY)) {
                                // Clean rooms up -- Restart
                                rooms[ROOM_START] = new Room("start");
                                rooms[ROOM_MID] = new Room("mid");
                                rooms[ROOM_CAVE] = new Room("cave");
                                rooms[ROOM_MOSS] = new Room("moss");
                                rooms[ROOM_BUILDING] = new Room("building");
                                // Clean Inventory -- Restart
                                inventory = new Item[3];

                                // Start playing game disable other commands as well
                                currentRoom = ROOM_START;
                                writeMessageSplitLines(
                                        "You have the following commands to your disposal: /newln 'move <direction>' /newln 'take <item>' /newln There are more but lets keep it a mystery 🤫 /newln");
                                writeMessageSplitLines(rooms[currentRoom].getDescription());

                                inGame = true;
                            } else {
                                // Write what the person says
                                String tempMessage = "";
                                for (int i = 1; i < messageSplit.length; i++) {
                                    tempMessage += messageSplit[i] + " ";
                                }
                                writeMessage(tempMessage);
                            }
                        } else {

                            if (messageSplit[1].toLowerCase().equals(CMD_EXIT)) {
                                writeMessage("Ending Game");
                                inGame = false;
                            } else {
                                String command = "";
                                for (int i = 1; i < messageSplit.length; i++) {
                                    command += messageSplit[i];
                                }
                                String returnedMessege = rooms[currentRoom].options(command.toLowerCase(), inventory);

                                // writeMessage(returnedMessege);

                                if (returnedMessege.contains("GAME OVER")) {

                                    // currentRoom = Integer.parseInt(returnedMessege.split(" ")[1]);
                                    // writeMessageSplitLines(rooms[currentRoom].options("look", inventory));
                                    writeMessageSplitLines(returnedMessege);
                                    writeMessage("Ending Game");
                                    inGame = false;

                                } else if (returnedMessege.contains("MOVE")) {

                                    currentRoom = Integer.parseInt(returnedMessege.split(" ")[1]);
                                    writeMessageSplitLines(rooms[currentRoom].options("look", inventory));

                                } else {
                                    writeMessageSplitLines(returnedMessege);
                                }
                            }
                        }
                    }
                }
            }

        }
        in.close();
        out.close();
        socket.close();

        System.out.println("done");

    }

    // method to send PRIVMSG to #thebois (should change so it is dynamic)
    private static void writeMessageSplitLines(String message) {
        String[] splitMessage = message.split("/newln");
        for (String mes : splitMessage) {
            writeMessage(mes);
        }
    }

    // method to send PRIVMSG to #thebois (should change so it is dynamic)
    private static void writeMessage(String message) {
        write("PRIVMSG", CHANNEL + " :" + message);
    }

    // method to send write a message
    private static void write(String command, String message) {
        String fullMessage = command + " " + message;
        System.out.println(">>> Full Message: " + fullMessage);
        out.print(fullMessage + "\r\n");
        out.flush();
    }
}
