package com.soen.risk.entity;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class Map implements Serializable {
    
    /** The logger. */
    private static Logger logger = Logger.getLogger(Map.class.getName());
    
    /** The name. */
    private String name;
    
    /** The continents. */
    private ArrayList<Continent> continents;
    
    /** The countries. */
    private ArrayList<Country> countries;
    
    /** The adj country. */
    private LinkedList<LinkedList<Country>> adjCountry;
    
    /** The list country. */
    private LinkedList<Object> list_country;
    //private String fileName;

    /**
     * Initiating continents, countries, 2D linkedlist of country.
     */
    public Map() {
        this.continents = new ArrayList<>();
        this.countries = new ArrayList<>();
        this.adjCountry = new LinkedList<>();
        this.list_country = new LinkedList<>();
    }

    /**
     * Add new continent to continent object     *.
     *
     * @param continent new continent
     */
    public void addContinent(Continent continent) {
        logger.log(Level.INFO, "Adding continent " + continent.getName());
        continents.add(continent);
    }

    /**
     * Add country to country object.
     *
     * @param country the country
     */
    public void addCountry(Country country) {
        logger.log(Level.INFO, "Adding country " + country.getName());
        countries.add(country);
    }

    /**
     * Find by country name.
     *
     * @param s the s
     * @return the country
     */
    public Country findByCountryName(String s) {
        for (Country c : countries) if (c.getName().equals(s)) return c;
        return null;
    }

    /**
     * Find by continent name.
     *
     * @param s the s
     * @return the continent
     */
    public Continent findByContinentName(String s) {
        for (Continent c : continents) if (c.getName().equals(s)) return c;
        return null;
    }


    // -------------------------------------------------------------

    /**
     * This method receive map from file and add to adjCountry object.
     * <p>
     *
     * @author Amit Sachdeva
     * @param fileName Path of File with name of file
     * @since 2018-10-06
     */
    public void load(String fileName) {
        logger.log(Level.INFO, "Reading filename " + fileName);
        try {
            Scanner readingFile = new Scanner(new FileReader(fileName));
            int flagContinent = 0, flagCountry = 0;
            //TODO: add map name using content in the file.
            while (readingFile.hasNext()) {
                String line = readingFile.nextLine();

                if (!line.equals("") && flagContinent == 1) this.addNewContinent(line);
                else if (line.equals("") && flagContinent == 1) flagContinent = 0;

                if (!line.equals("") && flagCountry == 1) this.addNewCountry(line);

                if (line.equals("[Continents]")) flagContinent = 1;
                else if (line.equals("[Territories]")) flagCountry = 1;
            }

            readingFile.close();
            this.map_country_object_creation();
            this.finalMap();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * This method save finally updated map in a file.
     *
     * @author Manmeet Singh
     * @param filePath the file path
     * @since 2018-10-07
     */
    public void save(String filePath) {
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

    }


    /**
     * Validates the continents and country objects for duplicate values.
     * Traversed adjacent countries and verified all countries are linked.
     *
     * @author Nivetha
     * @return true is no duplicates found and if all countries are connected.
     * false for duplicate occurrence and if any country is isolated
     * @since 2018-10-06
     */

    public boolean isValid() {
        if (checkContinentDuplicacy()) return false;
        else if (checkCountryDuplicacy()) return false;
        else if (checkIsolatedCountry()) return false;
        else if (!checkContinentCountriesConnected()) return false;
        return true;
    }

    /**
     * Gets the country names.
     *
     * @return the country names
     */
    public List<String> getCountryNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Country country : countries)
            names.add(country.getName());
        return names;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Path exists.
     *
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
     * Return HashMap with countries and its neighboring countries not belong to player.
     *
     * @param countries the countries
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

    /**
     * Check player specific neigbhouring countries.
     *
     * @param lc the lc
     * @param countries the countries
     * @return the array list
     */
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


    /**
     * Adds the new continent.
     *
     * @param temp the temp
     */
    private void addNewContinent(String temp) {
        logger.log(Level.FINE, "Adding continent " + temp);
        String split_continent[] = temp.split("=");
        Continent cont = new Continent(split_continent[0].trim().toLowerCase());
        cont.setControlValue(Integer.valueOf(split_continent[1]));
        continents.add(cont);
    }


    /**
     * Adds the new country.
     *
     * @param temp the temp
     */
    private void addNewCountry(String temp) {
        logger.log(Level.FINE, "Adding new country " + temp);
        String split_country[] = temp.split(",");
        Country coun = new Country(countries.size(), split_country[0].trim().toLowerCase());
        coun.setCoordinateX(split_country[1].trim());
        coun.setCoordinateY(split_country[2].trim());
        Iterator<Continent> il = continents.iterator();

        String temp_continent_name = split_country[3].trim().toLowerCase();
        while (il.hasNext()) {

            Continent temp_cont = il.next();
            if (temp_cont.getName().equals(temp_continent_name)) {
                temp_cont.addCountry(coun);
                break;
            }
        }
        this.countries.add(coun);
        String arr[] = new String[split_country.length - 3];
        arr[0] = split_country[0].trim().toLowerCase();
        for (int i = 4; i < split_country.length; i++) {
            arr[i - 3] = split_country[i].trim().toLowerCase();
        }
        this.map_name_creation(arr);
    }

    /**
     * Map name creation.
     *
     * @param s the s
     */
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
     * @author Manmeet Singh
     * @param c Pass country object
     * @return Return Continent name.
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


    /**
     * Gets the neighbouring countries.
     *
     * @param country the country
     * @return the neighbouring countries
     */
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
     * Check path valid.
     *
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
     * Adds the path.
     *
     * @param path the path
     * @param allPaths the all paths
     */
    private void addPath(LinkedList<Integer> path, ArrayList<ArrayList<Integer>> allPaths) {
        ArrayList<Integer> temp = new ArrayList<>();
        for (int a : path) {
            temp.add(a);
        }
        allPaths.add(temp);
    }


    /**
     * Count number of path.
     *
     * @param adj the adj
     * @param start the start
     * @param dest the dest
     * @param PathCount the path count
     * @param visited the visited
     * @param path the path
     * @param allPaths the all paths
     * @return the int
     */
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


    /**
     * Search path between countries.
     *
     * @param adj the adj
     * @param currentCountry the current country
     * @param shift the shift
     * @param countries the countries
     * @return true, if successful
     */
    private boolean searchPathBetweenCountries(LinkedList[] adj, int currentCountry, int shift, List<Country> countries) {
        boolean[] v = new boolean[adj.length];
        LinkedList<Integer> movingPath = new LinkedList();
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

    /**
     * Check continent duplicacy.
     *
     * @return true, if successful
     */
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


    /**
     * Check country duplicacy.
     *
     * @return true, if successful
     */
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


    /**
     * Checking contacting.
     *
     * @param adj the adj
     * @param CurrentCountry the current country
     * @param visited the visited
     * @param movingPath the moving path
     */
    private void checkingContacting(LinkedList[] adj, int CurrentCountry, boolean[] visited, LinkedList<Integer> movingPath) {
        visited[CurrentCountry] = true;
        movingPath.add(CurrentCountry);
        Iterator<Integer> i = adj[CurrentCountry].listIterator();
        while (i.hasNext()) {
            int n = i.next();
            if (!visited[n])
                checkingContacting(adj, n, visited, movingPath);
        }
    }

    /**
     * Check isolated country.
     *
     * @return true, if successful
     */
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

    /**
     * This method is responsible to fetch the neighbouring countries to given country.
     *
     * @param c find neighbour of given country
     * @return list of country
     */
    public List<Country> getNeighbouringCountry(Country c) {
        for (LinkedList<Country> ll : adjCountry) {
            Country c1 = ll.get(0);
            if (c1.getName().equals(c.getName())) return ll;
        }
        return null;
    }


    /**
     * Check link map.
     *
     * @param cc the cc
     * @param ff the ff
     * @param country the country
     */
    //MAKE FINAL MAP 
    private void checkLinkMap(Country cc, Country ff, LinkedList<LinkedList<Country>> country) {
        for (List<Country> ll : country) {

            if (ll.get(0).getName().equals(ff.getName())) {
                for (Country c : ll) {
                    if (c.getName().equals(cc.getName())) {
                        return;
                    }
                }
                ll.add(cc);
            }
        }
    }

    /**
     * Final map.
     */
    public void finalMap() {
        for (List<Country> ll : adjCountry) {
            Country cc = ll.get(0);
            int j = 1;
            while (j < ll.size()) {
                this.checkLinkMap(cc, ll.get(j), adjCountry);
                j++;
            }
        }
    }


    /**
     * DFS util.
     *
     * @param v the v
     * @param visited the visited
     * @param arr the arr
     * @param ar the ar
     */
    //CHECK MAP IF COUNTRY IN CONTINENT IS Connected
    private void DFSUtil(int v, boolean visited[], int arr[][], ArrayList<Integer> ar) {
        // Mark the current node as visited and print it 
        visited[v] = true;
        ar.add(v);
        // Recur for all the vertices adjacent to this vertex 
        for (int i = 0; i < arr[v].length; i++) {
            //System.out.println(visited[v]);
            if (arr[v][i] == 1 && !visited[i]) {
                DFSUtil(i, visited, arr, ar);
            }
        }
    }

    /**
     * Dfs.
     *
     * @param arr the arr
     * @param V the v
     * @param v the v
     * @return the int
     */
    // The function to do DFS traversal. It uses recursive DFSUtil() 
    private int DFS(int arr[][], int V, int v) {
        // Mark all the vertices as not visited(set as 
        // false by default in java) 
        boolean visited[] = new boolean[V];
        Arrays.fill(visited, false);
        // Call the recursive helper function to print DFS traversal 
        ArrayList<Integer> ar = new ArrayList<>();
        DFSUtil(v, visited, arr, ar);
        return ar.size();
    }

    /**
     * Check sub graph.
     *
     * @param c the c
     * @param countries the countries
     * @return the array list
     */
    private ArrayList checkSubGraph(Country c, List<Country> countries) {
        for (LinkedList<Country> ll : adjCountry) {
            if (ll.get(0).getName().equals(c.getName())) {
                ArrayList<Country> ar = new ArrayList<>();
                for (int i = 1; i < ll.size(); i++) {
                    if (countries.contains(ll.get(i))) {
                        ar.add(ll.get(i));
                    }
                }
                return ar;
            }
        }
        return null;
    }

    /**
     * Check continent countries connected.
     *
     * @return true, if successful
     */
    public boolean checkContinentCountriesConnected() {
        for (Continent c : continents) {
            int arr[][] = new int[countries.size()][countries.size()];
            for (int i = 0; i < countries.size(); i++) {
                Arrays.fill(arr[i], 0);
            }
            for (Country c1 : c.getCountries()) {
                ArrayList<Country> aa = new ArrayList<>();
                aa = this.checkSubGraph(c1, c.getCountries());
                for (Country mm : aa) {
                    arr[c1.getId()][mm.getId()] = 1;
                }
            }
            if (this.DFS(arr, countries.size(), c.getCountries().get(0).getId()) != c.getCountries().size()) {
                logger.log(Level.INFO, "Connected subgraph test failed.");
                return false;
            }
        }
        logger.log(Level.INFO, "Connected subgraph test passed.");
        return true;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    //------------------------------------------------------------------------------------------------------------------
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the countries.
     *
     * @return the countries
     */
    public ArrayList<Country> getCountries() {
        return countries;
    }

    /**
     * Sets the countries.
     *
     * @param countries the new countries
     */
    public void setCountries(ArrayList<Country> countries) {
        this.countries = countries;
    }

    /**
     * Gets the adj country.
     *
     * @return the adj country
     */
    public LinkedList<LinkedList<Country>> getAdjCountry() {
        return adjCountry;
    }

    /**
     * Gets the list country.
     *
     * @return the list country
     */
    public LinkedList<Object> getListCountry() {
        return list_country;
    }

    /**
     * Sets the adj country.
     *
     * @param adjCountry the new adj country
     */
    public void setAdjCountry(LinkedList<LinkedList<Country>> adjCountry) {
        this.adjCountry = adjCountry;
    }

    /**
     * Gets the continents.
     *
     * @return the continents
     */
    public ArrayList<Continent> getContinents() {
        return continents;
    }

    /**
     * Gets the map country object.
     *
     * @return the map country object
     */
    //Get map on basis of country object
    public LinkedList<LinkedList<Country>> getMapCountryObject() {
        return adjCountry;
    }

    /**
     * Gets the number of countries.
     *
     * @return the number of countries
     */
    //Get number of countries
    public int getNumberOfCountries() {
        return countries.size();
    }

    /**
     * Gets the number of continents.
     *
     * @return the number of continents
     */
    //Get number of continents
    public int getNumberOfContinents() {
        return continents.size();
    }
    // -------------------------------------------------------------

}
