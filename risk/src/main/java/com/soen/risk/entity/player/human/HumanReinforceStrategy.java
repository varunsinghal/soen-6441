package com.soen.risk.entity.player.human;

import com.soen.risk.entity.Country;
import com.soen.risk.entity.Map;
import com.soen.risk.entity.ReinforceStrategy;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Class HumanReinforceStrategy.
 *
 * @author Amit
 */
public class HumanReinforceStrategy implements ReinforceStrategy, Serializable {

    /**
     * The army counts.
     */
    private List<Integer> armyCounts;

    /**
     * The logger.
     */
    private static Logger logger = Logger.getLogger(HumanReinforceStrategy.class.getName());


    /**
     * Instantiates a new human reinforce strategy.
     *
     * @param armyCounts the army counts
     */
    public HumanReinforceStrategy(List<Integer> armyCounts) {
        this.armyCounts = armyCounts;
    }

    /* (non-Javadoc)
     * @see com.soen.risk.entity.ReinforceStrategy#execute(com.soen.risk.entity.Map, java.util.List)
     */
    @Override
    public void execute(Map map, List<Country> countries) {
        int i = 0;
        for (Country c : countries) {
            logger.log(Level.INFO, "Adding reinforce army to country " + c.getName() + ", army count " + armyCounts.get(i));
            c.addArmy(armyCounts.get(i)); // new army count of the country
            i++;
        }
    }
}
