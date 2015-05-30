package visualchat.server.main;
import java.util.ArrayList;


public class Msg_Forwarding {
	
	private ArrayList<String> ListA ;
	private ArrayList<String> ListB ;
   
	public Msg_Forwarding(){
		
		ListA = new ArrayList<String>();  
		ListB = new ArrayList<String>();  								
	}

	public boolean ElementContainingInList(String UID) {
		
		boolean contain = false ;  
		if (ListA.contains(UID) || ListB.contains(UID)){
		contain = true;
		}
				
	  return contain;	
	}
	

	public void SetElementInList(String UID1,String UID2) {
		ListA.add(UID1);
	    ListB.add(UID2);
	}
	
	public void DeleteElementInlist(String UID){
		
	   if (ListA.contains(UID)){
		   ListB.remove(ListA.indexOf(UID));
		   ListA.remove(UID);
	   } else if (ListB.contains(UID)){
		   ListA.remove(ListB.indexOf(UID));
		   ListB.remove(UID);
	   }
	   		
	}	
	
	public String getUID(String UID){
		
       String RTUID =null;	
		   if (ListA.contains(UID)){
			   RTUID =ListB.get(ListA.indexOf(UID));

		   } else if (ListB.contains(UID)){
			   RTUID =ListA.get(ListB.indexOf(UID));
		   }
		return RTUID;   		
	}
}
