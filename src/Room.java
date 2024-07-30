import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class Room {
    private LinkedBlockingQueue<Client> clients = new LinkedBlockingQueue<>();
    private static AtomicLong modCount = new AtomicLong(1);

    public static long generateRoomId() {
        return modCount.getAndIncrement();
    }

    public LinkedBlockingQueue<Client> getClients() {
        return new LinkedBlockingQueue<>(clients);
    }

    public void add(Client client) {
        try {
            clients.put(client);
        } catch (InterruptedException e) {}
    }

    public void remove(Client client) {
        clients.remove(client);
    }

}
