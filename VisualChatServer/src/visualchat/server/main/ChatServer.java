package visualchat.server.main;
import java.net.*;
import java.io.*;

import visualchat.server.admingui.ServerFrame;
import visualchat.server.config.Prefex;
import visualchat.server.dbms.DBMSConnection;
import visualchat.server.config.Configs;

public class ChatServer implements Runnable
{  
   
	private ChatServerThread clients[] = new ChatServerThread[Configs.MaxClient];
	private Msg_Forwarding  Msg_Forwarding;
   
	private ServerSocket server = null;                                                      
    private int clientCount = 0;  // Number Of Client //
     
// Parameter : PORT , Binding a port, --ServerFrame// 
   public ChatServer(int port)
   {  try
      { 
	     this.Msg_Forwarding = new Msg_Forwarding();	          
	     ServerFrame.displayMessage((Prefex.getlogPrefex(0)+"Binding to port " + port + ", please wait  ..."));
         server = new ServerSocket(port);  
         ServerFrame.displayMessage(Prefex.getlogPrefex(0)+"Server start : " + server);
         }
        
      catch(IOException ioe)
      {  ServerFrame.displayMessage(Prefex.getlogPrefex(0)+"Can not bind to port " + port + ": " + ioe.getMessage()); 
      }
   }
  
// Wait for a client (intinte loop) --ServerFrame//
   public void run()
   {  while (true)
      { 	   
	   try
         {  
		    ServerFrame.displayMessage(Prefex.getlogPrefex(0)+"Waiting for a client ..."); 
            addThread(server.accept()); 
            }          
             
         catch(IOException ioe)
         {   
        	 if (server.isClosed()) {
             break;  
             } else
         { ServerFrame.displayMessage(Prefex.getlogPrefex(0)+"Server accept error: " + ioe);
         } 
        	 }
	   }
   }

// Close the server --ServerFrame// 
   public void closeServer()
   {	   	     
		   try 
		   {
            for (int i =0 ; i < clientCount; i++){
            	remove(clients[i].getID()); 
            	}           	            
			server.close();
			}
		    catch (IOException ioe) {
		    ServerFrame.displayMessage(Prefex.getlogPrefex(0)+"Server closing error: " + ioe);				   		   
	  }	   	   
   }
   
// Parameter : ID , return Index of thread ,otherwise return -1// 
   private int findClient(String ID)
   {  for (int i = 0; i < clientCount; i++)
         if (clients[i].getID() == ID)
            return i;
      return -1;
   }
   
// Parameter : UID , return Index of thread,otherwise return -1 //  
   private int finduid(String UID)
   {  
	   for (int i = 0; i < clientCount; i++){
         if (clients[i].getUserID().equals(UID)){
            return i;}}
      return -1;
   }
   
// Parameter : ID,INPUT , handle the incoming messages //  
   public synchronized void handle(String ID, String input)             // input = Command(4 digit integer) + Content // 
   {   	   
	   String Command = input.substring(0,4);                          
	   String Content = input.substring(4,input.length());
	   DBMSConnection dbcon = new DBMSConnection();                               // Connect to MySQL //
	   
	   switch (Command) {
	   case "1011": 
		   CrOrLogAccount(dbcon,Content,ID,"GMAIL");
		   break;   
	   case "1012": 
		   CrOrLogAccount(dbcon,Content,ID,"FB");
		   break;
	   case "1013":  
		   attachgid(dbcon,Content,ID,"GMAIL"); 
		   break;
	   case "1014": 
		   attachgid(dbcon,Content,ID,"FB");
		   break;
	   case "1015": 
		   CheckGID(dbcon,Content,ID,"GMAIL"); 
		   break;
	   case "1016":	 
		   CheckGID(dbcon,Content,ID,"FB");
		   break;
	   case "1017":  
		   replyinvition(Content,ID,dbcon);  
		   break;
	   case "1018":  
		   Forwardinvition(Content,ID,dbcon); 
		   break;
	   case "1019": 
		   forwardMsg(ID,Content); 
           break; 
	   case "1020": 
		   stopmsg(ID); 
		   break;
	   }	   
	  dbcon.CloseCon();
	  dbcon=null;
   }
 
// Parameter : SQLConnection,Content,ID,FGID_TYPE , if FGID is found in DBMS ,return  Command + (Integer)N +"."+ UID 
// ,otherwise return Command + (Integer)N +".notfound"  //  
// Content = (Integer)N +"."+ FGID //
   public void  CheckGID(DBMSConnection dbcon,String Content,String ID,String idtype){
	  String Ndigit = Content.substring(0,Content.indexOf(".")); 
 	  String GFID = Content.substring(Content.indexOf(".")+1,Content.length()); 
 	  
 	  if (dbcon.ReturnUID(GFID,true,idtype,true)!=null){
 		  if(idtype.equals("GMAIL")){
 			 clients[findClient(ID)].send(Prefex.getSDClientPrefex(6)+Ndigit+"."+dbcon.ReturnUID(GFID,true,idtype,true)); 
 		  } else{
 			 clients[findClient(ID)].send(Prefex.getSDClientPrefex(7)+Ndigit+"."+dbcon.ReturnUID(GFID,true,idtype,true)); 
 		  } 		    
 	  } else if (idtype.equals("GMAIL")){
 		 clients[findClient(ID)].send(Prefex.getSDClientPrefex(6)+Ndigit+"."+"notfound");
 	  } else if (idtype.equals("FB")){
  		 clients[findClient(ID)].send(Prefex.getSDClientPrefex(7)+Ndigit+"."+"notfound");
  	  }
 	  }
 		  

   public void attachgid(DBMSConnection dbcon,String Content,String ID,String idtype){
	   
	   String RTUID = dbcon.ContainingUID(Content.substring(0,Content.indexOf(".")));
	   if (RTUID==null){
		   String fgid = Content.substring(Content.indexOf(".")+1,Content.length()); 		   
		   clients[findClient(ID)].send(Prefex.getSDClientPrefex(5)+dbcon.CreateUID(fgid,idtype));  
	   } else {
		   String REALUID = Content.substring(0,Content.indexOf(".")); 
		   String NEWGFID = Content.substring(Content.indexOf(".")+1,Content.length());
		   dbcon.UpdateUID(NEWGFID,REALUID,idtype); 
			   
	   }   
	   clients[findClient(ID)].setUserID(RTUID);  	  
	   ServerFrame.displayMessage(Prefex.getlogPrefex(1)+RTUID+" Login to our server ") ;	
	   
   }
   
   public void CrOrLogAccount(DBMSConnection dbcon,String Content,String ID,String idtype){
	   
	   String RTUID = dbcon.ReturnUID(Content,false,idtype,false);
	   clients[findClient(ID)].setUserID(RTUID); 
	   clients[findClient(ID)].send(Prefex.getSDClientPrefex(5)+RTUID);   	   
	   ServerFrame.displayMessage(Prefex.getlogPrefex(1)+RTUID+" Login to our server ") ;	
   
   }
   public synchronized void forwardMsg(String ID,String content){	  
	   
	   
	   String MYUID = clients[findClient(ID)].getUserID(); 
	   String TargetUID = Msg_Forwarding.getUID(MYUID);
	   clients[finduid(TargetUID)].send(Prefex.getSDClientPrefex(10)+content);  
	   ServerFrame.displayMessage(Prefex.getlogPrefex(1)+MYUID + "Told " + TargetUID + " : " + content) ;	
	   	   	   
   }
   
   public synchronized void replyinvition(String content,String ID,DBMSConnection dbcon){
	   
		  String TargetUID = content.substring(1,content.length());
		  String MYUID = clients[findClient(ID)].getUserID();  
		  
		  if (content.substring(0,1).equals("A")){
			   if (TargetUID!=null && MYUID!=TargetUID){
					  
					  if(finduid(TargetUID)!=-1){
						  
					  if (Msg_Forwarding.ElementContainingInList(TargetUID)==false) {
							       							  
							   clients[finduid(TargetUID)].send(Prefex.getSDClientPrefex(11)+MYUID);
								   clients[finduid(MYUID)].send(Prefex.getSDClientPrefex(1));		
							   Msg_Forwarding.SetElementInList(MYUID,TargetUID);
							   ServerFrame.displayMessage(Prefex.getlogPrefex(2)+ MYUID + "Accept to Draw with " + TargetUID ) ;	
							 
						  } else {
							  ServerFrame.displayMessage(Prefex.getlogPrefex(1)+ MYUID + "Typed the user "+ TargetUID + " Who is drawing ") ;
						     clients[finduid(MYUID)].send(Prefex.getSDClientPrefex(3));				  
					  }
					  } else{
						  ServerFrame.displayMessage(Prefex.getlogPrefex(1)+ MYUID + "Typed the user "+ TargetUID + " Who is offline ") ;
					  clients[finduid(MYUID)].send(Prefex.getSDClientPrefex(4));	
					  }		  	   
				  } else {
					  ServerFrame.displayMessage(Prefex.getlogPrefex(1)+ MYUID + "Typed the wrong UID " + TargetUID) ;	
					   clients[finduid(MYUID)].send(Prefex.getSDClientPrefex(0));	
				   }
			  
		 } else if (content.substring(0,1).equals("R")){
			  clients[finduid(TargetUID)].send(Prefex.getSDClientPrefex(8)+MYUID);
			  ServerFrame.displayMessage(Prefex.getlogPrefex(1)+ MYUID + "Reject to draw with " + TargetUID ) ;
		 }			  	       
   }
   
   public void Forwardinvition(String content,String ID,DBMSConnection dbcon){
	   
	  String TargetUID = content;
	  String MYUID = clients[findClient(ID)].getUserID();
	  
	   if (TargetUID!=null && MYUID.equals(TargetUID)==false){
		  
		  if(finduid(TargetUID)!=-1){
			  
			  if (Msg_Forwarding.ElementContainingInList(TargetUID)==false) {
				  
					   clients[finduid(TargetUID)].send(Prefex.getSDClientPrefex(12)+content);
					   clients[finduid(MYUID)].send(Prefex.getSDClientPrefex(2));		
					   ServerFrame.displayMessage(Prefex.getlogPrefex(1)+ MYUID + "Send the invition to " + TargetUID ) ;	
				 
			  } else {
				  ServerFrame.displayMessage(Prefex.getlogPrefex(1)+ MYUID + "Typed the user "+ TargetUID + " Who is drawing ") ;
				  clients[finduid(MYUID)].send(Prefex.getSDClientPrefex(3));				  
			  }
		  } else{
			  ServerFrame.displayMessage(Prefex.getlogPrefex(1)+ MYUID + "Typed the user "+ TargetUID + " Who is offline ") ;
			  clients[finduid(MYUID)].send(Prefex.getSDClientPrefex(4));	
		  }		  	   
	   } else {
		   ServerFrame.displayMessage(Prefex.getlogPrefex(1)+ MYUID + "Typed the wrong UID " + TargetUID) ;
		   clients[finduid(MYUID)].send(Prefex.getSDClientPrefex(0));	
	   }
	  	       
   }
   
   public synchronized void stopmsg(String ID){
	   
	   String MYUID = clients[findClient(ID)].getUserID(); 
	   String TargetUID = Msg_Forwarding.getUID(MYUID);
	   
	   Msg_Forwarding.DeleteElementInlist(MYUID);
	   clients[finduid(TargetUID)].send(Prefex.getSDClientPrefex(9));  
	   ServerFrame.displayMessage(Prefex.getlogPrefex(1)+ TargetUID + "Drawing was stopped by " + MYUID) ;	
   }
   
   
   public synchronized void remove(String ID)
   {  int pos = findClient(ID);
      if (pos >= 0)
      {  ChatServerThread toTerminate = clients[pos];
         ServerFrame.displayMessage(Prefex.getlogPrefex(1)+"Removing client thread " + ID + " at " + pos);
         if (Msg_Forwarding.ElementContainingInList(clients[findClient(ID)].getUserID())==true){stopmsg(ID);}
         if (pos < clientCount-1)
            for (int i = pos+1; i < clientCount; i++)
               clients[i-1] = clients[i];
         clientCount--;
         try
         {  toTerminate.close(); }
         catch(IOException ioe)
         {  ServerFrame.displayMessage(Prefex.getlogPrefex(1)+"Error closing thread: " + ioe); }
         toTerminate.stop(); }
      //ServerFrame.ThreadLabel.setText("Threads : "+String.valueOf(java.lang.Thread.activeCount()));
   }
   
   
   private void addThread(Socket socket)
   {  if (clientCount < clients.length)
      {  ServerFrame.displayMessage(Prefex.getlogPrefex(1)+"Client accepted: " + socket);
         clients[clientCount] = new ChatServerThread(this, socket);
         try
         {  clients[clientCount].open(); 
            clients[clientCount].start();  
            clientCount++;  
         }        
         catch(IOException ioe)
         { ServerFrame.displayMessage(Prefex.getlogPrefex(1)+"Error opening thread: " + ioe); } }
      else
    	  ServerFrame.displayMessage(Prefex.getlogPrefex(1)+"Client refused: maximum " + clients.length + " reached.");
   //ServerFrame.ThreadLabel.setText("Threads : "+String.valueOf(java.lang.Thread.activeCount()));
   }
   public static void main(String args[]) { 

   }

}