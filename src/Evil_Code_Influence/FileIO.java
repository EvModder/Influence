package Evil_Code_Influence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileIO {
	public static String loadFile(String filename, String defaultContent) {
		BufferedReader reader = null;
		try{reader = new BufferedReader(new FileReader("./plugins/EvFolder/"+filename));}
		catch(FileNotFoundException e){
			//Create Directory
			File dir = new File("./plugins/EvFolder");
			if(!dir.exists())dir.mkdir();
			
			//Create the file
			File conf = new File("./plugins/EvFolder/"+filename);
			try{
				conf.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(conf));
				writer.write(defaultContent);
				writer.close();
			}
			catch(IOException e1){e1.printStackTrace();}
			return defaultContent;
		}
		StringBuilder file = new StringBuilder();
		if(reader != null){
			String line = null;
			try{
				while((line = reader.readLine()) != null){file.append(line);file.append('\n');}
				reader.close();
			}catch(IOException e){}
		}
		if(file.length() > 0) file.substring(0, file.length()-1);
		return file.toString();
	}

	public static boolean saveFile(String filename, String content) {
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter("./plugins/EvFolder/"+filename));
			writer.write(content); writer.close();
			return true;
		}
		catch(IOException e){return false;}
	}
}
