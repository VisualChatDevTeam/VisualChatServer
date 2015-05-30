package visualchat.server.dbms;

import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.PreparedStatement; 
import java.sql.ResultSet; 
import java.sql.SQLException; 
import java.sql.Statement; 

import visualchat.server.admingui.ServerFrame;
import visualchat.server.config.Prefex;

 
public class DBMSConnection { 
  
  private Connection con = null;
  private Statement stat = null; 
  private ResultSet rs = null; 
  private PreparedStatement pst = null;
  
  
  public DBMSConnection() 
  { 
	  con=getCon();
  }
  
  public Integer Countrows()
  {   Integer rownum = 0 ;
	  try {		
		stat = con.createStatement();
		rs = stat.executeQuery("select COUNT(*) from userlist "); 
		while(rs.next()) 
	      { 
			rownum = rs.getInt("COUNT(*)");	       
	      }
	  }
		 catch(SQLException e) 
		    { 
			 ServerFrame.displayMessage(Prefex.getlogPrefex(2)+"SelectDB Exception :" + e.toString()); 
		    } 
		    finally 
		    { 		     
		      Close(); 		      
		    } 
	  return rownum ;
  }
  
  
  public String CreateUID(String fgid,String idtype) 
  { String UID = null;
    String UIDPrefex = null;
    if (idtype.equals("FB")){
    	UIDPrefex="F0";
    	} else {
    		UIDPrefex="G0";
    	}
    try 
    { 
      String newUID = null;
      Integer newrownum = Countrows()+1 ; 
      newUID = UIDPrefex+String.valueOf(newrownum);;  
      String sql = null;
      if (idtype.equals("FB")){
    	  sql = "INSERT INTO userlist " +
                  "VALUES ( "+newrownum+",'"+newUID+"','','"+fgid+"')";
      	} else {
      		sql = "INSERT INTO userlist " +
                    "VALUES ( "+newrownum+",'"+newUID+"','"+fgid+"','')";
      	} 
           
      pst = con.prepareStatement(sql); 
      pst.executeUpdate();      
      UID = newUID;
      ServerFrame.displayMessage(Prefex.getlogPrefex(2)+"Inserting , "+ fgid + " ,(F/G ID) has been created and return the UID : "+ UID); 
    } 
    catch(SQLException e) 
    { 
    	ServerFrame.displayMessage(Prefex.getlogPrefex(2)+"InsertDB Exception :" + e.toString()); 
    } 
    finally 
    { 
      Close(); 
    } 
    return UID;
  } 
  
  public String UpdateUID(String NEWgfid,String OrginUID,String idtype) 
  { String UID = null;
    try 
    { 
      String sql = null;
    	   sql = "UPDATE userlist SET "+idtype+"='"+NEWgfid+"'"
    	       		+ " WHERE USERID='"+OrginUID+"'";
      pst = con.prepareStatement(sql); 
      pst.executeUpdate();      
      UID = NEWgfid;
      ServerFrame.displayMessage(Prefex.getlogPrefex(2)+"Updateing , "+ OrginUID + " ,(UID) has been updated his G/F id and return the G/F id : "+ NEWgfid); 
    } 
    catch(SQLException e) 
    { 
    	ServerFrame.displayMessage(Prefex.getlogPrefex(2)+"UpdateDB Exception :" + e.toString()); 
    } 
    finally 
    { 
      Close(); 
    } 
    return UID;
  } 
  
  

  public String ReturnUID(String fgid,boolean onlyfind,String idtype,boolean RTfgid ) 
  { String UID = null;
    try 
    { 	
      boolean Found = false;
      stat = con.createStatement(); 
      rs = stat.executeQuery("select * from userlist where "+idtype+"='"+fgid+"'"); 
      while(rs.next()) 
      { 
    	  ServerFrame.displayMessage(Prefex.getlogPrefex(2)+"Containing Checking , "+ rs.getString(idtype) + ",(UID) has return the UID : "+ rs.getString("USERID")+" and (F/G) id : " + rs.getString(idtype)  ); ;
        if (RTfgid==true){
        	UID = rs.getString(idtype);
        } else  {UID = rs.getString("USERID");} 
        Found = true;
      } 
      if (Found==false && onlyfind!=true ){
    	  UID = CreateUID(fgid,idtype);//   	   	      	    	  
      }
    } 
    catch(SQLException e) 
    { 
    	ServerFrame.displayMessage(Prefex.getlogPrefex(2)+"SelectDB Exception :" + e.toString()); 
    } 
    finally 
    { 
      Close(); 
    } 
    return UID; 
  } 
  
  public String ContainingUID(String UID) 
  { String RTUID = null;
    try 
    { 	
      stat = con.createStatement(); 
      rs = stat.executeQuery("select * from userlist where USERID='"+UID+"'"); 
      while(rs.next()) 
      { 
    	  ServerFrame.displayMessage(Prefex.getlogPrefex(2)+"Containing Checking , "+ rs.getString("USERID") + " ,(UID) Containing in the dbms "); 
        RTUID = rs.getString("USERID") ;
      } 
    } 
    catch(SQLException e) 
    { 
    	ServerFrame.displayMessage(Prefex.getlogPrefex(2)+"SelectDB Exception :" + e.toString()); 
    } 
    finally 
    { 
      Close(); 
    } 
    return RTUID; 
  } 
           

  public void Close() 
  { 
    try 
    { 
      if(rs!=null) 
      { 
        rs.close(); 
        rs = null; 
      } 
      if(stat!=null) 
      { 
        stat.close(); 
        stat = null; 
      } 
      if(pst!=null) 
      { 
        pst.close(); 
        pst = null; 
      } 
    } 
    
    catch(SQLException e) 
    { 
    	ServerFrame.displayMessage(Prefex.getlogPrefex(2)+"Close Exception :" + e.toString()); 
    } 
  } 
  
  public void CloseCon(){
	  
	 if (con!=null){
		 try {
			con.close();
		} catch (SQLException e) {
			ServerFrame.displayMessage(Prefex.getlogPrefex(2)+"CloseDB Exception :" + e.toString()); 
		}
		 
	 } 
	 	  	  	  
  }
  
  public Connection getCon(){
	  try { 
		  Connection Connect;
	      Class.forName("com.mysql.jdbc.Driver"); 
	   
	      Connect = DriverManager.getConnection( 
	      "jdbc:mysql://localhost/userlist?useUnicode=true&characterEncoding=Big5", 
	      "root","27115112"); 
	      return  Connect;
	    } 
	    catch(ClassNotFoundException e) 
	    { 
	      ServerFrame.displayMessage(Prefex.getlogPrefex(2)+"DriverClassNotFound :"+e.toString()); 
	      return null;
	    }
	    catch(SQLException x) { 
	      ServerFrame.displayMessage(Prefex.getlogPrefex(2)+"Exception :"+x.toString()); 
	      return null;
	    } 	  	  	  
  }
  
  public static void main(String[] args){}
}