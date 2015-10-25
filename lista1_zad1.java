import java.util.*;
import java.io.*;

public class lista1_zad1 {
	
	private static String binaryToChar(String binary) {
		int len = binary.length();
		char c = 0;
		int current = 0;
		String result = "";
		String temp = "";
		while (current < len) {
			temp = "";
			for (int i = 0; i < 8; i++) {
				temp += String.valueOf(binary.charAt(current));
				current++;
			}
			current++;
			c = (char)Integer.parseInt(temp, 2);
			result += Character.toString(c);
		}
		return result;
	
	}
	public static void main(String[] args) {
		String[] ct = new String[21];
		int ptr = 0;
		FileReader freader = null;
		String line ="";
		try{freader = new FileReader("szyfry.txt");}
		catch(FileNotFoundException ex) {
			System.out.println("No file found");
			return;
		}
		BufferedReader breader = new BufferedReader(freader);
		try {
			while((line=breader.readLine())!=null){
				ct[ptr] = binaryToChar(line);
				ptr++;
			}
		}
		catch(IOException ex) {
			System.out.println("Cannot read the file");
			return;
		}
		try{freader.close();}
		catch(IOException ex){
			System.out.println("Cannot close the file");
		}							// o tej porze ct wczytane i w dobrej postaci
		int toDec;
		try {toDec = Integer.parseInt(args[0]);}
		catch (NumberFormatException ex) {
			return;
		}
		String x = "";
		String y = "";
		boolean flaga = false;
		int[] array = new int[127];				// tablica do analizy wystapien
		int xor, xletter, yletter, keyx, keyy, decrx, decry, target;
		for (int i = 0; i < ct[20].length(); i++) {
			flaga = false;
			for (int j = 0; j < 21; j++) {
				for (int k = j + 1; k < 21; k++) {
					x = ct[j];
					y = ct[k];
					xletter = (int)x.charAt(i);
					yletter = (int)y.charAt(i);
					xor = xletter ^ yletter;
					if ((xor >= 65 && xor <= 90) || (xor >= 97 && xor <= 122)) {
						target = (int)ct[toDec].charAt(i);
						keyx = xletter ^ 32;
						keyy = yletter ^ 32;
						decrx = target ^ keyx;
						decry = target ^ keyy;
						if (decrx >= 20 && decrx < 127)
							array[decrx]++;
						if (decry >= 20 && decry < 127)
							array[decry]++;
						flaga = true;
					}
				}			
			}
			if(flaga) {
				int max = 20;
				for (int l = 21; l < 127; l++) {
					if (array[l] > array[max])
						max = l;
				}
			System.out.print((char)max);
			}
			else
				System.out.print("X"); 	// nie znaleziono spacji
			Arrays.fill(array,0);
		}
	System.out.println("");
	return;
	}
}
