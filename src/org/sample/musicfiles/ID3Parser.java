package org.sample.musicfiles;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;

import android.util.Log;

public class ID3Parser {
	public static final String TAG = "ID3Parser";
	public static int parseBPM(String filename) {
		//Log.i(TAG, "Parsing " + filename);
		try {
			FileReader reader = new FileReader(filename);
			String version;
			char flags;
			
			char[] buffer = new char[3];
			reader.read(buffer, 0, 3);
			if (!String.valueOf(buffer).equals("ID3")) {
				Log.i(TAG, "No ID3. exiting");
				Log.i(TAG, String.valueOf(buffer));
				return 0;
			}
			
			// Version lesen
			buffer = new char[2];
			reader.read(buffer, 0,2);
			version = String.valueOf(buffer);
			
			 // Flags lesen
			buffer = new char[1];
			reader.read(buffer, 0,1);
			flags = buffer[0];
			buffer = new char[32];
			int read;
			char value;
			int i=0;
			
			char[][] needles = {
									{'T','B','P','M'},
									{'C','T','B','P'}
								};
			int[] offsets = {7,3};
			int targetIdx = 0;
			int targetNeedle = -1;
			
			StringBuffer sequence = new StringBuffer();
			
			// Jedes Zeichen einlesen, und prüfen ob es das richtige Zeichen
			// an der richtigen Stelle ist.
			do {
				// Zeichen einlesen und nach char casten
				read = reader.read();
				value = (char) read;
				
				if (targetNeedle == -1) {
					// Prüfen, welcher Tag in Frage käme
					for (int j=0;j<needles.length;j++) {
						if (value == needles[j][0]) {
							// Wir haben einen Kandidaten
							targetNeedle = j;	// beim nächsten Byte auf diese Sequenz prüfen
							targetIdx = 1;		// und am zweiten Zeichen testen
						}
					}
				} else {
					// Wir haben bereits einen Kandidaten und prüfen nun den
					if (value == needles[targetNeedle][targetIdx]) {
						targetIdx++; // Wieder ein Zeichen gefunden nächstes Zeichen
						
						// Prüfen, ob wir schon fertig sind
						if (targetIdx == needles[targetNeedle].length) {
							// komplette Sequenz gefunden
							Log.i(TAG, String.valueOf(needles[targetNeedle]) + " gefunden: ");
							
							// Überschhüssige Bytes einlesen
							buffer = new char[offsets[targetNeedle]];
							reader.read(buffer, 0, offsets[targetNeedle]);
							
							// nun die hintersten drei Stelle einlesen
							buffer = new char[3];
							reader.read(buffer, 0,3);
							
							// Die drei Stellen als Ziffern parsen
							/*int bpm = Integer.parseInt(String.valueOf(buffer));
							Log.i(TAG, "Die BPM-Zahl beträgt " + bpm);*/
							if (!Character.isDigit(buffer[0])) {
								char[] tmp = new char[2];
								tmp[0] = buffer[1];
								tmp[1] = buffer[2];
								buffer = tmp;
							}
							int bpm = Integer.parseInt(String.valueOf(buffer));
							Log.i(TAG, "BPM: " + bpm);
								
							return bpm;
						}
					} else {
						// Das nächste Zeichen hat nicht gepasst, also wieder alles zurücksetzen
						targetNeedle = -1;
						targetIdx = 0;
					}
				}
				
				//Log.i(TAG, Character.toString((char) value));
			} while ((i++)<1024 && read>-1);
	
			reader.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
}