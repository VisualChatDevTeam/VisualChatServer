
import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.PreparedStatement; 
import java.sql.ResultSet; 
import java.sql.SQLException; 
import java.sql.Statement; 
 
public class jdbcmysql { 
  private Connection con = null;
 
  private Statement stat = null; 

  private ResultSet rs = null; 
 
  private PreparedStatement pst = null; 
  
  public jdbcmysql() 
  { 
    try { 
      Class.forName("com.mysql.jdbc.Driver"); 
   
      con = DriverManager.getConnection( 
      "jdbc:mysql://localhost/userlist?useUnicode=true&characterEncoding=Big5", 
      "root","27115112");          
    } 
    catch(ClassNotFoundException e) 
    { 
      System.out.println("DriverClassNotFound :"+e.toString()); 
    }
    catch(SQLException x) { 
      System.out.println("Exception :"+x.toString()); 
    } 
    
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
		      System.out.println("InsertDB Exception :" + e.toString()); 
		    } 
		    finally 
		    { 		     
		      Close(); 		      
		    } 
	  return rownum ;
  }
  
  
  public String CreateUID(String Gmail) 
  { String UID = null;
    try 
    { 
      Integer newrownum = Countrows()+1 ; 
      String newUID = "G0"+String.valueOf(newrownum) ;
      String sql = "INSERT INTO userlist " +
                "VALUES ( "+newrownum+",'"+newUID+"','"+Gmail+"')";
      pst = con.prepareStatement(sql); 
      pst.executeUpdate();      
      UID = newUID;
      System.out.println( "User : " + Gmail + " ,has been created and return the UID : "+ UID); 
    } 
    catch(SQLException e) 
    { 
      System.out.println("InsertDB Exception :" + e.toString()); 
    } 
    finally 
    { 
      Close(); 
    } 
    return UID;
  } 
  

  public String ReturnUID(String gmail) 
  { String UID = null;
    try 
    { 	
      boolean Found = false;
      stat = con.createStatement(); 
      rs = stat.executeQuery("select * from userlist where GMAIL='"+gmail+"'"); 
      System.out.println("User : " + gmail +" ,start verification.."); 
      while(rs.next()) 
      { 
        System.out.println( "User : " + rs.getString("GMAIL") + " ,has return the UID : "+ rs.getString("USERID")); 
        UID = rs.getString("USERID") ;
        Found = true;
      } 
      if (Found==false){
    	  UID = CreateUID(gmail);//   	   	      	    	  
      }
      System.out.println("User : " + gmail +" ,Verification End !"); 
    } 
    catch(SQLException e) 
    { 
      System.out.println("DropDB Exception :" + e.toString()); 
    } 
    finally 
    { 
      Close(); 
    } 
    return UID; 
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
      System.out.println("Close Exception :" + e.toString()); 
    } 
  } 
  public static void main(String[] args){}
}