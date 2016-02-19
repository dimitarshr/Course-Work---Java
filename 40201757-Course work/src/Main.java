import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Main{
	public static void main(String[] args) throws Exception {
		BufferedReader fh = new BufferedReader(new FileReader("iot.txt"));
		String s = fh.readLine(); 
		ArrayList<String> langs = new ArrayList<>(Arrays.asList(s.split("\t")));
		langs.remove(0);
		Map<String,HashMap<String,Integer>> iot = new TreeMap<>();
		while ((s=fh.readLine())!=null)
		{
			String [] wrds = s.split("\t");
			HashMap<String,Integer> interest = new HashMap<>();
			for(int i=0;i<langs.size();i++)
				interest.put(langs.get(i), Integer.parseInt(wrds[i+1]));
			iot.put(wrds[0], interest);
		}
		fh.close();

		HashMap<Integer,HashMap<String,HashMap<String,Integer>>> regionsByYear = new HashMap<>();
		for (int i=2004;i<=2015;i++)
		{
			BufferedReader fh1 = new BufferedReader(new FileReader(i+".txt"));
			String s1 = fh1.readLine();
			HashMap<String,HashMap<String,Integer>> year = new HashMap<>();
			while ((s1=fh1.readLine())!=null)
			{
				String [] wrds = s1.split("\t");
				HashMap<String,Integer>langMap = new HashMap<>();
				for(int j=1;j<wrds.length;j++){
					langMap.put(langs.get(j-1), Integer.parseInt(wrds[j]));
				}
				year.put(wrds[0],langMap);
			}
			regionsByYear.put(i,year);
			fh1.close();
		}
		//Question 6: Which regions have demonstrated interests in exactly two programming language in 2010
		//I am using an ArrayList. For every country, if there is a language with interest greater than 0, it is added to the ArrayList
		//Then I check if the size of the List is equal to 2
		System.out.print("6. Regions that have demonstrated interests in exactly two programming language in 2010: ");
		ArrayList <String> languages = new ArrayList<String>();

		for (String country: regionsByYear.get(2010).keySet()){
			for (String lang:langs){
				if (regionsByYear.get(2010).get(country).get(lang) > 0){
					languages.add(lang);}
			}
			if (languages.size() == 2)
				System.out.print("\n"+country+": "+languages.get(0)+", "+languages.get(1));
			languages.clear();
		}

		//Question 7: What are the most and least popular programming languages all over the world in 2014
		//For every language I accumulate the total interest for all countries
		//Then the if statement checks if the sum is greater than max or lower than min.
		int maxValue = Integer.MIN_VALUE;
		int minValue = Integer.MAX_VALUE;
		int langAcc = 0;
		String maxLan = null;
		String minLan = null;

		for (String lang:langs){
			for (String country: regionsByYear.get(2014).keySet()){
				langAcc += regionsByYear.get(2014).get(country).get(lang);}
			if (maxValue<langAcc){
				maxValue = langAcc;
				maxLan = lang;
			}
			else if (minValue>langAcc){
				minValue = langAcc;
				minLan = lang;
			}
			langAcc=0;
		} 		
		System.out.printf("\n7. The most popular programming language all over the world in 2014 is: %s and the least popular language is: %s.",maxLan,minLan);

		//Question 8: Which are the least popular programming languages in the United Kingdom for each of the years 2009 to 2014
		//The first for loop goes through every year and the second for loop goes through every language
		//The least popular language is found and printed
		System.out.print("\n8. Which are the least popular programming languages in the United Kingdom for each of the years 2009 to 2014: ");
		int minUKvalue = Integer.MAX_VALUE;
		String minUKlan = null;

		for (int year:regionsByYear.keySet()){
			if (year >= 2009 && year <=2014){
				for (String lang: langs){
					if (minUKvalue > regionsByYear.get(year).get("United Kingdom").get(lang)){
						minUKvalue = regionsByYear.get(year).get("United Kingdom").get(lang);
						minUKlan = lang;
					}
				}
				System.out.printf("\n   In %d the least popular language in UK is: %s.",year,minUKlan);
				minUKvalue = Integer.MAX_VALUE;
			}
		}

		//Question 11: Which are the top 5 regions that demonstrated significant growth of interests in programming languages in general
		//You may not like my solution but I did my best to provide you with the most efficient one
		//I am looking for the growth between two consecutive years. For example, the interest in 2004 and 2005, 2005 and 2006, up to 2014 and 2015
		System.out.print("\n11. The top 5 regions that demonstrated significant growth of interests in programming languages in general: ");
		int finalResult = 0;
		int sumOfInterest = 0;
		int growth = 0;
		String countryName = null;
		TreeMap <String,Integer> nameCountries = new TreeMap<String,Integer>();
		TreeSet <String> countries = new TreeSet<String>();
		ArrayList <Integer> data = new ArrayList<Integer>();
		//I am using a TreeSet to get the name of every country once only
		for(int year:regionsByYear.keySet()){
			for (String name:regionsByYear.get(year).keySet())
				countries.add(name);
		}
		//I am calculating the general interest (the interest in all languages added together) for each country year by year and add it to 'data' ArrayList
		for (String name:countries){
			for (int year:regionsByYear.keySet()){
				if(regionsByYear.get(year).containsKey(name)){
					for (String langluage:langs)
						sumOfInterest += regionsByYear.get(year).get(name).get(langluage);
					data.add(sumOfInterest);
					sumOfInterest = 0;
				}
			}
			//To calculate the growth I subtract the data from two years. 
			//For example, the data from 2005 and 2004 and so on.
			//I am using a for loop as the data is stored in the same sequence as the years
			for (int i=1;i<data.size();i++){
				growth = Math.max(growth,data.get(i)-data.get(i-1));
			}
			//I check if the growth is positive and if so I add the name of the country and its growth to a TreeMap.
			if (growth>0)
				nameCountries.put(name,growth);
			growth = 0;
			data.clear();
		}
		//In this for loop I pick the 5  greatest growths from the TreeMap
		for (int i=0;i<5;i++){
			for (String country:nameCountries.keySet()){
				if (finalResult<nameCountries.get(country)){
					finalResult = nameCountries.get(country);
					countryName = country;
				}
			}
			//As I am calculating the general interest (the interest in all languages added together), the numbers I get are greater than 100
			System.out.printf("\n    %d. In %s the growth is %d",(i+1),countryName,finalResult);
			//After I get the country with the greatest growth I remove it from the TreeMap and start looking for the next ones
			nameCountries.remove(countryName);
			finalResult = 0;
		}

		//Question 15: A continuous decline is where a language decreases week by week. Which language had the deepest decline and when?
		//I know I could have used structures like ArrayLists and TreeMaps but I tried to provide you with the most simple and efficient solution
		int currentData = 0;
		int deepestDecline = Integer.MIN_VALUE;
		int startDecline = Integer.MIN_VALUE;
		int endDecline = Integer.MIN_VALUE;
		String startWeekDecline = null;
		String endWeekDecline = null;
		String langDecline = null;
		String startWeek = null;
		String endWeek = null;
		//I am going through the period from 2004 to 2015 for every language
		for (String lang : langs){
			for (String week : iot.keySet()){
				currentData = iot.get(week).get(lang);
				//This if statement checks if the current number is larger than the smallest number in the current decline
				//'endDecline' always contains the smallest number in the current decline
				//If so, it means that the current decline has ended
				if (currentData > endDecline){
					endDecline = currentData;
					startDecline = currentData;
					startWeek = week;
				}
				//If it is not the current decline continues
				else {
					endDecline = currentData;
					endWeek = week;
				}
				//This if statement checks if the current decline is the deepest and stores the start and the end of the decline
				if (deepestDecline <= startDecline - endDecline){
					deepestDecline = startDecline - endDecline;	
					langDecline = lang;
					startWeekDecline = startWeek;
					endWeekDecline = endWeek;
				}
			}
			startDecline = Integer.MIN_VALUE;
			endDecline = Integer.MIN_VALUE;
		}
		System.out.printf("\n15. The language with the deepest decline is %s (%d): from %s to %s",langDecline,deepestDecline,startWeekDecline.substring(0,10),endWeekDecline.substring(13,23));	
	}
}
