import jdk.swing.interop.SwingInterOpUtils;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {
    private String nickname;
    private InetAddress ip;
    private int portTCP;

    public String getNickname() {
        return nickname;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPortTCP() {
        return portTCP;
    }

    public int getPortUDP() {
        return portUDP;
    }

    private int portUDP;
    private ArrayList<InetAddress> peers;
    private HashMap<InetAddress, String> peerNicknames = new HashMap<>();
    private ArrayList<Message> history;

    public Client(String nickname, int portTCP, int portUDP) throws IOException {
        this.nickname = nickname;
        this.portTCP = portTCP;
        this.portUDP = portUDP;
        this.ip = getClientIP();
        peers = new ArrayList<>();
        history = new ArrayList<>();
    }

    public ArrayList<InetAddress> getPeers() {
        return peers;
    }


    public void sendHistory(){
        for (Message msg:
             this.history) {
            sendMessage(new Message(Message.msgType.MESSAGE, msg.getSenderNickname(), msg.getSenderIP(), msg.getText()));
        }
    }

    public void updateOutput(){
        Message msg = this.history.get(this.history.size()-1);
        if (!msg.getSenderIP().equals(this.ip)){
            System.out.println(msg.getTime()+":"+msg.getSenderNickname() + ":" + msg.getText());
        }
    }

    public void addMessageToHistory(Message msg){
        this.history.add(msg);
    }

    synchronized public void changePeerList(InetAddress ip, String nickname, boolean addFlg){
        if(addFlg){
            this.peers.add(ip);
            this.peerNicknames.put(ip, nickname);
        } else {
            this.peers.remove(ip);
        }
    }

    public static InetAddress getClientIP() throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("google.com", 80));
        return socket.getLocalAddress();
    }

    private DatagramPacket buildUDPPacket(InetAddress adr, int portUDP) throws IOException {
        Message msg = new Message(Message.msgType.CONNECTED, this.nickname, this.ip);
        msg.setText(this.nickname + " has connected");
        byte[] buf = Utilities.getByteArray(msg);
        return new DatagramPacket(buf, buf.length, adr, portUDP);
    }

    public void sendUDPBroadcast() throws IOException {
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet;
        String adrString = "192.168.0.255";
        InetAddress adr = InetAddress.getByName(adrString);
        packet = buildUDPPacket(adr, this.portUDP);
        socket.send(packet);
//        for (int i = 0; i<255; i++){
//            String adrString = "192.168.0." + i;
//            InetAddress adr = InetAddress.getByName(adrString);
//            packet = buildUDPPacket(adr, this.portUDP);
//            socket.send(packet);
//        }
    }


    public void sendMessage(Message msg){
        Socket socket;
        this.addMessageToHistory(msg);
        for (InetAddress peer: this.peers
        ) {
            try {
                socket = new Socket(peer, this.portTCP);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(msg);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }



}
