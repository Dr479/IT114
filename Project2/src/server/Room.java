package server;

import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Room implements AutoCloseable {
    private static SocketServer server;// used to refer to accessible server functions
    private String name;
    private final static Logger log = Logger.getLogger(Room.class.getName());


    private final static String COMMAND_TRIGGER = "/";
    private final static String CREATE_ROOM = "createroom";
    private final static String JOIN_ROOM = "joinroom";
    private final static String ROLL = "roll";
    private final static String FLIP = "flip";
    private final static String MUTE = "mute";
    private final static String UNMUTE = "unmute";
    private final static String PM = "@";
    
    Random rand = new Random();

    public Room(String name) {
	this.name = name;
    }

    public static void setServer(SocketServer server) {
	Room.server = server;
    }

    public String getName() {
	return name;
    }
    

    private List<ServerThread> clients = new ArrayList<ServerThread>();

    protected synchronized void addClient(ServerThread client) {
	client.setCurrentRoom(this);
	if (clients.indexOf(client) > -1) {
	    log.log(Level.INFO, "Attempting to add a client that already exists");
	}
	else {
	    clients.add(client);
	    if (client.getClientName() != null) {
		client.sendClearList();
		sendConnectionStatus(client, true, "joined the room " + getName());
		updateClientList(client);
	    }
	}
    }

    private void updateClientList(ServerThread client) {
	Iterator<ServerThread> iter = clients.iterator();
	while (iter.hasNext()) {
	    ServerThread c = iter.next();
	    if (c != client) {
		boolean messageSent = client.sendConnectionStatus(c.getClientName(), true, null);
	    }
	}
    }

    protected synchronized void removeClient(ServerThread client) {
	clients.remove(client);
	if (clients.size() > 0) {
	    // sendMessage(client, "left the room");
	    sendConnectionStatus(client, false, "left the room " + getName());
	}
	else {
	    cleanupEmptyRoom();
	}
    }

    private void cleanupEmptyRoom() {
	// If name is null it's already been closed. And don't close the Lobby
	if (name == null || name.equalsIgnoreCase(SocketServer.LOBBY)) {
	    return;
	}
	try {
	    log.log(Level.INFO, "Closing empty room: " + name);
	    close();
	}
	catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    protected void joinRoom(String room, ServerThread client) {
	server.joinRoom(room, client);
    }
    
    
    protected void joinLobby(ServerThread client) {
	server.joinLobby(client);
    }

    /***
     * Helper function to process messages to trigger different functionality.
     * 
     * @param message The original message being sent
     * @param client  The sender of the message (since they'll be the ones
     *                triggering the actions)
     */
    private String processCommands(String message, ServerThread client) 
    {
	String response = null;
	try 
	{
	    if (message.indexOf(COMMAND_TRIGGER) > -1) 
	    {
		String[] comm = message.split(COMMAND_TRIGGER);
		log.log(Level.INFO, message);
		String part1 = comm[1];
		String[] comm2 = part1.split(" ");
		String command = comm2[0];
		if (command != null) 
		{
		    command = command.toLowerCase();
		}
		String roomName;
		switch (command) 
		{
		case CREATE_ROOM:
		    roomName = comm2[1];
		    if (server.createNewRoom(roomName)) 
		    {  
			joinRoom(roomName, client);
		    }
		    break;
		case JOIN_ROOM:
		    roomName = comm2[1];
		    joinRoom(roomName, client);
		    break;
		case FLIP:
			String coin = "";
			if (Math.random() < 0.5) {
				coin = "<b style=color:red>Flipped heads!</b>";
			} else {
				coin = "<b style=color:green>Flipped tails!</b>";
			}
			response = coin;
			break;
		case ROLL:
			Random rand = new Random();
			String[] die = new String[]{ "1", "2", "3", "4", "5", "6" };
			int idx = rand.nextInt(die.length);
			String toString = Integer.toString(idx);
			String rolling = "<b style=color:purple> Rolled a " + toString + "<b>";
			response = (rolling);
			break;
		case UNMUTE:
			String[] FirstUnMuteUser = comm2[1].split(PM);
			String UnMuteUser2 = FirstUnMuteUser[1];
			if (client.userMuteList.contains(UnMuteUser2)) {
				client.userMuteList.remove(UnMuteUser2);
				unmuteOption(client, UnMuteUser2);}
				break;	
		case MUTE:
			String[] FirstMuteUser=comm2[1].split(PM);
			String MuteUser1=FirstMuteUser[1];
			if (!client.userMuteList.contains(MuteUser1)) {
				client.userMuteList.add(MuteUser1);
				muteOption(client, MuteUser1);
			break;
			}
			
		}
		
	  }
	}
		//move over to top
	catch (Exception e) 
	{
	    e.printStackTrace();
	}
	return response;
    }

    // TODO changed from string to ServerThread
    protected void sendConnectionStatus(ServerThread client, boolean isConnect, String message) 
    {
	Iterator<ServerThread> iter = clients.iterator();
	while (iter.hasNext()) 
	{
	    ServerThread c = iter.next();
	    boolean messageSent = c.sendConnectionStatus(client.getClientName(), isConnect, message);
	    if (!messageSent) 
	    {
		iter.remove();
		log.log(Level.INFO, "Removed client " + c.getId());
	    }
	}
    }

    protected void muteOption(ServerThread client, String user) 
    {
		Iterator<ServerThread> iter = clients.iterator();
		while (iter.hasNext()) 
		{
			ServerThread n = iter.next();
			if (n.getClientName().equals(user)) 
			{
				
			}
		}
	}
    
    protected void unmuteOption(ServerThread client, String user) 
    {
				Iterator<ServerThread> iter = clients.iterator();
				while (iter.hasNext()) 
				{
					ServerThread n = iter.next();
					if (n.getClientName().equals(user)) 
					{
				
			}
		}
	}
	protected void sendMessage(ServerThread sender, String message) 
	{
		log.log(Level.INFO, getName() + ": Sending message to " + clients.size() + " clients");
		String response = processCommands(message, sender);
		if (response != null) 
		{
			message = response;
		}

		boolean PMchecker = false;
		String recipient = "";
			try {
				if (message.substring(0, 1).equals("@")) 		//if message from position 0-1 is @ then
				{
					int BeginOfDm = message.indexOf(" ");
					PMchecker = true;
					recipient = message.substring(1, BeginOfDm);
					message = " (Message for " +recipient+ "):" + message.substring(BeginOfDm);
			}
			
		}
		catch (Exception e) 
		{
			log.log(Level.INFO, "Incorrect Response");
			return;
		}

		Iterator<ServerThread> iter = clients.iterator();

		while (iter.hasNext()) 	
		{
			ServerThread client = iter.next();
			if (!client.isMuted(sender.getClientName()) && !PMchecker || client.getClientName().equals(recipient) || sender.getClientName().equals(client.getClientName())) 
			{
				boolean messageSent = client.send(sender.getClientName(), message);
				if (!messageSent) 
				{
					iter.remove();
					log.log(Level.INFO, "Removed User" + client.getId());
				}
			}
		}
		
		
	
	while (iter.hasNext()) {
		
	    ServerThread client = iter.next();
	    boolean messageSent = client.send(sender.getClientName(), message);
	    if (!messageSent) {
		iter.remove();
		log.log(Level.INFO, "Removed User " + client.getId());
	    }
	}
    }
	public List<String> getRooms() {
		return server.getRooms();
	    }
  
    @Override
    public void close() throws Exception {
	int clientCount = clients.size();
	if (clientCount > 0) {
	    log.log(Level.INFO, "Migrating " + clients.size() + " to Lobby");
	    Iterator<ServerThread> iter = clients.iterator();
	    Room lobby = server.getLobby();
	    while (iter.hasNext()) {
		ServerThread client = iter.next();
		lobby.addClient(client);
		iter.remove();
	    }
	    log.log(Level.INFO, "Done Migrating " + clients.size() + " to Lobby");
	}
	server.cleanupRoom(this);
	name = null;
	// should be eligible for garbage collection now
    }



}