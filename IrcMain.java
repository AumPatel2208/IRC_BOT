import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

class IrcMain {
    private static String nick;
    private static String serverName;
    private static String userName;
    private static String realName;
    private static PrintWriter out;
    private static Scanner in;

    private static final String CMD_EXIT = "exit";
    private static final String CMD_HELLO = "hello";
    private static final String CMD_HACK = "hack";
    private static final String CMD_ATTACK = "attack";
    // private static final String CHANNEL = "#thebois";
    private static final String CHANNEL = "#Goethe";

    public static void main(String[] args) throws IOException {
        Scanner console = new Scanner(System.in);

        Socket socket = new Socket("127.0.0.1", 6667);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new Scanner(socket.getInputStream());

        nick = "BobBot"; // Nick Name

        write("Nick", nick);
        write("USER", "aumBot 8 * :aum's bot v1.0");
        // Hard Coded entry to the channel
        // write("JOIN", "#thebois");
        write("JOIN", CHANNEL);
        boolean active = false;
        String[] names = new String[0]; // names

        while (in.hasNext()) {
            String serverMessage = in.nextLine();

            System.out.println("<<< " + serverMessage);

            if (serverMessage.contains("353")) {// Names List
                names = serverMessage.split(":")[2].trim().split(" "); // got the names
                // for (String name : names) {
                // System.out.println(name);
                // }
            }

            if (serverMessage.contains("366")) // End of Names List
                active = true;
            if (active) {
                if (serverMessage.split(":").length >= 3) {
                    // Get the text input they have written
                    String[] messageSplit = serverMessage.split(":")[2].trim().split(" ");
                    if (messageSplit[0].toLowerCase().equals(nick.toLowerCase()) && messageSplit.length > 1) {
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
                        } else {
                            // Write what the person says
                            writeMessage(messageSplit[1]);
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
