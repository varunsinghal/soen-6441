package com.soen.risk.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.*;

import java.io.File;
import java.io.IOException;
/**
 * The Class MapTest.
 */
import java.util.Scanner;
public class MapTest {

    /**
     * The map.
     */
    private static Map map, map1, map2, map3, map4;
    String fileName="createnew.map";

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
        map = new Map();
    }

    @BeforeClass
    public static void set() {
        map1 = new Map();
        map1.load("map1.map");

        map2 = new Map();
        map2.load("map2.map");
      
        map3 = new Map();
        map3.load("map3.map");
      
        map4 = new Map();
        map4.load("invalid.map");
        }
    /**
     * Adds the country.
     */
    @Test
    public void addCountry() {
        Country country = new Country(13, "Africa");
        map.addCountry(country);
        assertNotNull(map.getCountries());
        //assertEquals(map.get);

    }

    /**
     * Load.
     */
    @Test
    public void load() {
      map.load(fileName);
      assertEquals(2, map.getContinents().size());
      assertEquals(4, map.getCountries().size());
    }


    /**
     * Adds the continent.
     */
    @Test
    public void addContinent() {
        Continent c = new Continent("Cont-Cont");
        c.setControlValue(3);
        map.addContinent(c);
        assertNotNull(map.getContinents());
    }


    /**
     * Find by country name.
     */
    @Test
    public void findByCountryName() {
        Country country = new Country(0, "Africa");
        Country country1 = new Country(1, "India");
        Country country2 = new Country(2, "Bangladesh");
        map.addCountry(country);
        map.addCountry(country1);
        map.addCountry(country2);
        assertNotNull(map.getCountries());
        assertEquals(country1, map.findByCountryName("India"));
    }

    /**
     * Find by continent name.
     */
    @Test
    public void findByContinentName() {
        Continent c = new Continent("Cont-Cont");
        c.setControlValue(3);
        Continent c1 = new Continent("Cont-Cont2");
        c1.setControlValue(4);
        Continent c2 = new Continent("Cont-Cont3");
        c2.setControlValue(5);
        map.addContinent(c);
        map.addContinent(c1);
        map.addContinent(c2);
        assertNotNull(map.findByContinentName("Cont-Cont2"));
    }


    /**
     * Map name creation.
     */
    @Test
    public void map_name_creation() {
       map.load(fileName);
        assertNotNull(map.getListCountry());
    }


    /**
     * Map country object creation.
     */
    @Test
    public void map_country_object_creation() {
       map.load(fileName);
        map.map_country_object_creation();
        assertNotNull(map.getAdjCountry());
    }

    /**
     * Checks if is valid country duplicacy.
     */
    @Test
    public void isValidCountryDuplicacy() {

        assertTrue(map1.checkCountryDuplicacy());
    }

    /**
     * Checks if is valid continent duplicacy.
     */
    @Test
    public void isValidContinentDuplicacy() {

        assertTrue(map2.checkContinentDuplicacy());
    }

    /**
     * Checks if is valid isolated country.
     */
    @Test
    public void isValidIsolatedCountry() {

        assertTrue(map3.checkIsolatedCountry());

    }

   @Test
    public void isValid() {
    
        assertFalse(map4.isValid());
    }


    @Test
    public void getAdjCountries() {
    }

}