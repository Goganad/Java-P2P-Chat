import java.io.*;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Message implements Serializable{
    public enum msgType{
        CONNECTED,
        DISCONNECTED,
        MESSAGE,
        NAME_TRANSMISSION,
        HISTORY_REQUEST,
        HISTORY_TRANSMISSION
    }

    private msgType type;
    private String senderNickname;
    private InetAddress senderIP;
    private String time;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    private String text;

    public static String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    public String getTime() {
        return time;
    }

    public msgType getType() {
        return type;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public InetAddress getSenderIP() {
        return senderIP;
    }

    public Message() {
        this.type = null;
        this.senderNickname = "";
        this.senderIP = null;
        this.text = "";
        this.time = getCurrentTime();
    }

    public Message(msgType type) {
        this.type = type;
        this.senderNickname = "";
        this.senderIP = null;
        this.text = "";
        this.time = getCurrentTime();
    }

    public Message(msgType type, String senderNickname, InetAddress senderIP) {
        this.type = type;
        this.senderNickname = senderNickname;
        this.senderIP = senderIP;
        this.text = "";
        this.time = getCurrentTime();
    }

    public Message(msgType type, String senderNickname, InetAddress senderIP, String text) {
        this.type = type;
        this.senderNickname = senderNickname;
        this.senderIP = senderIP;
        this.text = text;
        this.time = getCurrentTime();
    }
}
