import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes.Name;

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
						if (viewBy(string) != null) {
							currentUser = viewBy(string);
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
						chlst(string);
						break;
					
					case "chmod":
						chmod(string);
						break;
						
					case "chown":
						chown(string);
						break;
						
					case "readcomments":
						readComments(string);
						break;
					
					case "writecomments":
						writeComments(string);
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
		
		if (friendList.contains(name)) {
			
			System.err.format("ERROR: Friend %s already exists.\n", name);		
			log(String.format("ERROR: Friend %s already exists.", name));
			return;
			
		}

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
		
		System.out.println("A friend or you no longer view the profile.");
		log("A friend or you no longer view the profile.");
	
	}
	
	public static void listAdd(String command, HashMap<String, ArrayList<String>> map) throws IOException {
		/*
		 * Creates new list, adds list to listMap with no members
		 */
		
		String name = command.split(" ")[1];
		if(map.containsKey(name)) {
			
			System.err.format("ERROR: List %s already exists.\n", name);
			log(String.format("ERROR: List %s already exists.", name));
			return;
		
		}
		
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
		
		if (!friendList.contains(friendName)) {
			System.err.format("ERROR: Friend %s is not in friends list.\n", friendName);
			log(String.format("ERROR: Friend %s is not in friends list.", friendName));
			return;
		}
		
		if (!map.containsKey(listName)) {
			System.err.format("ERROR: List %s does not exist.\n", friendName);
			log(String.format("ERROR: List %s does not exist.", friendName));
			return;
		}
		
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
		ArrayList<String> properties = new ArrayList<String>();
		properties.add(0, owner);
		properties.add(1, "nil");
		properties.add(2, "rx -- --");
		map.put(pictureName, properties);
		System.err.format("Picture %s.txt with owner %s and default permissions has been posted.\n", pictureName, owner);
		log(String.format("Picture %s.txt with owner %s and default permissions has been posted.", pictureName, owner));
		
	}
	
	public static void chlst(String command) {
		
	}
	
	public static void chmod(String command) {
		
	}
	
	public static void chown(String command) {
		
	}
	
	public static void readComments(String command) {
		
	}
	
	public static void writeComments(String command) {
		
	}
	
	public static void end(HashMap<String, ArrayList<String>> lists, HashMap<String, ArrayList<String>> pictures) throws IOException {
		/*
		 * Writes necessary output files
		 */
		
		listWrite(lists);
		pictureWrite(pictures);
		
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
		 */
		
	    FileWriter writer = new FileWriter("pictures.txt", true);
	    BufferedWriter bufferedWriter = new BufferedWriter(writer);
	    
	    for(String key: map.keySet()) {
	    	bufferedWriter.write(key + ".txt: ");
	    	for(String value: map.get(key)) {
	    		bufferedWriter.write(value + " ");
	    	}
	    	bufferedWriter.write("\n");
	    }
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
