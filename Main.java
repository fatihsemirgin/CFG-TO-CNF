import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
	public static int alph_count = 0;	// It increases as you use a letter and indicates the letter limit.
	public static boolean limit=true;	//to indicate an error message when there are no more letters available.
	public static ArrayList<String> data = new ArrayList<String>();					// all data in file including alphabet
	public static ArrayList<String> alphabet = new ArrayList<String>();				// holds alphabet
	public static ArrayList<String> variables = new ArrayList<String>();			// holds variables
	public static ArrayList<String> productions = new ArrayList<String>();			// holds all productions
	public static ArrayList<String> nullables_temp = new ArrayList<String>();		// holds nullable variables
	public static ArrayList<String> non_nullables = new ArrayList<String>();		// holds non-nullable variables
	public static String[] alphabet_new_temp = "ABCDEFGHIJKLMNPQRSTUVWXYZ".split("");	
	public static Combinations combobj = new Combinations("");			// Object for Combinations Class (For all possibilities)
	public static ArrayList<String> alphabet_new = new ArrayList<String>(Arrays.asList(alphabet_new_temp));

	public static void read_file(String txt_name) {		// Reading file
		try {
			File myObj = new File(txt_name);
			Scanner myReader = new Scanner(myObj, "UTF-8");
			while (myReader.hasNextLine()) {
				String line = myReader.nextLine();
				data.add(line);
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public static String[] nullables(ArrayList<String> data) { // For determine the nullable variables
		String[] production_temp;
		String[] variable;
		String new_eleman = "";
		String nullables = "";
		String[] alph_data = data.get(0).split("=");
		String[] alph_2 = alph_data[1].split(",");
		for (int i = 0; i < alph_2.length; i++) {
			non_nullables.add(alph_2[i]);
			alphabet.add(alph_2[i]);
		}
		for (int i = 1; i < data.size(); i++) {			// variables that include epsilon
			variable = data.get(i).split("-");
			variables.add(variable[0]);
			alph_count++;
			productions.add(variable[1]);
			production_temp = variable[1].split("\\|");
			new_eleman = "";
			for (int j = 0; j < production_temp.length; j++) {
				if (production_temp[j].equalsIgnoreCase("€")) {		// check existing epsilon
					nullables += variable[0] + " ";
					nullables_temp.add(variable[0]);

				} else
					new_eleman += production_temp[j] + " ";		
			}
			productions.set(i - 1, new_eleman.trim());		// set the production for current variable
		}
		String[] temp = nullables.split(" ");
		ArrayList<String> n_temps = new ArrayList<String>(Arrays.asList(temp));
		for (int i = 1; i < data.size(); i++) {					//	If the nullable variable is singular(Ex: A) in other variables
			variable = data.get(i).split("-");					// 	If it is then that variable also nullable
			production_temp = variable[1].split("\\|");
			for (int j = 0; j < production_temp.length; j++) {
				if (n_temps.contains(production_temp[j]) && !n_temps.contains(variable[0])) {
					nullables += variable[0] + " ";
					nullables_temp.add(variable[0]);
				}
			}
		}
		for (int i = 1; i < data.size(); i++) {				// If the nullable variable is not singular(ex: AB is nullable ,if A and B are nullable) in other variables.
			variable = data.get(i).split("-");
			production_temp = variable[1].split("\\|");
			for (int j = 0; j < production_temp.length; j++) {
				if (check_nullable_long(production_temp[j]) && !nullables_temp.contains(variable[0])) {
					nullables += variable[0] + " ";
					nullables_temp.add(variable[0]);
				}
			}
		}
		nullables = nullables.trim();
		temp = nullables.split(" ");
		check_start();			// If right sides include 'S'
		add_non_nullable();		// adding non-nullable
		return temp;
	}

	public static void add_non_nullable() {				// adding non-nullable
		for (int i = 0; i < variables.size(); i++) {
			if (!nullables_temp.contains(variables.get(i))) {
				non_nullables.add(variables.get(i));
			}
		}
	}

	public static void check_start() {		// For adding S0 if right sides include 'S'
		String[] split_data;
		boolean flag=false;
		for (int i = 0; i < productions.size(); i++) {
			split_data = productions.get(i).split(" ");
			for (int j = 0; j < split_data.length; j++) {
				if (split_data[j].contains("S") && !variables.contains("S0")) {
					variables.add(0, "S0");
					productions.add(0, "S");
					flag=true;
					break;
				}
			}
			if(flag)
				break;
		}
	}

	public static boolean check_nullable_long(String str) {		// Check for variables that only include nullable variables
		boolean flag = true;									// This function for determine nullable variables (Ex: S->AB is nullable, if A and B are nullable)
		for (int i = 0; i < str.length(); i++) {
			if (!nullables_temp.contains(String.valueOf(str.charAt(i)))) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	public static boolean check_variable(String arr) {		// Check for any production include any variable
		boolean flag = false;
		for (int i = 0; i < arr.length(); i++) {
			if (variables.contains(String.valueOf(arr.charAt(i)))) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public static ArrayList<String> eliminate_epsilons() {		// Step 1: Eliminate Epsilons
		String[] nullables = nullables(data);					// Already nullables are clear
		String[] split_data;
		String new_element = "";
		String variable ="";
		if(nullables_temp.size()>0) {
			for (int i = 0; i < productions.size(); i++) {
				split_data = productions.get(i).split(" ");
				variable = variables.get(i);
					for (int j = 0; j < split_data.length; j++) {
						if (split_data[j].length() > 1 && check_variable(split_data[j])) {
							if(new_data(split_data[j]).equalsIgnoreCase("")) {	// if new_data is "" then only adding itself for its production
								new_element += split_data[j] + " ";
							}else {
								new_element += new_data(split_data[j]) + " ";	// new_data return all production for a variable
							}
						}
						else
							new_element += split_data[j]+" ";
					}
					if (nullables_temp.contains(variables.get(i)) && variables.get(i).equalsIgnoreCase("S"))
						new_element += "€";								/// for If S is nullable , then it must be include epsilon
					productions.set(i, new_element.trim());				/// update productions
					new_element = "";			
			}
			eliminate_duplicates();		// Eliminate duplicate values if they exist in productions of a variable
		}
		return productions;
	}

	public static void combination_new_data(String temp) {		// for Combination Class
		combobj.setInputstring(temp);							// set string for combine function
		combobj.combine();										// combine function add all possibilities into arraylist
	}

	public static boolean include_non_nullable(String temp) {	// to weed out all the possibilities that come
		boolean flag = false;
		for (int i = 0; i < temp.length(); i++) {
			if (non_nullables.contains(String.valueOf(temp.charAt(i)))) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public static boolean count_non_nullables(String temp, String possibilty) {	// to weed out all the possibilities that come
		int count = 0;
		int count2 = 0;
		boolean flag = false;
		for (int i = 0; i < temp.length(); i++) {
			if (non_nullables.contains(String.valueOf(temp.charAt(i))))
				count++;
		}
		for (int i = 0; i < possibilty.length(); i++) {
			if (non_nullables.contains(String.valueOf(possibilty.charAt(i))))
				count2++;
		}
		if (count == count2)
			flag = true;
		return flag;
	}
	public static boolean check_valid(String temp) {	/// For eliminate epsilon function, check the eliminating
		boolean flag=false;
		for (int i = 0; i < temp.length(); i++) {
			if(nullables_temp.contains(String.valueOf(temp.charAt(i)))) {	// If product include any nullable variable then it can be eliminate
				flag=true;
				break;
			}
		}
		return flag;
	}
	public static String new_data(String temp) {	/// For eliminate epsilon function, return a new productions for a variable.
		String new_element = "";
		if(check_valid(temp)) {
			ArrayList<String> without_duplicate_possibilities = new ArrayList<String>();
			combination_new_data(temp);				/// It is filling the possibilities into arraylist 
		
			for (int i = 0; i < Combinations.possibilities.size(); i++) {
				if (!check_nullable_long(temp)) {									/// if temp is not entirely of nullables
					if (!without_duplicate_possibilities.contains(Combinations.possibilities.get(i))
							&& include_non_nullable(Combinations.possibilities.get(i))
							&& count_non_nullables(temp, Combinations.possibilities.get(i))) {
						new_element += Combinations.possibilities.get(i) + " ";
						without_duplicate_possibilities.add(Combinations.possibilities.get(i));
					}
				} else {		/// if temp consists entirely of nullables
					if (!without_duplicate_possibilities.contains(Combinations.possibilities.get(i))) {
						new_element += Combinations.possibilities.get(i) + " ";
						without_duplicate_possibilities.add(Combinations.possibilities.get(i));
					}
				}
			}
		}
		new_element = new_element.trim();
		combobj.clear();		// cleaning possibilities arraylist because of new variable that coming
		return new_element;
	}

	public static void eliminate_duplicates() {		// eliminate duplicate productions
		String[] split_data;
		String update_element = "";
		for (int i = 0; i < productions.size(); i++) {
			split_data = productions.get(i).split(" ");
			ArrayList<String> temp = new ArrayList<String>();
			for (int j = 0; j < split_data.length; j++) {
				if (!temp.contains(split_data[j]))
					temp.add(split_data[j]);
			}
			update_element = String.join(" ", temp);
			productions.set(i, update_element.trim());
		}
	}

	public static ArrayList<String> eliminate_units() {		// Step 2: Eliminate Unit Productions  (A->B)
		String[] split_data;
		String new_element = "";
		boolean flag = false;
		String variable ="";
		int index = -1;
		for (int i = 0; i < productions.size(); i++) {		// delete unit production if it is exist into its production
			new_element = "";								// Ex S->A|S
			split_data = productions.get(i).split(" ");
			variable = variables.get(i);
			for (int j = 0; j < split_data.length; j++) {
				if (!split_data[j].equals(variable))
					new_element += split_data[j] + " ";
			}
			productions.set(i, new_element.trim());
		}
		for (int i = 0; i < productions.size(); i++) {		// delete other Unit production and update productions
			new_element = "";
			split_data = productions.get(i).split(" ");
			for (int j = 0; j < split_data.length; j++) {
				if (variables.contains(split_data[j])) {
					index = variables.indexOf(split_data[j]);		// determine the unit production variable
					new_element += productions.get(index) + " ";	// get production of that variable
					flag = true;
				} else
					new_element += split_data[j] + " ";

				if (flag && j == split_data.length - 1) {
					productions.set(i, new_element.trim());
				}
			}
		}
		if (check_units())
			eliminate_units();				// if still there is any unit production
		eliminate_duplicates();
		return productions;
	}

	public static boolean check_units() {				//return true if any productions includes unit products		
		String[] split_data;
		boolean flag = false;
		for (int i = 0; i < productions.size(); i++) {
			split_data = productions.get(i).split(" ");
			for (int j = 0; j < split_data.length; j++) {
				if (variables.contains(split_data[j])) {
					flag = true;
					break;
				}
			}
			if (flag)
				break;
		}
		return flag;
	}

	public static String new_variable() {					// Creating new variable if any production length > 2
		String str = "";
		int random = -1;
		Random rand = new Random();
		while (true) {										// Prevent the creating same letter
			random = rand.nextInt(alphabet_new.size());
			if (!variables.contains(alphabet_new.get(random))) {
				alph_count++;
				str = alphabet_new.get(random);
				break;
			}
			if (alph_count == 25) {			// when the letters for break are gone
				limit = false;			
				break;
			}
		}
		return str;
	}

	public static ArrayList<String> eliminate_terminals() {		// Step 3: Eliminate Terminals, replacing terminals that include a production
		String new_element = "";
		for (int i = 0; i < alphabet.size(); i++) {				// adding alphabet like production 
			if(!productions.contains(alphabet.get(i))) {
				variables.add(new_variable());
				productions.add(alphabet.get(i));
			}
		}
		String[] split_data;
		for (int i = 0; i < productions.size(); i++) {
			split_data = productions.get(i).split(" ");
			new_element = "";
			for (int j = 0; j < split_data.length; j++) {
				if (split_data[j].length() > 1) {
					if (!check_terminals(split_data[j]).equalsIgnoreCase(""))
						new_element += check_terminals(split_data[j]) + " ";		// determine new (update) productions
					else
						new_element += split_data[j] + " ";
				} else
					new_element += split_data[j] + " ";
			}
			productions.set(i, new_element.trim());							// update production without terminals
		}
		return productions;
	}

	public static String check_terminals(String arr) {				// determine new (update) productions
		String new_p = "";
		int index = -1;
		for (int i = 0; i < arr.length(); i++) {
			if (arr.length() > 1 && alphabet.contains(String.valueOf(arr.charAt(i)))) {
				index = productions.indexOf(String.valueOf(arr.charAt(i)));
				if (new_p.equalsIgnoreCase(""))
					new_p = arr.replace(String.valueOf(arr.charAt(i)), variables.get(index));
				else
					new_p = new_p.replace(String.valueOf(arr.charAt(i)), variables.get(index));
			}
		}
		return new_p;
	}

	public static ArrayList<String> break_long() {				// Step 4: Break if the length is > 2
		String[] split_data;
		String new_element = "";
		for (int i = 0; i < productions.size(); i++) {
			split_data = productions.get(i).split(" ");
			new_element = "";
			for (int j = 0; j < split_data.length; j++) {
				if (split_data[j].length() > 2) {
					new_element += check_long(split_data[j]) + " ";
				} else
					new_element += split_data[j] + " ";
			}
			productions.set(i, new_element.trim());
		}
		if (general_check_break())					 
			break_long();					// if still exist a production that lenght is >2
		eliminate_duplicates();				// eliminate duplicates
		return productions;
	}

	public static String check_long(String str) {		// if productions length > 2 then it is break them
		String new_element = "";
		String new_v = "";
		if (!productions.contains(str.substring(0, 2))) {	// if that production doesn't already exist before, then its using already exist variable
			new_v = new_variable();
			variables.add(new_v);
			productions.add(str.substring(0, 2));
		} else
			new_v = variables.get(productions.indexOf(str.substring(0, 2)));	// // if that production already exist before, then its using already exist variable
		new_element = new_v + str.substring(2);
		return new_element;
	}

	public static int c = 1;

	public static void print() {							// print the output
		for (int i = 0; i < productions.size(); i++) {
			System.out.println(variables.get(i) + "-" + productions.get(i).replace(" ", "|"));
		}
	}

	public static boolean general_check_break() {			// if still exist a production that lenght is >2
		String[] split_data;
		boolean flag = false;
		for (int i = 0; i < productions.size(); i++) {
			split_data = productions.get(i).split(" ");
			for (int j = 0; j < split_data.length; j++) {
				if (split_data[j].length() > 2) {
					flag = true;
					break;
				}
			}
			if (flag)
				break;
		}
		return flag;
	}

	public static void main(String[] args) {
		try {
			read_file("CFG.txt");	// Reading file
			System.out.println("------------------- CFG TO CNF -------------------\n*** CFG FORM ***\nSTART");
			for (int i = 0; i < data.size(); i++) {
				System.out.println(data.get(i));
			}
			System.out.println("--------------------------------------------\n*** Eliminate Epsilons (€) ***");
			eliminate_epsilons();
			print();
			System.out.println("--------------------------------------------\n*** Eliminate Unit Production ***");
			eliminate_units();
			print();
			System.out.println("--------------------------------------------\n*** Eliminate Terminals ***");
			eliminate_terminals();
			print();
			System.out.println("--------------------------------------------\n*** Break Variables String Longer Than 2 ***");
			break_long();
			print();
			if(!limit)
				System.out.println(
						"!!! The letters that can be used in the break operation are finished. !!!\n!!! So the process will be left halfway here. !!!");
			System.out.println("--------------------------------------------\n*** CNF FORM ***");
			print();
		} catch (Exception e) {
			System.out.println("Please Enter Valid Data." + e);
		}

	}
}