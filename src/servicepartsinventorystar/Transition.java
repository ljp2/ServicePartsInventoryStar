/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicepartsinventorystar;

/**
 *
 * @author ljp2
 */
   public class Transition {

        State state;
        Double prob;

        public Transition(State state, Double prob) {
            this.state = state;
            this.prob = prob;
        }

        public Transition(Location location, Integer I, Integer KQ, Integer KE, Double prob) {
            //System.out.println(String.format("     Transitioning to %d %d %d", I, KQ, KE));
            Integer IKQKE = KE + 100 * KQ + 10000 * I;
            this.state = location.states.get(IKQKE);
            if (this.state == null) {
                this.state = location.CreateState(I, KQ, KE);
            }
            this.prob = prob;
        }

        @Override
        public String toString() {
            return String.format("%d   %.3f", this.state, this.prob);
        }

    }
