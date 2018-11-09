package com.soen.risk.boundary.usecase;

import com.soen.risk.boundary.Usecase;
import com.soen.risk.boundary.request.ReinforceInfoRequest;
import com.soen.risk.boundary.response.ReinforceInfoResponse;
import com.soen.risk.interactor.GamePlay;

public class ReinforceInfo implements Usecase  {

    private ReinforceInfoRequest request;
    private ReinforceInfoResponse response;

    public ReinforceInfo(String... args){
        request = new ReinforceInfoRequest(args);
        response = new ReinforceInfoResponse();
    }
    
    /**
     * Instantiates a new reinforce info for acting as Observer.
     *
     // @param subject the subject that notifies observers.
     */
//    public ReinforceInfo(CardExchange subject){
//        this.cardExchangeObject = subject;
//        this.cardExchangeObject.attach(this);
//     }
//
    /* (non-Javadoc)
     * @see com.soen.risk.boundary.Usecase#execute()
     */
    @Override
    public ReinforceInfoResponse execute() {
       GamePlay gamePlay = GamePlay.getInstance();
        response = (ReinforceInfoResponse) gamePlay.getPhaseInfo(response);
        response.setCardExchangeView(gamePlay.getCardExchangeView());
        response.setPhaseView(GamePlay.getInstance().getPhaseView());
        response.setDominationView(GamePlay.getInstance().getDominationView());
        return response;
    }

//	@Override
//	public void update() {
//		GamePlay gamePlay = GamePlay.getInstance();
//		gamePlay.addNewArmies();
//	}
}
