package visualchat.server.main;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import visualchat.server.admingui.*;
import visualchat.server.admingui.ServerFrame.UserTableModel;
import visualchat.server.config.Prefex;


public class ChatServerThread extends Thread
{  private ChatServer       server    = null;
   private Socket           socket    = null;
   private String           ID        = ""  ;
   private String           UserID    = ""  ; 
   private DataInputStream  streamIn  = null;
   private DataOutputStream streamOut = null;

   public ChatServerThread(ChatServer _server, Socket _socket)
   {  super();
      server = _server;
      socket = _socket;
      ID     = socket.getRemoteSocketAddress().toString();
      ServerFrame.UserTableModel.addRow(Arrays.asList(UserID,socket.getInetAddress().toString(),String.valueOf(socket.getPort())));
   }
   public void send(String msg)
   {   try
       {  streamOut.writeUTF(msg);
          streamOut.flush();
       }
       catch(IOException ioe)
       {  ServerFrame.displayMessage(Prefex.getlogPrefex(1)+ID + " ERROR sending: " + ioe.getMessage());
          server.remove(ID);
          interrupt();
       }
   }
   public String getID()
   {  return ID;
   }
   public void run()
   {  ServerFrame.displayMessage(Prefex.getlogPrefex(1)+"Server Thread " + ID + " running.");
      while (true)
      {  try
         {  server.handle(ID, streamIn.readUTF());
         }
         catch(IOException ioe)
         {  ServerFrame.displayMessage(Prefex.getlogPrefex(1)+ID + " ERROR reading: " + ioe.getMessage());
            server.remove(ID);
            interrupt();
         }
      }
   }
   public void open() throws IOException
   {  streamIn = new DataInputStream(new 
                        BufferedInputStream(socket.getInputStream()));
      streamOut = new DataOutputStream(new
                        BufferedOutputStream(socket.getOutputStream()));
   }
   public void close() throws IOException
   {  
	   if (socket != null)    socket.close();
      if (streamIn != null)  streamIn.close();
      if (streamOut != null) streamOut.close();
   }
public String getUserID() {
	return UserID;
}
public void setUserID(String userID) {
	UserID = userID;
}
}