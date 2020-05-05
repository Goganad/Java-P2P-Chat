import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPListener implements Runnable{
    private ServerSocket srvSocket;
    private Client client;

    public TCPListener(Client client) {
        this.client = client;
    }

    public ServerSocket getSrvSocket() {
        return srvSocket;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                srvSocket = new ServerSocket(client.getPortTCP());
                Socket socket = srvSocket.accept();
                Thread receiveMsg = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                            Message msg = (Message) in.readObject();
                            switch (msg.getType()) {
                                case MESSAGE:
                                    TCPListener.this.client.addMessageToHistory(msg);
                                    TCPListener.this.client.updateOutput();
                                    break;
                                case DISCONNECTED:
                                    TCPListener.this.client.changePeerList(msg.getSenderIP(), msg.getSenderNickname(), false);
                                    break;
                                case NAME_TRANSMISSION:
                                    TCPListener.this.client.changePeerList(msg.getSenderIP(), msg.getSenderNickname(), true);
                                    break;
                                case HISTORY_REQUEST:
                                    TCPListener.this.client.sendHistory();
                                    break;
                            }
                        } catch (IOException | ClassNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                });
                srvSocket.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("TCP server down");;
            }
        }
    }
}