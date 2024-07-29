import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

public class Client implements Runnable {
    private final Socket socket;
    private final BufferedReader in;
    public final PrintWriter out;
    private long id;
    private long roomId;
    private static AtomicLong modCount = new AtomicLong(0);
    private User user;
    public boolean isConnected;


    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out =  new PrintWriter(socket.getOutputStream(), true);
        this.id = generateId();
        this.roomId = 0; // Init room
        this.user = new User("User" + "@" + Long.toString(id));
        this.isConnected = true;
        Server.rooms.get(roomId).add(this);
    }

    public long generateId() {
        return modCount.incrementAndGet();
    }

    public void changeName() throws IOException {
        Scanner scanner = new Scanner(socket.getInputStream());
        out.println("Enter a name: ");
        user.name = scanner.nextLine();
    }

    public void notifyConnected() {
        out.println("Your room is: " + roomId);
        sendMsg("I'm connected!");
    }

    public void notifyDisconnected() {
        sendMsg("I'm disconnected!");
    }


    @Override
    public void run() {
        notifyConnected();
        try {
            while (isConnected) {
                String msg;
                msg = in.readLine();

                if (msg.startsWith(Command.STOP.commandName)) {
                    CommandHandler.doStopCommand(this);
                } else if (msg.startsWith(Command.CREATE.commandName)) {
                    CommandHandler.doCreateCommand(this);
                } else if (msg.startsWith(Command.CONNECT.commandName)) {
                    try {
                        long roomId = Integer.parseInt(msg.substring(Command.CONNECT.commandName.length()).strip());
                        CommandHandler.doConnectCommand(this, roomId);
                    } catch (NumberFormatException e) {
                        out.println("Invalid number");
                    }
                } else if (msg.startsWith(Command.CHANGE_NAME.commandName)) {
                    CommandHandler.doChangeNameCommand(this);
                } else {
                    sendMsg(msg);
                }
            }
        } catch (IOException e) {}
        finally {
            notifyDisconnected();
        }
    }

    public void sendMsg(String msg) {
        for (Client client : Server.rooms.get(roomId).getClients()) {
            client.out.println(user.name + ": " + msg);
        }
    }

}

