package visualchat.server.config;
public class Prefex{
	
// Class for Prefix // 
   final private static String[] LogPrefex = {"Server" , "User" ,"DBMS"}; 
   final private static String[] SDClientPrefex = {"2010","2011" , "2012" ,"2013","2014","2015","2016","2017","2018","2019","2020","2021","2022"}; 
   
   public static String getlogPrefex(Integer PrefexNum){
    	
    	return LogPrefex[PrefexNum]+" : ";
    }
    	
    public static String getSDClientPrefex(Integer PrefexNum){
        	
        	return SDClientPrefex[PrefexNum];
    	  	
    }	
	
}