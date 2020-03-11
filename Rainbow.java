//Done by: Aaron Lim
//Student ID: 5985171
//Assignment: 1

import java.math.BigInteger; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.nio.file.Paths;
import static java.util.stream.Collectors.*;
import static java.util.Map.Entry.*;

//source code for Md5: https://www.geeksforgeeks.org/md5-hash-in-java/

public class Rainbow{
	
	static Scanner input;
	static Formatter output;
	static pw[] passwordTable;
	static HashMap<String, String> rainbowTable;
	static HashMap<String, String> hashMap;
	static String userHash;
	static int length;

	public static void main(String args[]) throws 
		NoSuchAlgorithmException, FileNotFoundException  {
		hashMap = new HashMap<String, String>();
		Scanner sc = new Scanner(System.in);		
		String fileName = args[0];
		String s = "";	
		boolean error = false;
		
		//check file exist
        boolean ok = checkTextFile(fileName);
		
		if(ok){
			try{
				while(input.hasNext()){
					s = input.nextLine();
					hashMap.put(s, "BLANK");
					length++;
				}
			}
			catch(Exception e){
				System.out.println("Error in reading " + fileName);
				error = true;
			}
			if(input != null){
				input.close();
			}
		
		
                        if(!error){
                                System.out.println("Number of words read in: " + length);

                                passwordTable = new pw[length];
                                int k = 0;
                                for(Map.Entry<String, String> entry : hashMap.entrySet()){
                                        passwordTable[k] = new pw(entry.getKey(), entry.getValue());
                                        k++;
                                }

                                int rounds = length / 5;
                                int balance = length % 5;

                                if(0 < balance){
                                        //additional rounds if balance is more than 0
                                        rounds++;
                                }

                                rainbowTable = new HashMap<String, String> ();
                                int usedPassword = 0;
                                int e = 1;
                                for(int w = 0; w < length; w++){
                                        int w2 = 0;
                                        if(!passwordTable[w].getUsed() && usedPassword < length - balance){
                                                passwordTable[w].setUsed(true);
                                                usedPassword++;
                                                w2 = w;
                                                for(int j = 0; j < 4; j++){
                                                        s = getMd5(passwordTable[w2].getPassword());
                                                        passwordTable[w2].setHash(s);
                                                        w2 = reduction(s);
                                                        passwordTable[w2].setUsed(true);
                                                        usedPassword++;
                                                }
                                                s = getMd5(passwordTable[w2].getPassword());
                                                passwordTable[w2].setHash(s);
                                                rainbowTable.put(passwordTable[w].getPassword(), s);
                                                e++;
                                        }
                                        else if(!passwordTable[w].getUsed() && usedPassword >= length - balance){
                                                passwordTable[w].setUsed(true);
                                                usedPassword++;
                                                w2 = w;
                                                for(int i = 0; i < balance - 1; i++){
                                                        s = getMd5(passwordTable[w2].getPassword());
                                                        passwordTable[w2].setHash(s);
                                                        w2 = reduction(s);
                                                        passwordTable[w2].setUsed(true);
                                                        usedPassword++;
                                                }

                                                s = getMd5(passwordTable[w2].getPassword());
                                                passwordTable[w2].setHash(s);
                                                rainbowTable.put(passwordTable[w].getPassword(), s);
                                                e++;
                                        }
                                }

                                if(rounds == rainbowTable.size()){
                                        System.out.println("Hashing of " + fileName + " is completed");
                                }
                                else{
                                        System.out.println("Hashing of " + fileName + " is incompleted");
                                }
                                System.out.println("Total number of passwords and hashes store in rainbow table: " + rainbowTable.size());

                                rainbowTable = sortByValue(rainbowTable);
                                createTextFile("Rainbow.txt");

                                for(int i = 0; i < length; i++){
                                        passwordTable[i].setUsed(false);
                                }
                                String userInput;
                                ok = true;
                                int red = 0;
                                boolean found = false;
                                while(ok){
                                        userInput = checkUserInput();
                                        userInput = userInput.toLowerCase();
                                        if(userInput.equals("q")){
                                                break;
                                        }
                                        found = false;
                                        if(checkHashRT(userInput)){
                                                userHash = userInput;
                                                found = true;
                                        }
                                        else{
                                                userHash = userInput;
                                                int i = 0;
                                                while(i < length){
                                                        red = reduction(userHash);
                                                        passwordTable[red].setUsed(true);
                                                        userHash = getMd5(passwordTable[red].getPassword());
                                                        if(checkHashRT(userHash)){
                                                                found = true;
                                                                                                                        break;
                                                        }
                                                        i++;
                                                                                        }
                                        }
                                        for(int i = 0; i < length; i++){
                                                passwordTable[i].setUsed(false);
                                        }
                                        if(found){
                                                                                        found = false;
                                                for(Map.Entry<String, String> entry : rainbowTable.entrySet()){
                                                        if(userHash.equals(entry.getValue())){
                                                                userHash = getMd5(entry.getKey());
                                                                if(userHash.equals(userInput)){
                                                                        System.out.println("Password(Pre-image): " + entry.getKey());
                                                                                                                                        found = true;
                                                                }
                                                                else{
                                                                        int i = 0;
                                                                        while(i < length){
                                                                                int image = reduction(userHash);
                                                                                passwordTable[image].setUsed(true);
                                                                                userHash = getMd5(passwordTable[image].getPassword());
                                                                                if(userHash.equals(userInput)){
                                                                                                                                                                        found = true;
                                                                                        System.out.println("Password(Pre-image): " + passwordTable[image].getPassword());
                                                                                        break;
                                                                                }
                                                                                i++;
                                                                        }
                                                                }
                                                        }
                                                }
                                        }
                                        if(!found){
                                                System.out.println("Password(Pre-image): not found");
                                        }
                                        for(int i = 0; i < length; i++){
                                                passwordTable[i].setUsed(false);
                                        }
                                        while(true){
                                                System.out.print("Do you want to try another hash value(Y/N): ");
                                                s = sc.nextLine();
                                                if(s.equals("Y") || s.equals("y")){
                                                        ok = true;
                                                        break;
                                                }
                                                else if(s.equals("n") || s.equals("N")){
                                                        ok = false;
                                                        break;
                                                }
                                                else{
                                                        System.out.println("You have entered a wrong value, please enter Y/N");
                                                }
                                        }
                                }
                        }
                }
		System.out.println("Program terminating, thank you for using this program");
	}
        
        public static boolean checkTextFile (String filename)
	{
		// Open the file for reading
		try
		{	
			input = new Scanner (Paths.get (filename));
		}
		catch (FileNotFoundException e)
		{
			System.err.println ("Error in open the file");
			return false;
		}
		catch (IOException e)
		{
			System.err.println ("Error in IO");
			return false;
		}

		return true;
	} 
        
	public static String getMd5(String input) 
		{ 
		try { 

			// Static getInstance method is called with hashing MD5 
			MessageDigest md = MessageDigest.getInstance("MD5"); 

			// digest() method is called to calculate message digest 
			//  of an input digest() return array of byte 
			byte[] messageDigest = md.digest(input.getBytes()); 

			// Convert byte array into signum representation 
			BigInteger no = new BigInteger(1, messageDigest); 

			// Convert message digest into hex value 
			String hashtext = no.toString(16); 
			while (hashtext.length() < 32) { 
				hashtext = "0" + hashtext; 
			} 
			return hashtext; 
		}  

		// For specifying wrong message digest algorithms 
		catch (NoSuchAlgorithmException e) { 
			throw new RuntimeException(e); 
		} 
	} 
	
	public static int reduction(String hash){
		String hexa = "0123456789ABCDEF"; // hexa values
        hash = hash.toUpperCase(); // convert hash values to uppercase  
        int hexValue = 0;  
	 	int d = 0;
		char c = 'a';
		int index = -1;
		BigInteger bigIndex;
		BigInteger counter = BigInteger.valueOf(length);
		
		//convert hexa hash value to decimal
		for (int i = 0; i < hash.length(); i++)  {  
			c = hash.charAt (i);
		        d = hexa.indexOf (c);
		        hexValue = 16*hexValue + d;
		}
		
		//changing all negative value to positive value
		hexValue = Math.abs(hexValue);
		
		//convert long to big integer to use the mod library function
		BigInteger bigHexa = BigInteger.valueOf(hexValue);

		//retrieving an index for a diff password
		//if hex value >= modValue then mod (smaller value)
		if(bigHexa.compareTo(counter) == 1 || bigHexa.compareTo(counter) == 0){
			bigIndex = bigHexa.mod(counter);
		}
		else{
			bigIndex = bigHexa;
		}

		index = bigIndex.intValue();

		if (!passwordTable[index].getUsed()){
			return index; 
		}
		
		boolean found = false;
		
		while(!found){
			hash = getMd5(hash);
			hash = hash.toUpperCase();
			for (int i = 0; i < hash.length(); i++)  {  
				c = hash.charAt (i);  
				d = hexa.indexOf (c); 
				hexValue = 16*hexValue + d;
			}

			bigHexa = BigInteger.valueOf (hexValue);
			bigIndex = bigHexa.mod (counter);
			index = bigIndex.intValue ();

			if(!passwordTable[index].getUsed()){
				found = true;
				return index; 
			}	
		}
		return index;
	}

	public static HashMap<String, String> sortByValue (HashMap<String, String> map) { 
		HashMap<String, String> sorted = map.entrySet().stream().sorted(comparingByValue()).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));

		return sorted;
	}

	public static void createTextFile (String filename){
		try{
			output = new Formatter (filename);
		}
		catch (FileNotFoundException e){
			System.err.println ("File could not open for creation");
		}
		catch (SecurityException e){
			System.err.println ("Write permission denied");
		}
		
		//writing the data
		int i = 1;
		for (Map.Entry <String, String> entry : rainbowTable.entrySet ()){
			output.format ("%d%c%s%c%s%n", i , ' ', entry.getKey (), 
				       ' ', entry.getValue ());
			i++;		
		}		
		
		//Closing of file
		if (output != null){
			output.close ();
			System.out.println (filename + " successfully created\n");
		}
	} 

	public static String checkUserInput ()
	{
		boolean ok = false;
		String hash = "";
		Scanner in = new Scanner(System.in);

		do{
			try{
				System.out.print("Please enter a hash value of length 32(Q to quit): ");
				hash = in.nextLine();
				if (hash.length() == 32){
					ok = checkHexa(hash);
				}
				else if(hash.equals("Q") || hash.equals("q")){
					ok = true;
				}
				else{
					System.out.println("Please re-enter");
				}
			}
			catch (Exception e){
				System.out.println ("Exception message: " + e);
			}
		} while (ok == false);

		return hash;
	}

	public static boolean checkHexa (String input){
		// check input is a hexadecimal input
		String hexa = "0123456789ABCEDFabcdef";

		for (int i = 0; i < input.length(); ){
			if (hexa.contains (Character.toString (input.charAt (i)))){
				i++;
			}
			else{
				return false;
			}
			
		}
		return true;
	}
	
	public static boolean checkHashRT (String userHash){
		for (Map.Entry <String, String> entry : rainbowTable.entrySet ()){
			if (userHash.equals(entry.getValue())){
				return true;
			}		
		}
		return false;
	}
}

class pw{
	private String password;
	private String hash;
	boolean used;
	private static int ID = 0;

	//default constructor
	public pw(String password, String hash){
		//Check the password is not empty		
		if(password.equals("")){
			throw new IllegalArgumentException("Password is empty, not stored");
		}
		//check if the hash is empty
		if(hash.equals("")){
			throw new IllegalArgumentException("Hash value is empty, not stored");
		}
		this.password = password;
		this.hash = hash;
		this.used = false;
		ID++;
	}

	//get functions
	public String getPassword(){
		return password;
	}

	public String getHash(){
		return hash;
	}
	
	public boolean getUsed(){
		return used;
	}
	
	public static int getID(){
		return ID;
	}

	//set functions
	public void setPassword(String password){
		this.password = password;
	}

	public void setHash(String hash){
		this.hash = hash;
	}

	
	public void setUsed(boolean used){
		this.used = used;
	}

	@Override
	public String toString(){
		return String.format ("%-20s%s%s%s%s%b", password, "\t", hash, "\t", used);
	}
}