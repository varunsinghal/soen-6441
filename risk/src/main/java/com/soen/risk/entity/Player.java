package com.soen.risk.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is responsible to hold the name, army capacity remaining and list of countries owned by the
 * player instance. It is being observed by the domination view to calculate the share of each player.
 *
 * @author Amit Sachdeva, Varun Singhal
 * @version 2.0.0
 * @since 2018-10-10
 */
public class Player extends Observable {
    private String name;
    private int armyCapacity;
    private List<Country> countries;
    private static Logger logger = Logger.getLogger(Player.class.getName());

    /**
     * Initialise the player with given suffix and empty list of owned countries.
     *
     * @param nameSuffix name of player
     */
    public Player(int nameSuffix) {
        this.name = "Player_" + String.valueOf(nameSuffix);
        this.countries = new ArrayList<>();
    }

    /**
     * Add new country to countries object
     *
     * @param c country
     */
    void addCountry(Country c) {
        this.countries.add(c);
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * When a match will be lost then remove country will be called
     */
    public void removeCountry(Country c) {
        // TODO: code to remove country
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * nextCountryToAssignArmy()
     *
     * @return Object of country to which army is assign
     */
    public Country nextCountryToAssignArmy() {
        for (Country country : countries) {
            if (country.isEmpty())
                return country;
        }
        return countries.get(countries.size() - 1);
    }

    /**
     * To allocate initial army we should know about the countries owned.
     */
    void allocateInitialArmy() {
        Random rand = new Random();
        this.setArmyCapacity(this.getCountries().size() * (2 + rand.nextInt(2)));
        logger.log(Level.INFO, "Adding army capacity to " + this.getName() + " " + this.getArmyCapacity());

    }

    // -------------------------------------------------------------

    /**
     * calculateReinforceCount(Map m): Checking if player has all countries of whole continent then give army according to
     * control value, otherwise give armies according to countries player own.
     *
     * @param m Reference of map class
     */
    public void calculateReinforceCount(Map m) {
        //Check if player has all country of a continent
        for (Continent ctt : m.getContinents()) {
            int size = ctt.getCountries().size();
            int count = 0;
            for (Country player_countries : this.getCountries()) {
                for (Country continent_countries : ctt.getCountries()) {
                    if (continent_countries.getName().equals(player_countries.getName())) {
                        count++;
                    }
                }
            }
            if (size == count) {
                armyCapacity = ctt.getControlValue();
            }

        }
        //If Player do not have all country of a continent
        int number_of_countries = this.getCountries().size();
        armyCapacity = Math.max(3, (int) Math.ceil(number_of_countries / 3.0));

    }

    //Return All Countries Name of Player
    public List<String> getCountryNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Country country : countries)
            names.add(country.getName());
        return names;
    }

    public int getTotalArmyCount() {
        int armyCount = 0;
        for (Country country : this.countries) {
            armyCount += country.getArmy();
        }
        return armyCount;
    }

    public String toString() {
        return this.name;
    }

    public String getName() {
        return name;
    }

    public List<Country> getCountries() {
        return this.countries;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getArmyCapacity() {
        return armyCapacity;
    }

    public void setArmyCapacity(int armyCapacity) {
        this.armyCapacity = armyCapacity;
        this.setChanged();
        this.notifyObservers();
    }

}
