import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class access {

	public static void main(String[] args) {
		
		File friendsFile, listsFile, pictureFile, auditFile;
		List<String> lists, pictures;
		
		if (args.length != 1) {
			System.out.println("Program must be executed with input file name: ./access filename.txt");
		}
		
		List<String> inputFileContents = readFile(args[0]);
		
		// Initialize files
		friendsFile =  new File("friends.txt");
		listsFile=  new File("lists.txt");
		pictureFile=  new File("pictures.txt");
		auditFile=  new File("audit.txt");
		
		// Initialize lists
		lists = new ArrayList<String>();
		pictures = new ArrayList<String>();
		
		for (String string : inputFileContents) {
			
			String[] split = string.split(" ");
			
			if(!split[0].equalsIgnoreCase("friendadd")) {
			    System.err.format("First command must be of the form: friendadd friendname, but was: %s", string);
			    return;
			}
			
			switch (split[0]) {
			
			case "friendadd":
				friendAdd(string);
				break;
			
			case "viewby":
				viewBy(string);
				break;
				
			case "logout":
				logout();
				break;
			
			case "listadd":
				listAdd(string);
				break;
			
			case "friendlist":
				friendList(string);
				break;
				
			case "postpicture":
				postPicture(string);
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
				end();
				break;	

			default:
				break;
			}
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
	
	public static void friendAdd(String command) {
		
	}
	
	public static void viewBy(String command) {
		
	}
	
	public static void logout() {
		
	}
	
	public static void listAdd(String command) {
		
	}
	
	public static void friendList(String command) {
		
	}
	
	public static void postPicture(String command) {
		
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
	
	public static void end() {
		
	}
	
}
