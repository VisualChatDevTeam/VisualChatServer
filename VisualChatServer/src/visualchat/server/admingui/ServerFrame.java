package visualchat.server.admingui;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import visualchat.server.main.ChatServer;
import visualchat.server.config.Configs;

public class ServerFrame extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	private static final String WindowsName =" Visual Chat Server Adminstration Panel " ;
	private static final String Bt_1_Name = " Start Server " ;
	private static final String Bt_2_Name = " Close Server " ; 
	
    private ChatServer ChatServer;
	private Thread ServerThread ; 

	private JButton StartServerBtn;
	private JButton CloseServerBtn;
	private JPanel panel;
	private JTable UserTable;
	//public static JLabel ThreadLabel;
	private static JTextArea ServerLog;
    public static UserTableModel  UserTableModel;
	
    
	// Widget Initiation // 
	public ServerFrame() {
		super(WindowsName);

		int width = 800, height = 800;
		this.setSize(width, height);
        
		panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		
		//ThreadLabel = new JLabel("Threads : "+String.valueOf(java.lang.Thread.activeCount()));
		//ThreadLabel.setVerticalTextPosition(JLabel.BOTTOM);
		//ThreadLabel.setHorizontalTextPosition(JLabel.CENTER);
		//panel.add(ThreadLabel);
    
		StartServerBtn = new JButton(Bt_1_Name);
		StartServerBtn.addActionListener(this);
		panel.add(StartServerBtn);

		CloseServerBtn = new JButton(Bt_2_Name);
		CloseServerBtn.setEnabled(false);
		CloseServerBtn.addActionListener(this);
		panel.add(CloseServerBtn);
		
		Container container=this.getContentPane();
	    UserTableModel = new UserTableModel();
	    UserTable =new JTable(UserTableModel);
		UserTable.setPreferredScrollableViewportSize(new Dimension(400,300));
		container.add(new JScrollPane(UserTable),BorderLayout.WEST);			

		ServerLog = new JTextArea();
		ServerLog.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(ServerLog,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);
		
		Toolkit tk = this.getToolkit();
		Dimension ds = tk.getScreenSize();
		this.setLocation((ds.width - width) / 2, (ds.height - height) / 2);
		this.setVisible(true);
	}
	
	// Button Press Event // 
	public void actionPerformed(ActionEvent event) {
		Object obj = event.getSource();
		if (obj == StartServerBtn) {
			StartServerBtn.setEnabled(false);
			ClickStartServerBtn();
			CloseServerBtn.setEnabled(true);
		} else if (obj == CloseServerBtn) {
			StartServerBtn.setEnabled(true);
			ClickCloseServerBtn();
			CloseServerBtn.setEnabled(false);
		}
	}
	

	// Start Server Thread --Chat Server // 
	private void ClickStartServerBtn() {
		
		if (ServerThread == null && ChatServer==null ) {
	           ChatServer = new ChatServer(Configs.BindedPort);
			   ServerThread  = new Thread(ChatServer); 
			   ServerThread.start(); 		  
			
			}

	}

	// Close Server Thread --Chat Server // 
	private void ClickCloseServerBtn() {
		try {
			if (ServerThread != null && ChatServer!=null ) {	
            
                ChatServer.closeServer();
                ServerThread.interrupt();
				ServerThread=null;
				ChatServer=null;
			}
		} catch (Exception e) {
			
		}
	}

	// Method for display Server Log (TextArea) //
	public static void displayMessage(final String messageToDisplay) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ServerLog.append(messageToDisplay + "\r\n");
			}
		});
	}
	
	// Class for JTable   // 
	public class UserTableModel extends AbstractTableModel {
				
		private List<String> columnnames = new ArrayList<String>(); 
		private List<List> userdata = new ArrayList<List>();
		   {
			columnnames.add("UID");
			columnnames.add("IP");
			columnnames.add("PORT");
	    };
		
		public void addRow(List rowData) {			
			 userdata.add(rowData);
		     fireTableRowsInserted(userdata.size() -1, userdata.size() -1);					
		}		
		public int getColumnCount() {
			return columnnames.size();
			}
		
		public int getRowCount() {
			return userdata.size();
			}
		
		public Object getValueAt(int row, int col) {
			return userdata.get(row).get(col);
			}
		
		public String getColumnName(int col) {
			try
		        {
		            return columnnames.get(col);
		        }
		        catch(Exception e)
		        {
		            return null;
		        }
			} 
		
		public void setValueAt(int col,List rowData) {
			userdata.set(col, rowData);
		    fireTableCellUpdated(userdata.size(), userdata.size());
		    }
		  
	}
	
}
