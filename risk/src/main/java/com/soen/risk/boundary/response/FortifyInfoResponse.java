package com.soen.risk.boundary.response;

import com.soen.risk.boundary.Response;
import com.soen.risk.interactor.DominationView;
import com.soen.risk.interactor.PhaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class FortifyInfoResponse.
 */
public class FortifyInfoResponse implements Response {

    private String playerName;

    private List<String> countryNames;

    private PhaseView phaseView;

    private DominationView dominationView;

    public FortifyInfoResponse() {
        this.countryNames = new ArrayList<>();
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }


    public List<String> getCountryNames() {
        return countryNames;
    }

    public void setCountryNames(List<String> countryNames) {
        this.countryNames = countryNames;
    }

    public PhaseView getPhaseView() {
        return phaseView;
    }

    public void setPhaseView(PhaseView phaseView) {
        this.phaseView = phaseView;
    }

    public DominationView getDominationView() {
        return dominationView;
    }

    public void setDominationView(DominationView dominationView) {
        this.dominationView = dominationView;
    }
}
