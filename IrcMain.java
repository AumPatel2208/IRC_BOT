
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
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

    private static final String help = "Commands:/newln    - hello: try it/newln    - play: play an adventure game/newln    - news: get the current top 5 news articles/newln    - joke: tells you a joke (safe for work ofc)/newln    - inspire/knowledge/insight/etc...: Will give you inspiration/newln    - hack: fun little hacking display/newln    - attack <username>: try it :)/newln    - exit: Exit Channel (if in game, then exits game and makes other commands active again.) ";
    private static String gameHelp;
    private static String gameCheatSheet;
    private static final String CMD_EXIT = "exit";
    private static final String CMD_HELLO = "hello";
    private static final String CMD_HACK = "hack";
    private static final String CMD_ATTACK = "attack";
    private static final String CMD_HELP = "help";
    private static final String CMD_TIME = "time";
    private static final String CMD_TIME_ZONES = "zones";
    private static final String CMD_PLAY = "play";
    public static final String CMD_PLAY_NORTH = "north";
    public static final String CMD_PLAY_SOUTH = "south";
    public static final String CMD_PLAY_EAST = "east";
    public static final String CMD_PLAY_WEST = "west";
    private static final String CMD_PLAY_CHEATSHEET = "cheatsheet";
    private static final String CMD_JOKE = "joke";
    private static final String CMD_MOTIVATES = "motivate hype teach insight knowledge inspire etc";
    private static final String CMD_NEWS = "news";
    public static final int ITEM_SHOVEL = 0;
    public static final int ITEM_KEY = 1;
    public static final int ITEM_MEAT = 2;

    public static final int ROOM_START = 0;
    public static final int ROOM_MID = 1;
    public static final int ROOM_CAVE = 2;
    public static final int ROOM_MOSS = 3;
    public static final int ROOM_BUILDING = 4;

    private static final String CHANNEL = "#thebois";
    // private static final String CHANNEL = "#help";
    // private static final String CHANNEL = "#Goethe";

    // Rooms for game
    private static Room[] rooms;
    private static int currentRoom;

    // URL for Joke api
    private static final String URL_JOKE = "https://sv443.net/jokeapi/v2/joke/Any?blacklistFlags=nsfw,racist,sexist&format=txt";
    // URL for motivation api
    private static final String URL_MOTIVATION = "https://www.affirmations.dev/";
    // URL for News Api
    private static final String URL_NEWS_TOP = "https://newsapi.org/v2/top-headlines?country=gb&apiKey=dfdd63d38d604e4584f6392d8f8c053e";

    // Random Util to generate random numbers
    private static Random rand = new Random();

    // one instance, reuse
    private static final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

    private static String sendGet(String url) throws Exception {

        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url))
                .setHeader("User-Agent", "Java 11 HttpClient Bot").build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // print status code
        // System.out.println(response.statusCode());

        // print response body
        // System.out.println(response.body());

        return (response.body());
    }

    public static void main(String[] args) throws IOException {
        Scanner console = new Scanner(System.in);

        Socket socket = new Socket("127.0.0.1", 6667);
        // Socket socket = new Socket("127.0.0.1", 7777);

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
        gameHelp = "You have the following commands to your disposal (commands are excluding '<>' and need to be prefixed with "
                + nick + " or " + trigger
                + " ): /newln    Type in '<direction>' as north, east, south or west /newln    take <item>: if you see an item which you would like to take /newln    inventory: to display the items you are holding/newln    look : if you want to describe your surroundings/newln    drop/throw: You can drop or throw specific items in your inventory /newln/newlnThere are more but you have to figure them out (if you type in 'bb cheatsheet' it will give you the commands)🕵️/newln";

        gameCheatSheet = "Extra commands" + nick + " or " + trigger
                + " ): /newln    dig: Try and dig the ground. If you have the shovel, it will automatically use the shovel/newln    unlock/open/door: will open the building door if you have the key./newln    eat <item>: you can try and eat the items you pick up/newln    die/suicide/oof : You try and kill yourself";
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
                // URL url = new URL(URL_JOKE);
                // HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
                            } else if (messageSplit[1].toLowerCase().equals(CMD_HELP)) {
                                writeMessage(help);
                            } else if (messageSplit[1].toLowerCase().equals(CMD_JOKE)) {
                                // get joke format and write it out
                                try {

                                    String joke = sendGet(URL_JOKE);
                                    writeMessage("Joke:");
                                    for (String line : joke.split("\n")) {
                                        if (!line.equals(""))
                                            writeMessage("    " + line);
                                    }
                                } catch (Exception e) {
                                    // TODO: handle exception
                                    System.out.println("Exception::: " + e);
                                    writeMessage("Try again, or restart bot for Joke functionality.");
                                }
                                // Maybe have my bot laugh to them as well.
                                int randInt = rand.nextInt(10);
                                if (randInt % 5 == 0) {
                                    writeMessage("HAHAH, funny isn't it " + serverMessage.split(":")[1].split("!")[0]);
                                } else if (randInt % 8 == 0) {
                                    writeMessage("I liked that one.");
                                } else if (randInt % 9 == 0) {
                                    writeMessage("You like it?");
                                } else if (randInt % 7 == 0) {
                                    writeMessage("OOF!");
                                }

                            } else if (messageSplit[1].toLowerCase().equals(CMD_NEWS)) {
                                // get Motivatoin saying, format and write it out
                                try {

                                    String newsJSON = sendGet(URL_NEWS_TOP); // Gets the news in JSON format

                                    String[] splitNewsJSON = newsJSON.split("\"articles\"");
                                    // System.out.println("\nbefore: " + splitNewsJSON[1] + "\n");
                                    splitNewsJSON[1] = splitNewsJSON[1].substring(3, splitNewsJSON[1].length() - 1);

                                    // System.out.println("\nMinus square bracket: " + splitNewsJSON[1] + "\n");

                                    // Splits it so it has the articles
                                    splitNewsJSON = splitNewsJSON[1].split("\"author\"");

                                    // write 5 headlines
                                    writeMessage("Top 5 headlines in the UK:");
                                    for (int i = 1; i < 6; i++) {
                                        // System.out.println("\n Article: " + splitNewsJSON[i] + "\n");

                                        // Get the title of the article
                                        int indexTitle = splitNewsJSON[i].indexOf("title");
                                        int indexEndOfTitle = splitNewsJSON[i].indexOf("description");
                                        String title = splitNewsJSON[i].substring(indexTitle + 8, indexEndOfTitle - 3);

                                        // Get the URL for the article
                                        int indexUrl = splitNewsJSON[i].indexOf("url");
                                        int indexEndOfUrl = splitNewsJSON[i].indexOf("urlToImage");
                                        String newsUrl = splitNewsJSON[i].substring(indexUrl + 6, indexEndOfUrl - 3);

                                        // Write it out. indented so it is clear.
                                        writeMessage("    " + i + ") " + title);
                                        writeMessage("        url: " + newsUrl);
                                    }

                                } catch (Exception e) {
                                    // TODO: handle exception
                                    System.out.println("Exception::: " + e);
                                    writeMessage("Try again, or restart bot for news functionality.");
                                }

                            } else if (CMD_MOTIVATES.contains(messageSplit[1].toLowerCase())) {
                                // get Motivatoin saying, format and write it out
                                try {

                                    String motivationJSON = sendGet(URL_MOTIVATION);

                                    String motivationString = motivationJSON.split(":")[1].split("}")[0].replace("\"",
                                            "");

                                    writeMessage("    " + motivationString);

                                } catch (Exception e) {
                                    // TODO: handle exception
                                    System.out.println("Exception::: " + e);
                                    writeMessage("Try again, or restart bot for motivation functionality.");
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
                                writeMessage(gameHelp);
                                writeMessage(rooms[currentRoom].getDescription());

                                inGame = true;
                            } else if (messageSplit[1].toLowerCase().equals("test")) {

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
                            } else if (messageSplit[1].toLowerCase().equals(CMD_HELP)) {
                                writeMessage(gameHelp);
                            } else if (messageSplit[1].toLowerCase().equals(CMD_PLAY_CHEATSHEET)) {
                                writeMessage(gameCheatSheet);
                            } else {
                                String command = " ";
                                for (int i = 1; i < messageSplit.length; i++) {
                                    command += messageSplit[i] + " ";
                                }
                                String returnedMessege = rooms[currentRoom].options(command.toLowerCase(), inventory);

                                // writeMessage(returnedMessege);

                                if (returnedMessege.contains("GAME OVER")
                                        || returnedMessege.contains("TO BE CONTINUED")) {
                                    writeMessage(returnedMessege);
                                    writeMessage("Ending Game");
                                    inGame = false;

                                } else if (returnedMessege.contains("MOVE")) {

                                    currentRoom = Integer.parseInt(returnedMessege.split(" ")[1]);
                                    writeMessage(rooms[currentRoom].options("look", inventory));

                                } else {
                                    writeMessage(returnedMessege);
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

    // // method to send PRIVMSG to #thebois (should change so it is dynamic)
    // private static void writeMessage(String message) {
    // String[] splitMessage = message.split("/newln");
    // for (String mes : splitMessage) {
    // writeMessage(mes);
    // }
    // }

    // method to send PRIVMSG to #thebois (should change so it is dynamic)
    private static void writeMessage(String message) {

        String[] splitMessage = message.split("/newln");
        for (String mes : splitMessage) {
            write("PRIVMSG", CHANNEL + " :" + mes);
        }
    }

    // method to send write a message
    private static void write(String command, String message) {
        String fullMessage = command + " " + message;
        System.out.println(">>> Full Message: " + fullMessage);
        out.print(fullMessage + "\r\n");
        out.flush();
    }
}
