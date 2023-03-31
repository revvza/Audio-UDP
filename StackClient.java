import java.io.*;
import java.net.*;

import javax.sound.sampled.*;
import javax.swing.plaf.multi.MultiInternalFrameUI;
import javax.xml.transform.Source;

public class StackClient {

  static AudioInputStream ais;
  static AudioFormat format;
  static int port = 50005;
  static float rate = 44100.0f;
  
  static DataLine.Info dataLineInfo;
  static SourceDataLine sourceDataLine;

  public static void toSpeaker(byte soundbytes[]) {
    try {
      System.out.println("At speaker");
      sourceDataLine.write(soundbytes, 0, soundbytes.length);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    
    System.out.println("Client started on port: " + port);
    System.setProperty("java.net.preferIPv4Stack", "true");

    try {
      InetAddress group = InetAddress.getByName("225.6.7.8");
      MulticastSocket mSocket = new MulticastSocket(port);
      mSocket.joinGroup(group);
  
      byte [] receiveData = new byte[4096];
  
      format = new AudioFormat(rate, 16, 2, true, false);
  
      dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
      sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
      sourceDataLine.open(format);
      sourceDataLine.start();
  
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      ByteArrayInputStream baiss = new ByteArrayInputStream(receivePacket.getData());
  
      while(true) {
        mSocket.receive(receivePacket);
        ais = new AudioInputStream(baiss, format, receivePacket.getLength());
        toSpeaker(receivePacket.getData());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    

  }
}