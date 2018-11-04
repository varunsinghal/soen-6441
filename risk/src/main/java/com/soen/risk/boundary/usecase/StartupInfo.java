package com.soen.risk.boundary.usecase;

import com.soen.risk.boundary.Usecase;
import com.soen.risk.boundary.request.StartupInfoRequest;
import com.soen.risk.boundary.response.StartupInfoResponse;
import com.soen.risk.entity.Player;
import com.soen.risk.interactor.GamePlay;

/**
 * The Class StartupInfo.
 */
public class StartupInfo implements Usecase {

    /**
     * The request.
     */
    private StartupInfoRequest request;

    /**
     * The response.
     */
    private StartupInfoResponse response;

    /**
     * Instantiates a new startup info.
     *
     * @param args the args
     */
    public StartupInfo(String... args) {
        request = new StartupInfoRequest(args);
        response = new StartupInfoResponse();
    }

    @Override
    public StartupInfoResponse execute() {
        GamePlay gamePlay = GamePlay.getInstance();
        //gamePlay.requestInfo();

        Player player = gamePlay.getGame().getCurrentPlayer();

        response.setCountryName(player.nextCountryToAssignArmy().getName());
        response.setArmyCapacity(player.getArmyCapacity());

        response.setPhaseView(gamePlay.getPhaseView());
        response.setDominationView(gamePlay.getDominationView());

        return response;
    }
}
