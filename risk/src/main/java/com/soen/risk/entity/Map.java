package com.soen.risk.entity;

import com.soen.risk.interactor.GamePlay;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.util.ResourceUtils;

/**
 * <h2>Map Class</h2>
 * <p> Map class contains loading of map using load method, save map, add new Country and continents.
 * Validation of map.</p>
 *
 * @author : Amit Sachdeva
 * @author : Manmeet Singh
 * @author : Nivetha
 * @version 1.0.2
 */
public class Map {
    private static Logger logger = Logger.getLogger(Map.class.getName());
    private String name;
    private ArrayList<Continent> continents;
    private ArrayList<Country> countries;
    private LinkedList<LinkedList<Country>> adjCountry;
    private LinkedList<Object> list_country;
    //private String fileName;

    /**
     * Initiating continents, countries, 2D linkedlist of country
     */
    public Map() {
        this.continents = new ArrayList<>();
        this.countries = new ArrayList<>();
        this.adjCountry = new LinkedList<>();
        this.list_country = new LinkedList<>();
    }

    /**
     * Add new continent to continent object     *
     *
     * @param continent new continent
     */
    public void addContinent(Continent continent) {
        logger.log(Level.INFO, "Adding continent " + continent.getName());
        continents.add(continent);
    }

    /**
     * Add country to country object
     *
     * @param country
     */
    public void addCountry(Country country) {
        logger.log(Level.INFO, "Adding country " + country.getName());
        countries.add(country);
    }

    public Country findByCountryName(String s) {
        for (Country c : countries) if (c.getName().equals(s)) return c;
        return null;
    }

    public Continent findByContinentName(String s) {
        for (Continent c : continents) if (c.getName().equals(s)) return c;
        return null;
    }


    // -------------------------------------------------------------

    /**
     * This method receive map from file and add to adjCountry object.
     *
  //   * @param filename Path of File with name of file
     * @author Amit Sachdeva
     * @since 2018-10-06
     */
    public void load(String fileName) {
    	File file;
		try {
			file = ResourceUtils.getFile("classpath:map/"+fileName);
			fileName=file.getAbsolutePath();
		} 
		catch (FileNotFoundException fileNotFound) 
		{
			logger.log(Level.SEVERE, fileNotFound.getMessage());
		}

    	
        logger.log(Level.INFO, "Reading filename " + fileName);
        try {
            Scanner readingFile = new Scanner(new FileReader(fileName));
            int flagContinent = 0, flagCountry = 0;
            //TODO: add map name using content in the file.
            while (readingFile.hasNext()) {
                String line = readingFile.nextLine();

                if (!line.equals("") && flagContinent == 1) this.addNewContinent(line);
                else if (line.equals("") && flagContinent == 1) flagContinent = 0;

                if (flagCountry == 1) this.addNewCountry(line);

                if (line.equals("[Continents]")) flagContinent = 1;
                else if (line.equals("[Territories]")) flagCountry = 1;
            }

            readingFile.close();
            this.map_country_object_creation();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * This method save finally updated map in a file.
     *
     * @author Manmeet Singh
     * @since 2018-10-07
     */
   /* public void save(String filePath) {
        try {
            logger.log(Level.INFO, "Saving map to " + filePath);
            PrintWriter pw = new PrintWriter(new File(filePath));
            
            pw.println("Map=" + this.name);
            pw.println();
            pw.println("[Continents]");
            for (Continent continent : continents) {
                pw.println(continent.getName() + "=" + continent.getControlValue());
            }
            pw.println();
            pw.println("[Territories]");
            for (Country country : countries) {
                String tem = country.getName() + "," + country.getCoordinateX() + "," + country.getCoordinateY() + "," +
                        this.getContinent(country) + "," + this.getNeighbouringCountries(country.getName());
                logger.log(Level.INFO, this.getNeighbouringCountries(country.getName()));
                logger.log(Level.INFO, tem);
                pw.println(tem);
            }
            pw.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

      }*/
    
    public void save(String fileName)
    {
    	Path newFilePath = Paths.get("src/main/resources/map/"+fileName);
        try {
			Files.createFile(newFilePath);
			FileWriter fw = new FileWriter("src/main/resources/map/"+fileName, true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    PrintWriter pw = new PrintWriter(bw);
			
				pw.println("Map=" + this.name);
	            pw.println();
	            pw.println("[Continents]");
	            for (Continent continent : continents) {
	                pw.println(continent.getName() + "=" + continent.getControlValue());
	            }
	            pw.println();
	            pw.println("[Territories]");
	            for (Country country : countries) {
	                String tem = country.getName() + "," + country.getCoordinateX() + "," + country.getCoordinateY() + "," +
	                        this.getContinent(country) + "," + this.getNeighbouringCountries(country.getName());
	                logger.log(Level.INFO, this.getNeighbouringCountries(country.getName()));
	                logger.log(Level.INFO, tem);
	                pw.println(tem);
	            }
			pw.close();

			
			
		} 
        catch (IOException ioException) {
			
        	logger.log(Level.SEVERE, ioException.getMessage());
		}
    }

    /**
     * Validates the continents and country objects for duplicate values.
     * Traversed adjacent countries and verified all countries are linked.
     *
     * @return true is no duplicates found and if all countries are connected.
     * false for duplicate occurrence and if any country is isolated
     * @author Nivetha
     * @since 2018-10-06
     */

    public boolean isValid() {
        if (checkContinentDuplicacy()) return false;
        else if (checkCountryDuplicacy()) return false;
        else if (checkIsolatedCountry()) return false;
        return true;
    }

    public List<String> getCountryNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Country country : countries)
            names.add(country.getName());
        return names;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * @param startCountry Starting country name
     * @param endCountry   Checking Ending country name
     * @param countries    List of countries to check for path
     * @return boolean value to denote if path exists
     */
    public boolean pathExists(Country startCountry, Country endCountry, List<Country> countries) {
        int startId = startCountry.getId();
        int endId = endCountry.getId();

        LinkedList<Integer> adj[] = new LinkedList[adjCountry.size()];
        for (int i = 0; i < adjCountry.size(); i++) {
            adj[i] = new LinkedList();
        }
        for (LinkedList<Country> ll1 : adjCountry) {
            int index = ll1.get(0).getId();
            int j = 0;
            for (Country c : ll1) {
                if (j != 0) {
                    adj[index].add(c.getId());
                }
                j++;

            }
        }
        return this.searchPathBetweenCountries(adj, startId, endId, countries);


    }

    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------

    /**
     * todo: rename the function and optimise the loop
     * Return HashMap with countries and its neighboring countries not belong to player
     *
     * @return HashMap of Neighboring countries of a Player Specific Country Name
     */
    public HashMap<String, ArrayList<String>> getAdjCountries(List<Country> countries) {
        HashMap<String, ArrayList<String>> neighbouring = new HashMap<>();
        for (Country c : countries) {
            for (LinkedList<Country> lc : adjCountry) {
                if (c.getName().equals(lc.get(0).getName())) {
                    neighbouring.put(c.getName(), checkPlayerSpecificNeigbhouringCountries(lc, countries));
                }
            }
        }
        return neighbouring;
    }

    private ArrayList<String> checkPlayerSpecificNeigbhouringCountries(LinkedList<Country> lc,
                                                                       List<Country> countries) {
        ArrayList<String> ar = new ArrayList<>();
        for (Country c1 : lc) {
            int flag = 0;
            for (Country c : countries) {
                if (c1.getName().equals(c.getName())) {
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                ar.add(c1.getName());
            }
        }
        return ar;
    }


    private void addNewContinent(String temp) {
        logger.log(Level.FINE, "Adding continent " + temp);
        String split_continent[] = temp.split("=");
        Continent cont = new Continent(split_continent[0]);
        cont.setControlValue(Integer.valueOf(split_continent[1]));
        continents.add(cont);
    }


    private void addNewCountry(String temp) {
        logger.log(Level.FINE, "Adding new country " + temp);
        String split_country[] = temp.split(",");
        Country coun = new Country(countries.size(), split_country[0]);
        coun.setCoordinateX(split_country[1]);
        coun.setCoordinateY(split_country[2]);
        Iterator<Continent> il = continents.iterator();

        String temp_continent_name = split_country[3];
        while (il.hasNext()) {

            Continent temp_cont = il.next();
            if (temp_cont.getName().equals(temp_continent_name)) {
                temp_cont.addCountry(coun);
                break;
            }
        }
        this.countries.add(coun);
        String arr[] = new String[split_country.length - 3];
        arr[0] = split_country[0];
        for (int i = 4; i < split_country.length; i++) {
            arr[i - 3] = split_country[i];
        }
        this.map_name_creation(arr);
    }

    //Creating map on basis of Country Name
    public void map_name_creation(String s[]) {
        LinkedList<String> temp_list = new LinkedList<>();

        int i = 0;
        while (i < s.length) {
            temp_list.add(s[i]);
            i++;
        }
        this.list_country.add(temp_list);
    }

    /**
     * This method to create graph of whole map.
     *
     * @author Amit Sachdeva
     * @since 2018-10-07
     */
    public void map_country_object_creation() {
        Iterator il = list_country.iterator();
        while (il.hasNext()) {
            LinkedList l1 = (LinkedList) il.next();
            LinkedList temp = new LinkedList<Country>();
            Iterator il2 = l1.iterator();
            while (il2.hasNext()) {
                String s = (String) il2.next();
                temp.add(findByCountryName(s));
            }
            adjCountry.add(temp);
        }

    }


    /**
     * This method get continent for specific country.
     *
     * @param c Pass country object
     * @return Return Continent name.
     * @author Manmeet Singh
     * @since 2018-10-07
     */
    private String getContinent(Country c) {
        for (Continent con : continents) {
            for (Country coun : con.getCountries()) {
                if (coun.getName().equals(c.getName())) {
                    return con.getName();
                }
            }
        }
        return "";
    }


    private String getNeighbouringCountries(String country) {
        for (LinkedList<Country> ll : this.adjCountry) {
            if (ll.get(0).getName().equals(country)) {
                int i = 1;
                String temp = "";
                while (i < ll.size()) {
                    temp = temp + ll.get(i).getName();
                    if (i != ll.size() - 1) {
                        temp = temp + ",";
                    }
                    i++;
                }
                return temp;
            }
        }
        return "";
    }


    // -----------------------------------------------------------------------------------------------------------------


    /**
     * @param allowedCountries List of countries to be assigned as adjacent
     * @param movingPath       Path of a country
     * @return to denote whether path is valid or not
     */
    private boolean checkPathValid(List<Country> allowedCountries, List<Integer> movingPath) {
        for (int countryId : movingPath) {
            int flag = 0;
            for (Country c : allowedCountries) {
                if (c.getId() == countryId) {
                    flag = 1;
                    break;
                }
            }
            if (flag != 1) {
                logger.log(Level.INFO, "Invalid path.");
                return false;
            }
        }
        logger.log(Level.INFO, "Valid path.");
        return true;
    }

    /**
     * @param path
     * @param allPaths
     */
    private void addPath(LinkedList<Integer> path, ArrayList<ArrayList<Integer>> allPaths) {
        ArrayList<Integer> temp = new ArrayList<>();
        for (int a : path) {
            temp.add(a);
        }
        allPaths.add(temp);
    }


    private int countNumberOfPath(LinkedList adj[], int start, int dest, int PathCount, boolean visited[], LinkedList<Integer> path, ArrayList<ArrayList<Integer>> allPaths) {
        visited[start] = true;
        path.add(start);
        if (start == dest) {
            PathCount++;
            this.addPath(path, allPaths);
        } else {
            Iterator<Integer> il = adj[start].listIterator();
            while (il.hasNext()) {
                int n = il.next();
                if (!visited[n]) {
                    PathCount = countNumberOfPath(adj, n, dest, PathCount, visited, path, allPaths);
                }
            }
        }
        int i = 0;
        int flag = 0;
        for (int a : path) {
            if (a == start) {
                flag = 1;
                break;
            }
            i++;
        }
        if (flag == 1) {
            path.remove(i);
        }
        visited[start] = false;
        return PathCount;
    }


    private boolean searchPathBetweenCountries(LinkedList adj[], int currentCountry, int shift, List<Country> countries) {
        boolean v[] = new boolean[adj.length];
        LinkedList<Integer> movingPath = new LinkedList();
        ArrayList<Country> coun = GamePlay.getInstance().getGame().getMap().getCountries();
        ArrayList<ArrayList<Integer>> allPaths = new ArrayList<ArrayList<Integer>>();

        //Start searching for path between both countries
        int PathCount = 0;
        PathCount = this.countNumberOfPath(adj, currentCountry, shift, PathCount, v, movingPath, allPaths);
        logger.log(Level.INFO, "Path count : " + PathCount);

        if (PathCount != 0) {
            //Finally Testing whether player able to move army
            logger.log(Level.INFO, "All Possible Path Followed");
            for (ArrayList<Integer> path : allPaths) {
                logger.log(Level.INFO, "Path Followed");
                for (int countryId : path) {
                    logger.log(Level.INFO, "Country index - " + countryId);
                }
            }
            for (ArrayList<Integer> path : allPaths) {
                if (this.checkPathValid(countries, path)) {
                    return true;
                }
            }
        }
        return false;
    }


    // -----------------------------------------------------------------------------------------------------------------

    public boolean checkContinentDuplicacy() {
        if (this.continents.size() == 0) {
            logger.log(Level.INFO, "Continents not found.");
            return true;
        }
        for (int i = 0; i < this.continents.size() - 1; i++) {
            for (int j = i + 1; j < this.continents.size(); j++) {
                if (this.continents.get(i).getName().equals(this.continents.get(j).getName())) {
                    logger.log(Level.INFO, "Found duplicate continent " + this.continents.get(i).getName());
                    return true;
                }
            }
        }
        logger.log(Level.INFO, "Check continent duplicacy passed");
        return false;
    }


    public boolean checkCountryDuplicacy() {

        if (this.countries.size() == 0) {
            logger.log(Level.INFO, "Countries not found.");
            return true;
        }
        for (int i = 0; i < this.countries.size() - 1; i++) {
            for (int j = i + 1; j < this.countries.size(); j++) {
                if (this.countries.get(i).getName().equals(this.countries.get(j).getName())) {
                    logger.log(Level.INFO, "duplicate country found " + this.countries.get(i).getName());
                    return true;
                }
            }
        }
        logger.log(Level.INFO, "Check country duplicacy passed");
        return false;
    }


    private void checkingContacting(LinkedList adj[], int CurrentCountry, boolean visited[], LinkedList<Integer> movingPath) {
        visited[CurrentCountry] = true;
        movingPath.add(CurrentCountry);
        Iterator<Integer> i = adj[CurrentCountry].listIterator();
        while (i.hasNext()) {
            int n = i.next();
            if (!visited[n])
                checkingContacting(adj, n, visited, movingPath);
        }
    }

    public boolean checkIsolatedCountry() {
        int currentCoun = this.countries.get(0).getId();    //Convert Country name to their ids
        LinkedList<LinkedList<Country>> ll = this.adjCountry;

        LinkedList<Integer> adj[] = new LinkedList[ll.size()];
        for (int i = 0; i < ll.size(); i++) {
            adj[i] = new LinkedList();
        }

        for (LinkedList<Country> ll1 : ll) {
            int index = ll1.get(0).getId();
            int j = 0;
            for (Country c : ll1) {
                if (j != 0) {
                    adj[index].add(c.getId());
                }
                j++;

            }
        }

        boolean v[] = new boolean[adj.length];
        LinkedList<Integer> movingPath = new LinkedList();
        //Start searching for path between both countries
        this.checkingContacting(adj, currentCoun, v, movingPath);
        if (movingPath.size() == this.countries.size()) {
            logger.log(Level.INFO, "Isolation test passed.");
            return false;
        } else {
            logger.log(Level.INFO, "Isolation test failed.");
            return true;
        }
    }

    public List<Country> getNeighbouringCountry(Country c) {
        for (LinkedList<Country> ll : adjCountry) {
            Country c1 = ll.get(0);
            if (c1.getName().equals(c.getName())) return ll;
        }
        return null;
    }

    //------------------------------------------------------------------------------------------------------------------
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Country> getCountries() {
        return countries;
    }

    public void setCountries(ArrayList<Country> countries) {
        this.countries = countries;
    }

    public LinkedList<LinkedList<Country>> getAdjCountry() {
        return adjCountry;
    }

    public LinkedList<Object> getListCountry() {
        return list_country;
    }

    public void setAdjCountry(LinkedList<LinkedList<Country>> adjCountry) {
        this.adjCountry = adjCountry;
    }

    public ArrayList<Continent> getContinents() {
        return continents;
    }

    //Get map on basis of country object
    public LinkedList<LinkedList<Country>> getMapCountryObject() {
        return adjCountry;
    }

    //Get number of countries
    public int getNumberOfCountries() {
        return countries.size();
    }

    //Get number of continents
    public int getNumberOfContinents() {
        return continents.size();
    }
    // -------------------------------------------------------------

}
