import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPListener implements Runnable{
    private ServerSocket srvSocket;
    private Client client;
    private Socket socket;

    public TCPListener(Client client) {
        this.client = client;
    }

    public ServerSocket getSrvSocket() {
        return srvSocket;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                srvSocket = new ServerSocket(client.getPortTCP());
                socket = srvSocket.accept();
                TCPConnection conn = new TCPConnection(socket, client);
                Thread TCPConnThread = new Thread(conn);
                TCPConnThread.start();
                srvSocket.close();
            } catch (IOException e) {
                System.out.println("TCP server down");
            }
        }
    }
}