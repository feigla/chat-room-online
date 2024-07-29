import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Room {
    private List<Client> clients = new LinkedList<>();
    private static AtomicLong modCount = new AtomicLong(1);

    public static long generateRoomId() {
        return modCount.getAndIncrement();
    }

    public List<Client> getClients() {
        return clients;
    }
    public void add(Client client) {
        clients.add(client);
    }

    public void remove(Client client) {
        clients.remove(client);
    }

}
