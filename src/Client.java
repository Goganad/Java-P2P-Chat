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

    public HashMap<InetAddress, String> getPeerNicknames() {
        return peerNicknames;
    }

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

    public void addMessageToHistory(Message msg){
        this.history.add(msg);
    }

    synchronized public void changePeerList(InetAddress ip, String nickname, boolean addFlg){
        if(addFlg){
            this.peers.add(ip);
            this.peerNicknames.put(ip, nickname);
        } else {
            this.peers.remove(ip);
            this.peerNicknames.remove(ip);
        }
    }

    public static InetAddress getClientIP(){
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress("google.com", 80));
        } catch (IOException e){
            System.out.println("Couldn't get ip");
        }
        return socket.getLocalAddress();
    }

    private DatagramPacket buildUDPPacket(InetAddress adr, int portUDP){
        Message msg = new Message(Message.msgType.CONNECTED, this.nickname, this.ip);
        msg.setText(this.nickname + " has connected");
        byte[] buf = new byte[1];
        try {
            buf = Utilities.getByteArray(msg);
        } catch (IOException e){
            System.out.println("Couldn't load message");
        }
        return new DatagramPacket(buf, buf.length, adr, portUDP);
    }

    public void sendUDPBroadcast(){
        try {
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet;
            String adrString = "192.168.0.255";
            InetAddress adr = InetAddress.getByName(adrString);
            packet = buildUDPPacket(adr, this.portUDP);
            socket.send(packet);
        } catch (IOException e){
            e.printStackTrace();
        }
//        for (int i = 0; i<255; i++){
//            String adrString = "192.168.0." + i;
//            InetAddress adr = InetAddress.getByName(adrString);
//            packet = buildUDPPacket(adr, this.portUDP);
//            socket.send(packet);
//        }
    }

    public ArrayList<Message> getHistory() {
        return history;
    }

    public void sendHistory(InetAddress ip){
        try {
            Socket socket;
            for (Message msg :
                    this.history) {
                socket = new Socket(ip, this.portTCP);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(msg);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendHistoryRequest(InetAddress ip){
        Socket socket;
        try {
            socket = new Socket(ip, this.portTCP);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(new Message(Message.msgType.HISTORY_REQUEST, this.getNickname(), this.getIp()));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessage(Message msg){
        Socket socket;
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
