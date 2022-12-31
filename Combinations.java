import java.util.ArrayList;

public class Combinations {
	private StringBuilder output = new StringBuilder();
	private String inputstring;

	public static ArrayList<String> possibilities = new ArrayList<String>();	// Store the all possible production

	public Combinations(String str) {
		inputstring = str;
	}

	public void combine() {
		combine(0);
	}

	private void combine(int start) {
		for (int i = start; i < inputstring.length(); ++i) {
			output.append(inputstring.charAt(i));
			possibilities.add(output.toString());		// adding possible production
			if (i < inputstring.length())
				combine(i + 1);							// recursive, if new product still include exist any possible product
			output.setLength(output.length() - 1);
		}
	}

	public void clear() {
		possibilities.clear();	// Clear arraylist , because of delete old possibles
	}

	public String getInputstring() {
		return inputstring;
	}

	public void setInputstring(String inputstring) {
		this.inputstring = inputstring;
	}
}
