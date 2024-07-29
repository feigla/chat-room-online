import java.io.IOException;

public class CommandHandler {
    public static void doStopCommand(Client client) {
        client.isConnected = false;
    }
    public static void doCreateCommand(Client client) {
        long roomId = Room.generateRoomId();
        Server.rooms.put(roomId, new Room());
        Server.rooms.get(client.getRoomId()).remove(client);
        Server.rooms.get(roomId).add(client);
        client.setRoomId(roomId);
        client.notifyConnected();
    }

    public static void doConnectCommand(Client client, long roomId) {
        if (client.getRoomId() == roomId) {
            client.out.println("You are already connected");
            return;
        }
        if (Server.rooms.containsKey(roomId)) {
            Server.rooms.get(roomId).add(client);
            Server.rooms.get(client.getRoomId()).remove(client);
            client.setRoomId(roomId);
            client.notifyConnected();
        } else {
            client.out.println("Not existing room");
        }
    }

    public static void doChangeNameCommand(Client client) throws IOException {
        client.changeName();
        client.out.println("You successfully changed name!");
    }
}

