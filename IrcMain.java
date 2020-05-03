import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

class IrcMain {
    private static String nick;
    private static String trigger;

    // Printer and scanner for the network
    private static PrintWriter out;
    private static Scanner in;

    // constant help string that contains a list of commands the user can use
    private static final String help = "Commands:/newln    - hello: try it/newln    - play: play an adventure game/newln    - news: get the current top 5 news articles/newln    - joke: tells you a joke (safe for work ofc)/newln    - inspire/knowledge/insight/etc...: Will give you inspiration/newln    - hack: fun little hacking display/newln    - attack <username>: it will kick that user out of the channel if it has operator privileges/newln    - leave/part: Bot leaves/parts the channel, if the bot is only in one channel, then the bot will close connection/newln    - exit: Bot Closes its connection and exits the server (if in game, then exits game and makes other commands active again.) ";
    private static String gameHelp;
    private static String gameCheatSheet;

    // Constants that define the commands the bot can recognize
    private static final String CMD_EXIT = "exit";
    private static final String CMD_PART = "part leave";
    private static final String CMD_LIST = "list";
    private static final String CMD_HELLO = "hello";
    private static final String CMD_HACK = "hack";
    private static final String CMD_ATTACK = "attack";
    private static final String CMD_HELP = "help";
    private static final String CMD_PLAY = "play";
    private static final String CMD_JOIN = "join";
    public static final String CMD_PLAY_NORTH = "north";
    public static final String CMD_PLAY_SOUTH = "south";
    public static final String CMD_PLAY_EAST = "east";
    public static final String CMD_PLAY_WEST = "west";
    private static final String CMD_PLAY_CHEATSHEET = "cheatsheet";
    private static final String CMD_JOKE = "joke";
    private static final String CMD_MOTIVATES = "motivate hype teach insight knowledge inspire etc";
    private static final String CMD_NEWS = "news";

    // Constants the are the positions in the array of items in ROOM.JAVA
    public static final int ITEM_SHOVEL = 0;
    public static final int ITEM_KEY = 1;
    public static final int ITEM_MEAT = 2;

    // Constants the are the positions in the array of rooms.
    public static final int ROOM_START = 0;
    public static final int ROOM_MID = 1;
    public static final int ROOM_CAVE = 2;
    public static final int ROOM_MOSS = 3;
    public static final int ROOM_BUILDING = 4;
    // Rooms for game
    private static Room[] rooms;
    private static int currentRoom; // Current room

    // Channel in which the bot shall join in initially. By default help
    private static String CHANNEL = "#help";

    // URL for Joke api
    private static final String URL_JOKE = "https://sv443.net/jokeapi/v2/joke/Any?blacklistFlags=nsfw,racist,sexist&format=txt";
    // URL for motivation api
    private static final String URL_MOTIVATION = "https://www.affirmations.dev/";
    // URL for News Api
    private static final String URL_NEWS_TOP = "https://newsapi.org/v2/top-headlines?country=gb&apiKey=dfdd63d38d604e4584f6392d8f8c053e";

    // Random Util to generate random numbers
    private static Random rand = new Random();

    // The HTTP client used to make HTTP requests
    private static final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

    public static void main(String[] args) throws IOException {

        System.out.println("\nPlease enter the details correct. If not then the bot will not connect successfully\n");
        // console to get the input in for the correct ip port, and channel
        Scanner console = new Scanner(System.in);
        System.out.println("What is the IP Address you would like to connect to:");
        String address = console.nextLine();
        System.out.println("What is the port you would like to connect to:");
        String port = console.nextLine();
        System.out.println("What is the channel you would like to join:");
        CHANNEL = console.nextLine();

        if (!CHANNEL.contains("#"))
            CHANNEL = "#" + CHANNEL;

        console.close();

        // Socket to connect to
        Socket socket = new Socket(address, Integer.parseInt(port));

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new Scanner(socket.getInputStream());

        nick = "BobBot"; // Nick Name
        trigger = "bb"; // Trigger that also works to use the bot

        write("Nick", nick); // use the nick name specified
        write("USER", "aumBot 8 * :aum's bot v1.0"); // Use this default username as it is most likely unique.

        write("JOIN", CHANNEL); // join the channel specified

        // Write a notice notifying everyone that the bot is in the channel
        write("NOTICE", CHANNEL + " :" + nick + " has joined the server type '" + trigger
                + " help' for list of possible commands");

        // the channel the current message came from
        String currentChannel = CHANNEL.replace("#", "");

        // boolean to check whether or not the bot should start accepting commands
        boolean active = false;
        // boolean to check whether the player is in a game.
        boolean inGame = false;

        // Number of channels the Bot is in
        int numberOfChannels = 1;

        // List of channels available
        ArrayList<String> listOfChannels = new ArrayList<String>();
        boolean toListChannels = false;

        // Inventory for the game
        Item[] inventory = new Item[3];

        // Initializing blank rooms
        rooms = new Room[5];
        rooms[ROOM_START] = new Room("start");
        rooms[ROOM_MID] = new Room("mid");
        rooms[ROOM_CAVE] = new Room("cave");
        rooms[ROOM_MOSS] = new Room("moss");
        rooms[ROOM_BUILDING] = new Room("building");

        // Help and cheatsheet for the game
        gameHelp = "You have the following commands to your disposal (commands are excluding '<>' and need to be prefixed with "
                + nick + " or " + trigger
                + " ): /newln    Type in '<direction>' as north, east, south or west /newln    take <item>: if you see an item which you would like to take /newln    inventory: to display the items you are holding/newln    look : if you want to describe your surroundings/newln    drop/throw: You can drop or throw specific items in your inventory /newln/newlnThere are more but you have to figure them out (if you type in '"
                + trigger + " cheatsheet' it will give you the commands)🕵️/newln";

        gameCheatSheet = "Extra commands" + nick + " or " + trigger
                + " ): /newln    dig: Try and dig the ground. If you have the shovel, it will automatically use the shovel/newln    unlock/open/door: will open the building door if you have the key./newln    eat <item>: you can try and eat the items you pick up/newln    die/suicide/oof : You try and kill yourself";
        while (in.hasNext()) {
            // gets the message the server sends
            String serverMessage = in.nextLine();

            // prints it out to the terminal
            System.out.println("<<< " + serverMessage);

            // Uses PONG command to return the PING the server sends
            if (serverMessage.contains("PING")) {
                write("PONG", socket.getLocalAddress().toString());
            }

            // End of Names List. This is to say that the bot is active and ready to take
            // user commands
            if (serverMessage.contains("366"))
                active = true;

            if (serverMessage.contains("322")) { // If it is listing the channels
                String[] splitMessage = serverMessage.split("#");

                if (splitMessage.length >= 2) {
                    splitMessage = splitMessage[1].split(" ");
                    listOfChannels.add("        #" + splitMessage[0] + " : " + splitMessage[1]);
                }
            }
            if (serverMessage.contains("323") && toListChannels) { // List the channels
                writeMessage("    Channels: Number of people in Channel", CHANNEL.replace("#", ""));
                for (String channel : listOfChannels) {
                    writeMessage(channel, CHANNEL.replace("#", ""));
                }
                writeMessage("    " + trigger + " join <channel> to send " + nick + " to that channel",
                        CHANNEL.replace("#", ""));
                toListChannels = false;
            }

            if (active) {

                if (serverMessage.contains("482")) {
                    writeMessage(
                            "☹️  I don't have the power to do that yet. Give me operator status and try again, or use some of my other commands ('"
                                    + trigger + " help' to show)",
                            currentChannel);

                }
                if (serverMessage.split(":").length >= 3) {
                    // Get the text input they have written
                    String[] messageSplit = serverMessage.split(":")[2].trim().split(" ");

                    if ((messageSplit[0].toLowerCase().equals(trigger.toLowerCase())
                            || messageSplit[0].toLowerCase().equals(nick.toLowerCase())) && messageSplit.length > 1) {
                        // Gets the current channel the message was received from
                        if (serverMessage.split("#").length >= 2)
                            currentChannel = serverMessage.split("#")[1].split(" ")[0];

                        // If the bot is currently not in a game
                        if (!inGame) {

                            if (messageSplit[1].toLowerCase().equals(CMD_EXIT)) { // EXIT
                                // Sends a QUIT command to the server, making the bot leave.
                                write("QUIT", "");

                            } else if (messageSplit[1].toLowerCase().equals(CMD_HELLO)) { // HELLO
                                // replies with hello <username>
                                writeMessage("Hello " + serverMessage.split(":")[1].split("!")[0], currentChannel);
                            } else if (CMD_PART.contains(messageSplit[1].toLowerCase())) { // PART/LEAVE

                                // leaves the channel if the bot is in more than one channel
                                if (numberOfChannels == 1)
                                    writeMessage("This is the only channel the bot is in. Type '" + trigger
                                            + " exit' to quit the bot.", currentChannel);
                                else {
                                    write("PART", "#" + currentChannel);
                                    numberOfChannels--;
                                }
                            } else if (messageSplit[1].toLowerCase().equals(CMD_HACK)) {

                                // a fun little hacking visual
                                String dots = "Hacking .";
                                for (int i = 0; i < 5; i++) {
                                    writeMessage(dots, currentChannel);
                                    dots += ".";
                                    try {
                                        TimeUnit.MILLISECONDS.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                                writeMessage("Server Hack successful", currentChannel);

                                writeMessage(
                                        "Please select a person to attack (type '" + trigger + " attack <name>' ): ",
                                        currentChannel);

                            } else if (messageSplit[1].toLowerCase().equals(CMD_ATTACK)) {
                                // Uses the KICK command on the user
                                if (messageSplit.length >= 3) {
                                    writeMessage("I banish you from the server " + messageSplit[2], currentChannel);
                                    write("KICK", "#" + currentChannel + " " + messageSplit[2]);
                                }
                            } else if (messageSplit[1].toLowerCase().equals(CMD_JOIN)) {
                                if (messageSplit.length >= 3) {
                                    write("JOIN", "#" + messageSplit[2].replace("#", ""));
                                    numberOfChannels++;
                                }
                            } else if (messageSplit[1].toLowerCase().equals(CMD_HELP)) {
                                writeMessage(help, currentChannel);
                            } else if (messageSplit[1].toLowerCase().equals(CMD_LIST)) {
                                toListChannels = true;
                                write("LIST", "");
                            } else if (messageSplit[1].toLowerCase().equals(CMD_JOKE)) {
                                // get joke format and write it out
                                try {

                                    String joke = sendGet(URL_JOKE);
                                    writeMessage("Joke:", currentChannel);
                                    for (String line : joke.split("\n")) {
                                        if (!line.equals(""))
                                            writeMessage("    " + line, currentChannel);
                                    }
                                } catch (Exception e) {

                                    System.out.println("Exception::: " + e);
                                    writeMessage("Try again, or restart bot for Joke functionality.", currentChannel);
                                }
                                // Maybe have my bot laugh to them as well.
                                int randInt = rand.nextInt(10);
                                if (randInt % 5 == 0) {
                                    writeMessage("HAHAH, funny isn't it " + serverMessage.split(":")[1].split("!")[0],
                                            currentChannel);
                                } else if (randInt % 8 == 0) {
                                    writeMessage("I liked that one.", currentChannel);
                                } else if (randInt % 9 == 0) {
                                    writeMessage("You like it?", currentChannel);
                                } else if (randInt % 7 == 0) {
                                    writeMessage("OOF!", currentChannel);
                                }

                            } else if (messageSplit[1].toLowerCase().equals(CMD_NEWS)) {
                                // get motivation saying, format and write it out
                                try {

                                    String newsJSON = sendGet(URL_NEWS_TOP); // Gets the news in JSON format

                                    String[] splitNewsJSON = newsJSON.split("\"articles\"");
                                    splitNewsJSON[1] = splitNewsJSON[1].substring(3, splitNewsJSON[1].length() - 1);

                                    // Splits it so it has the articles
                                    splitNewsJSON = splitNewsJSON[1].split("\"author\"");

                                    // write 5 headlines
                                    writeMessage("Top 5 headlines in the UK:", currentChannel);
                                    for (int i = 1; i < 6; i++) {

                                        // Get the title of the article
                                        int indexTitle = splitNewsJSON[i].indexOf("title");
                                        int indexEndOfTitle = splitNewsJSON[i].indexOf("description");
                                        String title = splitNewsJSON[i].substring(indexTitle + 8, indexEndOfTitle - 3);

                                        // Get the URL for the article
                                        int indexUrl = splitNewsJSON[i].indexOf("url");
                                        int indexEndOfUrl = splitNewsJSON[i].indexOf("urlToImage");
                                        String newsUrl = splitNewsJSON[i].substring(indexUrl + 6, indexEndOfUrl - 3);

                                        // Write it out. indented so it is clear.
                                        writeMessage("    " + i + ") " + title, currentChannel);
                                        writeMessage("        url: " + newsUrl, currentChannel);
                                    }

                                } catch (Exception e) {

                                    System.out.println("Exception::: " + e);
                                    writeMessage("Try again, or restart bot for news functionality.", currentChannel);
                                }

                            } else if (CMD_MOTIVATES.contains(messageSplit[1].toLowerCase())) {
                                // get motivation saying, format and write it out
                                try {

                                    String motivationJSON = sendGet(URL_MOTIVATION);

                                    String motivationString = motivationJSON.split(":")[1].split("}")[0].replace("\"",
                                            "");

                                    writeMessage("    " + motivationString, currentChannel);

                                } catch (Exception e) {
                                    System.out.println("Exception::: " + e);
                                    writeMessage("Try again, or restart bot for motivation functionality.",
                                            currentChannel);
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
                                writeMessage(gameHelp, currentChannel);
                                writeMessage(rooms[currentRoom].getDescription(), currentChannel);

                                inGame = true;
                            } else {
                                // Write what the person says
                                String tempMessage = "";
                                for (int i = 1; i < messageSplit.length; i++) {
                                    tempMessage += messageSplit[i] + " ";
                                }
                                writeMessage(tempMessage, currentChannel);
                            }
                        } else {

                            if (messageSplit[1].toLowerCase().equals(CMD_EXIT)) {
                                writeMessage("Ending Game", currentChannel);
                                inGame = false;
                            } else if (messageSplit[1].toLowerCase().equals(CMD_HELP)) {
                                writeMessage(gameHelp, currentChannel);
                            } else if (messageSplit[1].toLowerCase().equals(CMD_PLAY_CHEATSHEET)) {
                                writeMessage(gameCheatSheet, currentChannel);
                            } else {
                                String command = " ";
                                for (int i = 1; i < messageSplit.length; i++) {
                                    command += messageSplit[i] + " ";
                                }
                                String returnedMessege = rooms[currentRoom].options(command.toLowerCase(), inventory);

                                if (returnedMessege.contains("GAME OVER")
                                        || returnedMessege.contains("TO BE CONTINUED")) {
                                    writeMessage(returnedMessege, currentChannel);
                                    writeMessage("Ending Game", currentChannel);
                                    inGame = false;

                                } else if (returnedMessege.contains("MOVE")) {

                                    currentRoom = Integer.parseInt(returnedMessege.split(" ")[1]);
                                    writeMessage(rooms[currentRoom].options("look", inventory), currentChannel);

                                } else {
                                    writeMessage(returnedMessege, currentChannel);
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

    /*
     * method to send PRIVMSG to the channel given. Can accept inputs where there
     * are multiple lines.
     */
    private static void writeMessage(String message, String currentChannel) {

        String[] splitMessage = message.split("/newln");
        if (currentChannel.contains("#")) {
            currentChannel.replace("#", "");
        }
        for (String mes : splitMessage) {
            // write("PRIVMSG", CHANNEL + " :" + mes);
            write("PRIVMSG", "#" + currentChannel + " :" + mes);
        }
    }

    // method to send a message to the server
    private static void write(String command, String message) {
        String fullMessage = command + " " + message;
        System.out.println(">>> Full Message: " + fullMessage);
        out.print(fullMessage + "\r\n");
        out.flush();
    }

    // method that sends a HTTP GET request to the URL you provide
    private static String sendGet(String url) throws Exception {

        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url))
                .setHeader("User-Agent", "Java 11 HttpClient Bot").build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return (response.body());
    }

}
