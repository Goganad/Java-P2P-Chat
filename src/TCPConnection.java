import jdk.jshell.execution.Util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class TCPConnection implements Runnable{
    private Socket socket = null;
    private Client client = null;

    public TCPConnection(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(this.socket.getInputStream());
            Message msg = (Message) in.readObject();
            switch (msg.getType()) {
                case MESSAGE:
                    this.client.addMessageToHistory(msg);
                    System.out.println(msg.getTime()+":"+msg.getSenderNickname() + ":" + msg.getText());
                    break;
                case DISCONNECTED:
                    this.client.changePeerList(msg.getSenderIP(), msg.getSenderNickname(), false);
                    System.out.println(msg.getTime()+":"+msg.getSenderNickname()+" disconnected");
                    break;
                case NAME_TRANSMISSION:
                    this.client.changePeerList(msg.getSenderIP(), msg.getSenderNickname(), true);
                    break;
                case HISTORY_REQUEST:
                    this.client.sendHistory(msg.getSenderIP());
                case HISTORY_TRANSMISSION:
                    Message message = (Message) Utilities.getObject(Utilities.getByteArray(msg.getText()));
                    this.client.addMessageToHistory(message);
                    System.out.println(msg.getTime()+":"+message.getSenderNickname() + ":" + message.getText());
                    break;
            }
            in.close();
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}