/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicepartsinventorystar;

import java.util.HashMap;

/**
 *
 * @author ljp2
 */
public class TransitionsForDecision extends HashMap<Decision, Transitions> {

    public TransitionsForDecision() {
    }

    public void Add(State fromState, Decision decision, Transition transition) {
        System.out.println(String.format("From %s decision[%s] Transitioning to %s", fromState, decision, transition.state));
        if (this.containsKey(decision) == false) {
            this.put(decision, new Transitions());
        }
        this.get(decision).add(transition);
    }
}
