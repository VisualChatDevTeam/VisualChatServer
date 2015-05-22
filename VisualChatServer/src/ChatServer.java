import java.net.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.*;

import com.mysql.jdbc.Connection;

public class ChatServer implements Runnable
{  private ChatServerThread clients[] = new ChatServerThread[50];
   private ServerSocket server = null;
   private Thread       thread = null;
   private int clientCount = 0;
   private Msg_Forwarding  Msg_Forwarding;

   public ChatServer(int port)
   {  try
      {  System.out.println("Binding to port " + port + ", please wait  ...");
         server = new ServerSocket(port);  
         Msg_Forwarding = new Msg_Forwarding();
         System.out.println("Server started: " + server);
         start(); }
      catch(IOException ioe)
      {  System.out.println("Can not bind to port " + port + ": " + ioe.getMessage()); }
   }
   public void run()
   {  while (true)
      {  try
         {  System.out.println("Waiting for a client ..."); 
            addThread(server.accept()); }
         catch(IOException ioe)
         {  System.out.println("Server accept error: " + ioe); stop(); }
      }
   }
   public void start()  { /* as before */ }
   public void stop()   {  }
   private int findClient(String ID)
   {  for (int i = 0; i < clientCount; i++)
         if (clients[i].getID() == ID)
            return i;
      return -1;
   }
   
   private int CheckOnline(String UID)
   {  
	   for (int i = 0; i < clientCount; i++){
         if (clients[i].getUserID().equals(UID)){
            return i;}}
      return -1;
   }
   
   public synchronized void handle(String ID, String input)
   {   
	   String Command = input.substring(0,4);
	   String Content = input.substring(4,input.length());
	   jdbcmysql dbcon = new jdbcmysql(); 
	   String Prefex_5 = "2015"; // RETURN ID ,SENDER MSG Prefix IS 2014 //
	   
	   if (Command.equals("1011")) {     	  
	   String RTUID = dbcon.ReturnUID(Content,false);
	   clients[findClient(ID)].setUserID(RTUID); 
	   clients[findClient(ID)].send(Prefex_5+RTUID);
	      // RETURN UID OR CREATE UID // 
      }
      else if (Command.equals("1012")){
    	  Forwardinvition(Content,ID,dbcon);  
    	  // SEND INVITION // 
      }
      else if (Command.equals("1013")){
    	  replyinvition(Content,ID,dbcon);  
    	  //REPLY INVITION /// 
      } 
      else if (Command.equals("1014")){
    	  forwardMsg(ID,Content); 
    	  //MSG Forwarding /// 
    	  
      } else  if (Command.equals("1015")){
    	  stopmsg(ID);
    	  //Stop Messaging ///
      }            
   }
   
   public void forwardMsg(String ID,String content){	  
	   
	   //msg send to rec  //
	   String Prefex_1 = "2019"; // IF SUCCESSFULLY RP ,RECEIVER MSG Prefix IS 2017 //
	   
	   String MYUID = clients[findClient(ID)].getUserID(); 
	   String TargetUID = Msg_Forwarding.getUID(MYUID);
	   clients[CheckOnline(TargetUID)].send(Prefex_1+content);  
	   System.out.println(MYUID + " told " + TargetUID + " : " + content) ;	
	   	   	   
   }
   
   public void replyinvition(String content,String ID,jdbcmysql dbcon){
	   
		  String TargetUID = dbcon.ReturnUID(content.substring(1,content.length()),true);
		  String MYUID = clients[findClient(ID)].getUserID();  
		  
		  String Prefex_1 = "2017"; // IF SUCCESSFULLY RP ,RECEIVER MSG Prefix IS 2017 //
		  String Prefex_2 = "2018"; // IF SUCCESSFULLY RP ,SENDER   MSG Prefix IS 2018 //
		  String Prefex_3 = "2013"; // IF NOT SUCCESSFULLY SEND(WRONG RECER) ,SENDER MSG Prefix IS 2013 //
		  String Prefex_4 = "2014"; // IF NOT SUCCESSFULLY SEND(RECER OFFLINE) ,SENDER MSG Prefix IS 2014 //
		  String Prefex_6 = "2016"; // IF NOT SUCCESSFULLY SEND(RECER IS DRAWING) ,SENDER MSG Prefix IS 2016 //
		  String Prefex_7 = "2018"; // REJECT // 
		  
		  if (content.substring(0,1).equals("A")){
			   if (TargetUID!=null && MYUID!=TargetUID){
					  
					  if(CheckOnline(TargetUID)!=-1){
						  
						  if (Msg_Forwarding.ElementContainingInList(TargetUID)==false) {
							       							  
								   clients[CheckOnline(TargetUID)].send(Prefex_1+dbcon.ReturnGmail(MYUID));
								   clients[CheckOnline(MYUID)].send(Prefex_2);		
								   Msg_Forwarding.SetElementInList(MYUID,TargetUID);
								   System.out.println(MYUID + " accept to draw with " + TargetUID ) ;	
							 
						  } else {
							  System.out.println(MYUID + " typed the user that is drawing ") ;
							  clients[CheckOnline(MYUID)].send(Prefex_6);				  
						  }
					  } else{
						  System.out.println(MYUID + " typed the user that is offline ") ;
						  clients[CheckOnline(MYUID)].send(Prefex_4);	
					  }		  	   
				   } else {
					   System.out.println(MYUID + " typed the wrong user name ") ;	
					   clients[CheckOnline(MYUID)].send(Prefex_3);	
				   }
			  
		  } else {
			  clients[CheckOnline(TargetUID)].send(Prefex_7+dbcon.ReturnGmail(MYUID));
			  System.out.println(MYUID + " reject to draw with " + TargetUID ) ;
		  }			  	       
   }
   
   public void Forwardinvition(String content,String ID,jdbcmysql dbcon){
	   
	  String TargetUID = dbcon.ReturnUID(content,true);
	  String MYUID = clients[findClient(ID)].getUserID();
	  
	  String Prefex_1 = "2011"; // IF SUCCESSFULLY SEND ,RECEIVER MSG Prefix IS 2011 //
	  String Prefex_2 = "2012"; // IF SUCCESSFULLY SEND ,SENDER   MSG Prefix IS 2012 //
	  String Prefex_3 = "2013"; // IF NOT SUCCESSFULLY SEND(WRONG RECER) ,SENDER MSG Prefix IS 2013 //
	  String Prefex_4 = "2014"; // IF NOT SUCCESSFULLY SEND(RECER OFFLINE) ,SENDER MSG Prefix IS 2014 //
	  String Prefex_6 = "2016"; // IF NOT SUCCESSFULLY SEND(RECER IS DRAWING) ,SENDER MSG Prefix IS 2016 //
	  
	   if (TargetUID!=null && MYUID.equals(TargetUID)==false){
		  
		  if(CheckOnline(TargetUID)!=-1){
			  
			  if (Msg_Forwarding.ElementContainingInList(TargetUID)==false) {
				  
					   clients[CheckOnline(TargetUID)].send(Prefex_1+dbcon.ReturnGmail(MYUID));
					   clients[CheckOnline(MYUID)].send(Prefex_2);		
					   System.out.println(MYUID + " send the invition to " + TargetUID ) ;	
				 
			  } else {
				  System.out.println(MYUID + " typed the user that is drawing ") ;
				  clients[CheckOnline(MYUID)].send(Prefex_6);				  
			  }
		  } else{
			  System.out.println(MYUID + " typed the user "+ TargetUID + " that is offline ") ;
			  clients[CheckOnline(MYUID)].send(Prefex_4);	
		  }		  	   
	   } else {
		   System.out.println(MYUID + " typed the wrong user name ") ;	
		   clients[CheckOnline(MYUID)].send(Prefex_3);	
	   }
	  	       
   }
   
   public void stopmsg(String ID){
	   
	   String Prefex_1 = "2020"; // Stop msg , and send to the anouther one //
	   
	   String MYUID = clients[findClient(ID)].getUserID(); 
	   String TargetUID = Msg_Forwarding.getUID(MYUID);
	   
	   Msg_Forwarding.DeleteElementInlist(MYUID);
	   clients[CheckOnline(TargetUID)].send(Prefex_1);  
	   System.out.println(TargetUID + " drawing was stopped by " + MYUID) ;	
   }
   
   
   public synchronized void remove(String ID)
   {  int pos = findClient(ID);
      if (pos >= 0)
      {  ChatServerThread toTerminate = clients[pos];
         System.out.println("Removing client thread " + ID + " at " + pos);
         if (Msg_Forwarding.ElementContainingInList(clients[findClient(ID)].getUserID())==true){stopmsg(ID);}
         if (pos < clientCount-1)
            for (int i = pos+1; i < clientCount; i++)
               clients[i-1] = clients[i];
         clientCount--;
         try
         {  toTerminate.close(); }
         catch(IOException ioe)
         {  System.out.println("Error closing thread: " + ioe); }
         toTerminate.stop(); }
   }
   
   
   private void addThread(Socket socket)
   {  if (clientCount < clients.length)
      {  System.out.println("Client accepted: " + socket);
         clients[clientCount] = new ChatServerThread(this, socket);
         try
         {  clients[clientCount].open(); 
            clients[clientCount].start();  
            clientCount++; }
         catch(IOException ioe)
         {  System.out.println("Error opening thread: " + ioe); } }
      else
         System.out.println("Client refused: maximum " + clients.length + " reached.");
   }
   public static void main(String args[]) { 
	   Thread t  = new Thread(new ChatServer(1519)); // 產生Thread物件
       t.start(); 
   }

}