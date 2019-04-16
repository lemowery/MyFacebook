/*
 * Name: Levi Mowery
 * ID: lemowery
 * WVU Number: 800096308
 * Programming Assignment 2
 * Date: 04/16/2019
 * 
 * STATUS
 * All features working properly, no extra-credit is implemented
 * 	
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class access {

	public static void main(String[] args) throws IOException {
		
		HashMap<String, ArrayList<String>> lists, pictures;
		String profileOwner = null;
		String currentUser = null;
		
		if (args.length != 1) {
			System.err.println("Program must be executed with input file name: ./access filename.txt");
			return;
		}
		
		List<String> inputFileContents = readFile(args[0]);
		
		initializeFiles();
		
		// Initialize maps
		lists = new HashMap<String, ArrayList<String>>();
		pictures = new HashMap<String, ArrayList<String>>();
		
		if(inputFileContents.size() >= 2 && inputFileContents.get(0).split(" ")[0].equalsIgnoreCase("friendadd") && inputFileContents.get(1).split(" ")[0].equalsIgnoreCase("viewby")) {				
			
			for (String string : inputFileContents) {
					
				String[] split = string.split(" ");
					
					switch (split[0]) {
					
					case "friendadd":
						
						// Ensure only profile owner can add friends, unless first time
						if(!(currentUser == null && profileOwner == null) && !currentUser.equals(profileOwner)) {
							System.err.println("Cannot add friends unless profile owner.");
							log("Cannot add friends unless profile owner.");
							break;
						}
						
						friendAdd(string);
						
						// If profile owner not set, set it
						if (profileOwner == null) {
							profileOwner = split[1];
						}
						
						break;
					
					case "viewby":
						
						// If successful, change the current user
						String temp = viewBy(string);
						if (temp != null) {
							currentUser = temp;
						}
						
						break;
						
					case "logout":
						
						logout();
						currentUser = null;
						
						break;
					
					case "listadd":
						
						if(!currentUser.equals(profileOwner)) {
							System.err.println("ERROR: Only profile owner can add new lists.");
							log("ERROR: Only profile owner can add new lists.");
							break;
						}
						
						if(split[1].equals("nil")) {
							System.err.println("ERROR: nil is a reserved name which cannot be used for list names.");
							log("ERROR: nil is a reserved name which cannot be used for list names.");
							break;
						}
						
						listAdd(string, lists);
						
						break;
					
					case "friendlist":
						
						if (!currentUser.equals(profileOwner)) {
							System.err.println("ERROR: friendlist command only useable by profile owner.");
							log("ERROR: friendlist command only useable by profile owner.");
							break;
						}
						
						friendList(string, lists);
						
						break;
						
					case "postpicture":
						
						if(currentUser == null) {
							System.err.println("ERROR: Must be someone viewing profile.");
							log("ERROR: Must be someone viewing profile.");	
							break;
						}
						
						postPicture(string, pictures, currentUser);
						
						break;
					
					case "chlst":
						
						chlst(string, currentUser, profileOwner, pictures, lists);
						
						break;
					
					case "chmod":
						
						chmod(string, currentUser, profileOwner, pictures);
						
						break;
						
					case "chown":
						
						chown(string, profileOwner, currentUser, pictures);
						
						break;
						
					case "readcomments":
						
						readComments(string, currentUser, profileOwner, pictures, lists);
						
						break;
					
					case "writecomments":
						writeComments(string, currentUser, profileOwner, pictures, lists);
						break;
						
					case "end":
						end(lists, pictures);
						break;	

					default:
						break;
						
					}
				}			
			}
		
		else {
			System.err.print("ERROR: Beginning arguments must be friendadd, followed by viewby.\n");
		}
	}
	
	public static List<String> readFile (String fileName) {
		/*
		 * Method used to read the contents of a file and return its line-by-line contents
		 * as a list of strings
		 */
		
		List<String> contents = new ArrayList<String>();
		
		  try {
		    BufferedReader reader = new BufferedReader(new FileReader(fileName));
		    String line;
		    while ((line = reader.readLine()) != null) {
		      contents.add(line);
		    }
		    reader.close();
		    return contents;
		  }
		  catch (Exception e) {
		    System.err.format("Exception occurred trying to read '%s'.", fileName);
		    e.printStackTrace();
		    return null;
		  }
	}
	
	public static void friendAdd(String command) throws IOException {
		/*
		 * Adds friend to friend file
		 * Ensures no duplicate friends
		 */
		
		List<String> friendList = readFile("friends.txt");
		String name = command.split(" ")[1];
		
		// Ensure no duplicate friends
		if (friendList.contains(name)) {
			System.err.format("ERROR: Friend %s already exists.\n", name);		
			log(String.format("ERROR: Friend %s already exists.", name));
			return;
		}

		// Add friend to friend file
		FileWriter writer = new FileWriter("friends.txt", true);
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		bufferedWriter.write(name + "\n");
		bufferedWriter.close();
		System.out.format("%s added to friends.\n", name);		
		log(String.format("%s added to friends.", name));
		
	}
	
	public static String viewBy(String command) throws IOException {
		/*
		 * Ensures friend exists
		 * Returns new currentUser
		 */
		
		List<String> friendList = readFile("friends.txt");	
		String name = command.split(" ")[1];
		
		// Ensure valid friend name
		if (!friendList.contains(name)) {	
			System.err.format("ERROR: %s is not on the owner's friend list.\n", name);		
			log(String.format("ERROR: %s is not on the owner's friend list.", name));
			return null;	
		}
		
		System.out.format("Friend %s views the profile.\n", name);
		log(String.format("Friend %s views the profile.", name));
		return name;		
		
	}
	
	public static void logout() throws IOException {
		/*
		 * Simply logs logout
		 */
		
		System.out.println("A friend or you no longer view the profile.");
		log("A friend or you no longer view the profile.");
	
	}
	
	public static void listAdd(String command, HashMap<String, ArrayList<String>> map) throws IOException {
		/*
		 * Creates new list, adds list to listMap with no members
		 */
		
		String name = command.split(" ")[1];
		
		// Ensure no duplicate lists
		if(map.containsKey(name)) {
			System.err.format("ERROR: List %s already exists.\n", name);
			log(String.format("ERROR: List %s already exists.", name));
			return;
		}
		
		// Create new list and add it to memory
		ArrayList<String> list  = new ArrayList<String>();
		map.put(name, list);
		
		System.out.format("List %s created.\n", name);
		log(String.format("List %s created.", name));
		
	}
	
	public static void friendList(String command, HashMap<String, ArrayList<String>> map) throws IOException {
		/*
		 * Adds friend to given list, updates list map
		 */
		
		List<String> friendList = readFile("friends.txt");
		
		String friendName = command.split(" ")[1];
		String listName = command.split(" ")[2];
		
		// Ensure friend name is valid 
		if (!friendList.contains(friendName)) {
			System.err.format("ERROR: Friend %s is not in friends list.\n", friendName);
			log(String.format("ERROR: Friend %s is not in friends list.", friendName));
			return;
		}
		
		// Ensure list name is valid
		if (!map.containsKey(listName)) {
			System.err.format("ERROR: List %s does not exist.\n", friendName);
			log(String.format("ERROR: List %s does not exist.", friendName));
			return;
		}
		
		// Add friend to list
		map.get(listName).add(friendName);
		
		System.out.format("Friend %s added to list %s.\n", friendName, listName);
		log(String.format("Friend %s added to list %s.\n", friendName, listName));
		
	}
	
	public static void postPicture(String command, HashMap<String, ArrayList<String>> map, String owner) throws IOException {
		/*
		 * Creates picture and adds it to the pictureMap
		 * Stored in map as such: pictureName --> [owner, list, permissions]
		 */
		
		String pictureFile = command.split(" ")[1];
		String pictureName = pictureFile.replace(".txt", "");
		
		// Create list with default properties
		ArrayList<String> properties = new ArrayList<String>();
		properties.add(0, owner);
		properties.add(1, "nil");
		properties.add(2, "rx -- --");
		
		// Create file for picture
    	createPicFile(pictureName + ".txt");
		
		// Add picture to in-memory storage
		map.put(pictureName, properties);
		System.err.format("Picture %s.txt with owner %s and default permissions has been posted.\n", pictureName, owner);
		log(String.format("Picture %s.txt with owner %s and default permissions has been posted.", pictureName, owner));
		
	}
	
	public static void chlst(String command, String currentUser, String profileOwner, HashMap<String, ArrayList<String>> pictures, HashMap<String, ArrayList<String>> lists) throws IOException {
		/*
		 * Changes current list associated with a picture
		 * Only picture owner or profile owner can execute
		 * Can only change the list to one the owner is a member of
		 */
		
		String picName = command.split(" ")[1].replace(".txt", "");
		String listName = command.split(" ")[2];
		
		// Ensure picture exists
		if (pictures.get(picName) == null) {
			System.err.format("Picture %s.txt does not exist1.\n", picName);
			log(String.format("Picture %s.txt does not exist1.", picName));
			return;
		}
		
		// Ensure list exists
		if (lists.get(listName) == null) {
			System.err.format("List %s does not exist.\n", listName);
			log(String.format("List %s does not exist.", listName));
			return;
		}
		
		// Ensure someone is viewing the profile
		if (currentUser == null) {
			System.err.println("Must be someone viewing the profile to use readComments.\n");
			log("Must be someone viewing the profile to use readComments.");
		}
		
		// Ensure user is profile owner or picture owner
		if (currentUser.equals(profileOwner) || pictures.get(picName).get(0).equals(currentUser)) {
	
			// Ensure user is profile owner or member of list 
			if (currentUser.equals(profileOwner) || lists.get(listName).contains(currentUser)) {
				
				pictures.get(picName).set(1, listName);
				System.err.format("Picture %s.txt list changed to %s.\n", picName, listName);
				log(String.format("Picture %s.txt list changed to %s.", picName, listName));
				return;
				
			}
			
			System.err.format("ERROR: Current user %s is not a member of the desired list %s.\n", currentUser, listName);
			log(String.format("ERROR: Current user %s is not a member of the desired list %s.", currentUser, listName));
			return;
			
		}
		
		System.err.format("ERROR: Current user %s is neither the profile owner nor the picture owner.\n", currentUser);
		log(String.format("ERROR: Current user %s is neither the profile owner nor the picture owner.", currentUser));

	}
	
	public static void chmod(String command, String currentUser, String profileOwner, HashMap<String, ArrayList<String>> pictures) throws IOException {
		/*
		 * Changes current permissions associated with a picture
		 * Only picture owner or profile owner can execute
		 */
		
		String picName = command.split(" ")[1].replace(".txt", "");
		String permissions = command.split(" ", 3)[2];
		
		// Ensure picture exists
		if (pictures.get(picName) == null) {
			System.err.format("Picture %s.txt does not exist.\n", picName);
			log(String.format("Picture %s.txt does not exist.", picName));
			return;
		}
		
		// Ensure someone is viewing the profile
		if (currentUser == null) {
			System.err.println("Must be someone viewing the profile to use readComments.\n");
			log("Must be someone viewing the profile to use readComments.");
		}
				
		// Ensure user is profile owner or picture owner
		if (currentUser.equals(profileOwner) || pictures.get(picName).get(0).equals(currentUser)) {
			
			pictures.get(picName).set(2, permissions);
			System.err.format("Picture %s.txt permissions changed to %s.\n", picName, permissions);
			log(String.format("Picture %s.txt permissions changed to %s.", picName, permissions));
			return;
				
		}
		
		System.err.format("ERROR: Current user %s is neither the profile owner nor the picture owner.\n", currentUser);
		log(String.format("ERROR: Current user %s is neither the profile owner nor the picture owner.", currentUser));

	}
	
	public static void chown(String command, String profileOwner, String currentUser, HashMap<String, ArrayList<String>> pictures) throws IOException {
		/*
		 * Changes current owner of picture
		 * Can only be executed by profile owner
		 */
		
		String picName = command.split(" ")[1].replace(".txt", "");
		String newUser = command.split(" ")[2];
		
		// Ensure picture exists
		if (pictures.get(picName) == null) {
			System.err.format("Picture %s.txt does not exist.\n", picName);
			log(String.format("Picture %s.txt does not exist.", picName));
			return;
		}
				
		// Ensure user is profile owner or picture owner
		if (currentUser.equals(profileOwner)) {
			
			pictures.get(picName).set(0, newUser);
			System.err.format("Picture %s.txt owner changed to %s.\n", picName, newUser);
			log(String.format("Picture %s.txt owner changed to %s.", picName, newUser));
			return;
				
		}
		
		System.err.format("ERROR: Current user %s is not the profile owner nor the picture owner.\n", currentUser);
		log(String.format("ERROR: Current user %s is not the profile owner nor the picture owner.", currentUser));

	}
	
	public static void readComments(String command, String currentUser, String profileOwner, HashMap<String, ArrayList<String>> pictures, HashMap<String, ArrayList<String>> lists) throws IOException {
		/*
		 * Allows the user to view the comments/contents of the picture file if they have permissions
		 * If currentuser == pictureowner and owner has r access
		 * If currentuser != owner but is a member of the list which has r access
		 * If currentuser != owner, not a member of the list, but others have r access
		 * Otherwise deny access
		 */
		
		String picName = command.split(" ")[1].replace(".txt", "");
		
		// Ensure picture exists
		if (pictures.get(picName) == null) {
			System.err.format("Picture %s.txt does not exist.\n", picName);
			log(String.format("Picture %s.txt does not exist.", picName));
			return;
		}

		// Ensure someone is viewing the profile
		if (currentUser == null) {
			System.err.println("Must be someone viewing the profile to use readComments.\n");
			log("Must be someone viewing the profile to use readComments.");
		}
		
		String pictureOwner = pictures.get(picName).get(0);
		String pictureList = pictures.get(picName).get(1);
		String permissions = pictures.get(picName).get(2);
		String ownerP = permissions.split(" ")[0];
		String listP = permissions.split(" ")[1];
		String otherP = permissions.split(" ")[2];
		
		// If currentuser is owner and owner has access
		if (pictureOwner.equals(currentUser) && ownerP.contains("r")) {
			String contentString = fetchComments(picName + ".txt");
			System.out.format("User %s reads comments for picture %s.txt as: \n%s\n", currentUser, picName, contentString);
			log(String.format("User %s reads comments for picture %s as: \n%s\n", currentUser, picName, contentString));
		}
		
		// If currentuser isnt owner but is member of list with access
		else if (lists.get(pictureList).contains(currentUser) && listP.contains("r")) {
			String contentString = fetchComments(picName + ".txt");
			System.out.format("User %s reads comments for picture %s.txt as: \n%s\n", currentUser, picName, contentString);
			log(String.format("User %s reads comments for picture %s.txt as: \n%s\n", currentUser, picName, contentString));
		}
		
		// If others have access
		else if (otherP.contains("r")) {
			String contentString = fetchComments(picName + ".txt");
			System.out.format("User %s reads comments for picture %s.txt as: \n%s\n", currentUser, picName, contentString);
			log(String.format("User %s reads comments for picture %s.txt as: \n%s\n", currentUser, picName, contentString));
		}
		
		// No access
		else {
			System.err.format("Current user %s does not have access to read from %s.txt.\n", currentUser, picName);
			log(String.format("Current user %s does not have access to read from %s.txt.", currentUser, picName));
		}
		
	}
	
	public static void writeComments(String command, String currentUser, String profileOwner, HashMap<String, ArrayList<String>> pictures, HashMap<String, ArrayList<String>> lists) throws IOException {
		/*
		 * Allows the user to write new comments/contents to the picture file if they have permissions
		 * If currentuser == pictureowner and owner has w access
		 * If currentuser != owner but is a member of the list which has w access
		 * If currentuser != owner, not a member of the list, but others have w access
		 * Otherwise deny access
		 */
		
		String picName = command.split(" ")[1].replace(".txt", "");
		String text = command.split(" ", 3)[2];
		
		// Ensure picture exists
		if (pictures.get(picName) == null) {
			System.err.format("Picture %s.txt does not exist.\n", picName);
			log(String.format("Picture %s.txt does not exist.", picName));
			return;
		}

		// Ensure someone is viewing the profile
		if (currentUser == null) {
			System.err.println("Must be someone viewing the profile to use readComments.\n");
			log("Must be someone viewing the profile to use readComments.");
		}
		
		String pictureOwner = pictures.get(picName).get(0);
		String pictureList = pictures.get(picName).get(1);
		String permissions = pictures.get(picName).get(2);
		String ownerP = permissions.split(" ")[0];
		String listP = permissions.split(" ")[1];
		String otherP = permissions.split(" ")[2];
		
		// If currentuser is owner and owner has access
		if (pictureOwner.equals(currentUser) && ownerP.contains("w")) {
			writeComments(picName + ".txt", text);
			System.out.format("Friend %s wrote to file %s.txt:\n%s\n", currentUser, picName, text);
			log(String.format("Friend %s wrote to file %s.txt:\n%s", currentUser, picName, text));
		}
		
		// If currentuser isnt owner but is member of list with access
		else if (lists.get(pictureList).contains(currentUser) && listP.contains("w")) {
			writeComments(picName + ".txt", text);
			System.out.format("Friend %s wrote to file %s.txt:\n%s\n", currentUser, picName, text);
			log(String.format("Friend %s wrote to file %s.txt:\n%s", currentUser, picName, text));
		}
		
		// If others have access
		else if (otherP.contains("w")) {
			writeComments(picName + ".txt", text);
			System.out.format("Friend %s wrote to file %s.txt:\n%s\n", currentUser, picName, text);
			log(String.format("Friend %s wrote to file %s.txt:\n%s", currentUser, picName, text));
		}
		
		// No access
		else {
			System.err.format("Current user %s does not have access to write to %s.\n", currentUser, picName + ".txt");
			log(String.format("Current user %s does not have access to write to %s.", currentUser, picName + ".txt"));
		}
		
	}
	
	public static void end(HashMap<String, ArrayList<String>> lists, HashMap<String, ArrayList<String>> pictures) throws IOException {
		/*
		 * Writes necessary output files
		 */
		
		listWrite(lists);
		pictureWrite(pictures);
		
	}
	
	public static String fetchComments(String fileName) throws IOException {
		/*
		 * Reads from picture file to display comments
		 */
		
		File file = new File(fileName);
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		String output = "";
		
		String line;
		while((line = br.readLine()) != null) {
			output = output + line;
		}
		
		br.close();
		
		return output;
		
	}
	
	public static void writeComments(String fileName, String text) throws IOException {
		/*
		 * Appends text to picture file
		 */
		
		File file = new File(fileName);
		FileWriter	writer = new FileWriter("fish.txt", true);
		BufferedWriter	bufferedWriter = new BufferedWriter(writer);
		bufferedWriter.write(text + "\n");
		bufferedWriter.close();
	}
	
	public static void log(String command) throws IOException {
		/*
		 * Logs output prompt to audit file
		 */
	    
		FileWriter writer = new FileWriter("audit.txt", true);
	    BufferedWriter bufferedWriter = new BufferedWriter(writer);
	    try {
			bufferedWriter.write(command + "\n");
		    bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	
	public static void listWrite(HashMap<String, ArrayList<String>> map) throws IOException {
		/*
		 * Writes every list and its contents to the list file
		 */
		
	    FileWriter writer = new FileWriter("lists.txt", true);
	    BufferedWriter bufferedWriter = new BufferedWriter(writer);
	    
	    for(String key: map.keySet()) {
	    	bufferedWriter.write(key + ": ");
	    	for(String value: map.get(key)) {
	    		bufferedWriter.write(value + " ");
	    	}
	    	bufferedWriter.write("\n");
	    }
		bufferedWriter.close();
	}
	
	public static void pictureWrite(HashMap<String, ArrayList<String>> map) throws IOException {
		/*
		 * Writes every picture and its properties to the picture file
		 * Also writes every picture to its own file
		 */
		
	    FileWriter writer = new FileWriter("pictures.txt", true);
	    BufferedWriter bufferedWriter = new BufferedWriter(writer);
	    
	    // Every picture
	    for(String key: map.keySet()) {
	    	
	    	bufferedWriter.write(key + ".txt: ");
	    	
	    	// Every property
	    	for(String value: map.get(key)) {
	    		bufferedWriter.write(value + " ");
	    	}
	    	
	    	bufferedWriter.write("\n");
	    	
	    }
	    
		bufferedWriter.close();
		
	}
	
	public static void createPicFile(String picName) throws IOException {
		/*
		 * Create individual file for each picture
		 */
				
    	File picFile = new File(picName);
		if (!picFile.createNewFile()) {
			picFile.delete();
			picFile.createNewFile();
		}
			
		
		
	    FileWriter writer = new FileWriter(picName, true);
	    BufferedWriter bufferedWriter = new BufferedWriter(writer);
	    bufferedWriter.write(picName.replace(".txt", "") + "\n");
	    bufferedWriter.close();
		
	}
	
	public static void initializeFiles() throws IOException {
		/*
		 * Initializes the friends, audit, pictures, and list files
		 * Deletes them if the already exist
		 */
		
		File friendsFile = new File("friends.txt");
		if (!friendsFile.createNewFile()) {
			friendsFile.delete();
			friendsFile.createNewFile();
		}
		
		File auditFile = new File("audit.txt");
		if (!auditFile.createNewFile()) {
			auditFile.delete();
			auditFile.createNewFile();
		}
		
		File listsFile = new File("lists.txt");
		if (!listsFile.createNewFile()) {
			listsFile.delete();
			listsFile.createNewFile();
		}
	
		File pictureFile = new File("pictures.txt");
		if (!pictureFile.createNewFile()) {
			pictureFile.delete();
			pictureFile.createNewFile();
		}
		
	}

}
