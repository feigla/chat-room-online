import java.io.IOException;
import java.util.Scanner;

public class CommandHandler {
    public static void doCreateCommand(Client client) {
        long roomId = Room.generateRoomId();
        Server.rooms.put(roomId, new Room());
        Server.rooms.get(client.getRoomId()).remove(client);
        Server.rooms.get(roomId).add(client);
        client.setRoomId(roomId);
        client.notifyConnected();
    }

    public static void doConnectCommand(Client client, String msg) throws InterruptedException {
        try {
            long roomId = Integer.parseInt(msg.substring(Command.CONNECT.commandName.length()).strip());
            if (client.getRoomId() == roomId) {
                client.getOut().println("You are already connected");
                return;
            }
            if (Server.rooms.containsKey(roomId)) {
                Server.rooms.get(roomId).add(client);
                Server.rooms.get(client.getRoomId()).remove(client);
                client.setRoomId(roomId);
                client.notifyConnected();
            } else {
                client.getOut().println("Not existing room");
            }
        } catch (NumberFormatException e) {
            client.getOut().println("Invalid number");
        }
    }

    public static void doChangeNameCommand(Client client) throws IOException {
        Scanner scanner = new Scanner(client.getSocket().getInputStream());
        client.getOut().println("Enter a name: ");
        client.setUserName(scanner.nextLine());
        client.getOut().println("You successfully changed name!");
    }

    public static void doSendMessageCommand(String msg, Client client) {
        for (Client clientReceiver : Server.rooms.get(client.getRoomId()).getClients()) {
            clientReceiver.getOut().println(client.getUserName() + ": " + msg);
        }
    }
}

