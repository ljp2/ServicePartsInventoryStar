/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicepartsinventorystar;

import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author ljp2
 */
public class Location {

    String id;
    Integer Istar;
    Integer Imax;
    Integer KQmax;
    Integer KEmax;
    Integer orderQuantity;
    Double lambda; //rate at which demand occurs at location
    Double alpha;  //rate at which replenishment lead-time phases transition
    Double beta;   // rate at which reserve restoration shipment time phases transition
    Double A_AL;
    Double L_AL;
    Double B_BL;
    Double L_BL;
    Double A_ABL;
    Double B_ABL;
    Double L_ABL;
    State currentState;
    HashMap<Integer, State> states;

    public Location(String id, Integer orderQuantity, Integer Istar, Integer Imax, Integer KQmax, Integer KEmax, Double lambda, Double alpha, Double beta) {
        this.id = id;
        this.orderQuantity = orderQuantity;
        this.Istar = Istar;
        this.Imax = Imax;
        this.KQmax = KQmax;
        this.KEmax = KEmax;
        this.lambda = lambda;
        this.alpha = alpha;
        this.beta = beta;
        this.A_AL = alpha / (alpha + lambda);
        this.L_AL = lambda / (alpha + lambda);
        this.B_BL = beta / (beta + lambda);
        this.L_BL = lambda / (beta + lambda);
        this.A_ABL = alpha / (alpha + beta + lambda);
        this.B_ABL = beta / (alpha + beta + lambda);
        this.L_ABL = lambda / (alpha + beta + lambda);
        this.currentState = null;
        this.states = new HashMap<>();
    }

    public State CreateState(Integer I, Integer KQ, Integer KE) {
        /**
         * Creates a new state in this location and inserts it into states :
         * HashMap<Integer, State>.
         */
        Integer IKQKE = KE + 100 * KQ + 10000 * I;
        State state = new State(this);
        this.states.put(IKQKE, state);
        state.SetProperties(I, KQ, KE);
        return state;
    }

    public State SetState(Integer I, Integer KQ, Integer KE) {
        Integer IKQKE = KE + 100 * KQ + 10000 * I;
        this.currentState = this.states.get(IKQKE);
        if (this.currentState == null) {
            this.currentState = this.CreateState(I, KQ, KE);
        }
        return this.currentState;
    }

}
