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
                new Thread(new TCPConnection(socket, client)).start();
                srvSocket.close();
                Thread.sleep(10);
            } catch (IOException | InterruptedException e) {
                System.out.println("TCP server down");
            }
        }
    }
}