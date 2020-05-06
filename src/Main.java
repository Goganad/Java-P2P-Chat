import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    private static boolean isActive = true;
    private static Client client;

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
            Thread.sleep(500);
            if (!client.getPeers().isEmpty()){
                client.sendHistoryRequest(client.getPeers().get(0));
            }

            while (isActive) {
                bufText = bufferedReader.readLine();
                if (!bufText.equals("/exit")) {
                    System.out.println(client.getPeers());
                    System.out.println(client.getHistory());
                    Message message = new Message(Message.msgType.MESSAGE, client.getNickname(), client.getIp(), bufText);
                    client.sendMessage(message);
                    System.out.println(message.getTime()+":"+message.getSenderNickname() + ":" + message.getText());
                    client.addMessageToHistory(message);
                } else {
                    client.sendMessage(new Message(Message.msgType.DISCONNECTED, client.getNickname(), client.getIp()));
                    isActive = false;
                    Thread.sleep(200);
                    tcpListener.getSrvSocket().close();
                    udpListener.getDatagramSocket().close();
                    udpListenerThread.interrupt();
                    tcpListenerThread.interrupt();
                }
            }
        }
    }

}
