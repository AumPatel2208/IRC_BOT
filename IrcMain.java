import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

class IrcMain {
    private static String nick;
    private static String serverName;
    private static String userName;
    private static String realName;
    private static PrintWriter out;
    private static Scanner in;

    public static void main(String[] args) throws IOException {
        Scanner console = new Scanner(System.in);

        Socket socket = new Socket("127.0.0.1", 6667);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new Scanner(socket.getInputStream());

        nick = "BobBot"; // Nick Name

        write("Nick", nick);
        write("USER", "aumBot 8 * :aum's bot v1.0");
        // Hard Coded entry to the channel
        write("JOIN", "#thebois");

        while (in.hasNext()) {
            String serverMessage = in.nextLine();
            System.out.println("<<< " + serverMessage);

            // Get the text input they have written
            if (serverMessage.split(":").length >= 3) {
                String[] messageSplit = serverMessage.split(":")[2].trim().split(" ");
                if (messageSplit[0].equals(nick)) {
                    switch (messageSplit[1]) {
                        case "hello":

                            write("PRIVMSG", "#thebois :Goodbye");
                            break;

                        default:
                            write("PRIVMSG", "#thebois :" + messageSplit[1]);
                            break;
                    }
                }
            }
        }
        in.close();
        out.close();
        socket.close();

        System.out.println("done");
    }

    private static void write(String command, String message) {
        String fullMessage = command + " " + message;
        System.out.println(">>> Full Message: " + fullMessage);
        out.print(fullMessage + "\r\n");
        out.flush();
    }
}