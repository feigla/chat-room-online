import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket server;
    public static Map<Long, Room> rooms = new ConcurrentHashMap<>();
    private static final int PORT = 8080;
    private static final int COUNT_OF_USERS = 1000;
    private static final Executor exec = Executors.newFixedThreadPool(COUNT_OF_USERS);

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        try {
            Server.rooms.put(0L, new Room());
            for (int i = 0; i < COUNT_OF_USERS; i++) {
                Socket socket = server.accept();
                exec.execute(new Client(socket));
            }
        } finally {
            server.close();
        }
    }
}
