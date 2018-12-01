package com.soen.risk.entity.player.random;


import com.soen.risk.entity.Country;
import com.soen.risk.entity.Map;
import com.soen.risk.entity.StartupStrategy;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RandomStartupStrategyTest {
    private Map map;
    private StartupStrategy random;
    private Country country1;
    private Country country4;

    @Before
    public void setUp() {
        map = new Map();
        map.load("./fixture/demo.map");
        random = new RandomStartupStrategy();
        country1 = map.findByCountryName("Country1");
        country4 = map.findByCountryName("Country4");
    }

    @Test
    public void ZeroInitialArmy_ShouldAddOneArmy() {
        country1.setArmy(0);
        random.execute(country1, 10);
        assertEquals(1, country1.getArmy());
    }

    @Test
    public void WithInitialArmy_ShouldAddOneArmy() {
        int expectedCount = country1.getArmy() + 1;
        random.execute(country1, 11);
        assertEquals(expectedCount, country1.getArmy());
    }
}