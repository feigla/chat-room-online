import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

public class Client implements Runnable {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private long id;
    private long roomId;
    private static AtomicLong modCount = new AtomicLong(0);
    private User user;


    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public PrintWriter getOut() {
        return out;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setUserName(String userName) {
        user.setName(userName);
    }

    public String getUserName() {
        return user.getName();
    }

    private Client(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out =  new PrintWriter(socket.getOutputStream(), true);
        this.id = generateId();
        this.roomId = 0; // Init room
        this.user = new User("User" + "@" + Long.toString(id));
    }

    public static Client createAndAddClient(Socket socket) throws IOException {
        Client client = new Client(socket);
        Server.rooms.get(client.getRoomId()).add(client);
        return client;
    }

    public long generateId() {
        return modCount.incrementAndGet();
    }

    public void notifyConnected() {
        out.println("Your room is: " + roomId);
        CommandHandler.doSendMessageCommand("I'm connected!", this);
    }

    public void notifyDisconnected() {
        CommandHandler.doSendMessageCommand("I'm disconnected!", this);
    }


    @Override
    public void run() {
        notifyConnected();
        try {
            while (true) {
                String msg;
                msg = in.readLine();

                if (msg.startsWith(Command.STOP.commandName)) {
                    CommandHandler.doStopCommand(this);
                    break;
                } else if (msg.startsWith(Command.CREATE.commandName)) {
                    CommandHandler.doCreateCommand(this);
                } else if (msg.startsWith(Command.CONNECT.commandName)) {
                    CommandHandler.doConnectCommand(this, msg);
                } else if (msg.startsWith(Command.CHANGE_NAME.commandName)) {
                    CommandHandler.doChangeNameCommand(this);
                } else {
                    CommandHandler.doSendMessageCommand(msg, this);
                }
            }
        } catch (IOException | InterruptedException e) {}
        finally {
            notifyDisconnected();
        }
    }

}

