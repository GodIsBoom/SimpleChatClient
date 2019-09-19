import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class SImpleChatClient {

    Socket sock;
    JFrame frame;
    JTextArea chatArea;
    JTextField inputArea;
    BufferedReader reader;
    PrintWriter writer;

    public void go(){
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea(15,50);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setEditable(false);
        JScrollPane chatScroller = new JScrollPane(chatArea);
        chatScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        chatScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        inputArea = new JTextField(20);

        JButton sendButton = new JButton("send");
        sendButton.addActionListener(new sendButtonListener());

        JPanel sendPanel = new JPanel();
        sendPanel.add(inputArea);
        sendPanel.add(sendButton);

        setUpNetworking();
        Thread t = new Thread(new IncomingReader());
        t.start();

        frame.getContentPane().add(BorderLayout.CENTER,chatScroller);
        frame.getContentPane().add(BorderLayout.SOUTH,sendPanel);
        frame.setSize(400,500);
        frame.setVisible(true);
    }
    public class sendButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                writer.println(inputArea.getText());
                writer.flush();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            inputArea.setText("");
            inputArea.requestFocus();
            chatArea.append(inputArea.getText()+"\n");
        }
    }
    private void setUpNetworking(){
        try {
            sock = new Socket("127.0.0.1",4242);
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("Network established");

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public class IncomingReader implements Runnable{
        @Override
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null){
                    System.out.println("read "+message);
                    chatArea.append(message+"\n");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {

        SImpleChatClient client = new SImpleChatClient();
        client.go();
    }
}
