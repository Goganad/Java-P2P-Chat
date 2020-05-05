import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

public class UDPListener implements Runnable{
    private Client client;
    private DatagramSocket datagramSocket;
    private byte[] buf = new byte[1024];
    private DatagramPacket packet = new DatagramPacket(buf, buf.length);


    public DatagramSocket getDatagramSocket(){
        return datagramSocket;
    }

    public UDPListener(Client client){
        this.client = client;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                datagramSocket = new DatagramSocket(this.client.getPortUDP());
                datagramSocket.receive(packet);
                byte[] buf = packet.getData();
                Message bufMsg = (Message) Utilities.getObject(buf);
                if (!bufMsg.getSenderIP().equals(this.client.getIp())) {
                    try {
                        System.out.println(bufMsg.getTime()+":"+bufMsg.getSenderNickname()+" connected");
                        this.client.changePeerList(bufMsg.getSenderIP(), bufMsg.getSenderNickname(), true);
                        Socket socket = new Socket(bufMsg.getSenderIP(), this.client.getPortTCP());
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        Message msg = new Message(Message.msgType.NAME_TRANSMISSION, this.client.getNickname(), this.client.getIp());
                        out.writeObject(msg);
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                datagramSocket.close();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("UDP server down");;
            }
        }
    }
}