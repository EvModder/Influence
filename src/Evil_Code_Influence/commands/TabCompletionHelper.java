package Evil_Code_Influence.commands;

import java.util.ArrayList;
import java.util.List;	

/**
* Class to help with the TabCompletion for Bukkit.
* @author D4rKDeagle
*/
public class TabCompletionHelper {
	 
	public static List<String> getPossibleCompletionsForGivenArgs(String[] args, List<String> possibilitiesOfCompletion) {
		String argumentToFindCompletionFor = args[args.length - 1];
		List<String> listOfPossibleCompletions = new ArrayList<String>();

		for(String str : possibilitiesOfCompletion){

			if(str.regionMatches(true, 0, argumentToFindCompletionFor, 0, argumentToFindCompletionFor.length())) {
				listOfPossibleCompletions.add(str);
			}
		}
		return listOfPossibleCompletions;
	}
}
