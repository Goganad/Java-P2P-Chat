import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    private static boolean isActive = true;
    private static Client client;
    private static Message msg;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Welcome to chat!");
        System.out.println("Enter your nickname");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            client = new Client(bufferedReader.readLine(), 8844, 8855);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(Message.getCurrentTime());
        if (!client.equals(null)) {
            String bufText;
            TCPListener tcpListener = new TCPListener(client);
            Thread tcpListenerThread = new Thread(tcpListener);
            tcpListenerThread.start();

            UDPListener udpListener = new UDPListener(client);
            Thread udpListenerThread = new Thread(udpListener);
            udpListenerThread.start();

            client.sendUDPBroadcast();
            if (!client.getPeers().isEmpty()){
                client.sendMessage(new Message(Message.msgType.HISTORY_REQUEST, client.getNickname(), client.getIp()));
                //client.printHistory();
            }

            while (isActive) {
                bufText = bufferedReader.readLine();
                if (!bufText.equals("/exit")) {
                    client.sendMessage(new Message(Message.msgType.MESSAGE, client.getNickname(), client.getIp(), bufText));
                    client.updateOutput();
                } else {
                    client.sendMessage(new Message(Message.msgType.DISCONNECTED, client.getNickname(), client.getIp(), bufText));
                    isActive = false;
                    tcpListener.getSrvSocket().close();
                    udpListener.getDatagramSocket().close();
                    tcpListenerThread.interrupt();
                    udpListenerThread.interrupt();
                }
            }
        }
    }

}
