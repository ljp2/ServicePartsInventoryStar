/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicepartsinventorystar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author ljp2
 */
public class Location {

    String id;
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
    HashSet<String> unimplementedStates;

    public Location(String id, Integer orderQuantity, Integer Imax, Integer KQmax, Integer KEmax, Double lambda, Double alpha, Double beta) {
        this.id = id;
        this.orderQuantity = orderQuantity;
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
        this.unimplementedStates = new HashSet<>();
    }

    public class Transition {

        State state;
        Double prob;

        public Transition(State state, Double prob) {
            this.state = state;
            this.prob = prob;
        }

        public Transition(Integer I, Integer KQ, Integer KE, Double prob) {
            //System.out.println(String.format("     Transitioning to %d %d %d", I, KQ, KE));
            Integer IKQKE = KE + 100 * KQ + 10000 * I;
            this.state = Location.this.states.get(IKQKE);
            if (this.state == null) {
                this.state = Location.this.CreateState(I, KQ, KE);
            }
            this.prob = prob;
        }

        @Override
        public String toString() {
            return String.format("%d   %.3f", this.state, this.prob);
        }

    }

    public class Transitions extends ArrayList<Transition> {

        public Transitions() {
        }
    }

    public class TransitionsForDecision extends HashMap<Decision, Transitions> {

        public TransitionsForDecision() {
        }

        public void Add(State fromState, Decision decision, Location.Transition transition) {
            System.out.println(String.format("From %s decision[%s] Transitioning to %s", fromState, decision, transition.state));
            if (this.containsKey(decision) == false) {
                this.put(decision, new Transitions());
            }
            this.get(decision).add(transition);
        }
    }

    public State CreateState(Integer I, Integer KQ, Integer KE) {
        Integer IKQKE = KE + 100 * KQ + 10000 * I;
        State state = new State();
        this.states.put(IKQKE, state);
        state.SetProperties(I, KQ, KE);
        return state;
    }

    public class State {

        Integer I;
        Integer KQ;
        Integer KE;
        ArrayList<Decision> possibleDecisions;
        TransitionsForDecision transitionsForDecision;
        String code;

        public State() {
        }

        public State(Integer I, Integer KQ, Integer KE) {
            this.SetProperties(I, KQ, KE);
        }

        public final void SetProperties(Integer I, Integer KQ, Integer KE) {
            this.I = I;
            this.KQ = KQ;
            this.KE = KE;
            this.code = DetermineIKQKECode();
            System.out.println(String.format("Creating %s", this));
            this.transitionsForDecision = DetermineAllTransitionsForAllDecisions();
            this.possibleDecisions = new ArrayList<>();
            this.transitionsForDecision.keySet().forEach((d) -> {
                this.possibleDecisions.add(d);
            });
        }

        @Override
        public String toString() {
            return String.format("%s %d %d %d", this.code, this.I, this.KQ, this.KE);
        }

        private TransitionsForDecision DetermineAllTransitions_IZ_KQZ_KEZ() {
            TransitionsForDecision td = new TransitionsForDecision();
            td.Add(this, Decision.N, new Transition(0, 0, 0, 1.0));
            td.Add(this, Decision.NQ, new Transition(0, 2, 0, Location.this.A_AL));
            td.Add(this, Decision.NQ, new Transition(0, 1, 0, Location.this.L_AL));
            td.Add(this, Decision.NQR, new Transition(0, 1, 1, Location.this.L_ABL));
            td.Add(this, Decision.NQR, new Transition(0, 2, 1, Location.this.A_ABL));
            td.Add(this, Decision.NQR, new Transition(0, 1, 2, Location.this.B_ABL));
            return td;
        }

        private TransitionsForDecision DetermineAllTransitions_IZ_KQZ_KEG() {
            TransitionsForDecision td = new TransitionsForDecision();
            td.Add(this, Decision.N, new Transition(0, 0, this.KE, Location.this.L_BL));
            td.Add(this, Decision.N, new Transition(0, 0, this.KE + 1, Location.this.B_BL));
            td.Add(this, Decision.NQ, new Transition(0, 1, this.KE, Location.this.L_ABL));
            td.Add(this, Decision.NQ, new Transition(0, 2, this.KE, Location.this.A_ABL));
            td.Add(this, Decision.NQ, new Transition(0, 1, this.KE + 1, Location.this.B_ABL));
            return td;
        }

        private TransitionsForDecision DetermineAllTransitions_IZ_KQG_KEZ() {
            TransitionsForDecision td = new TransitionsForDecision();
            td.Add(this, Decision.N, new Transition(0, this.KQ, 0, Location.this.L_AL));
            td.Add(this, Decision.N, new Transition(0, this.KQ + 1, 0, Location.this.A_AL));
            td.Add(this, Decision.NR, new Transition(0, this.KQ, 1, Location.this.L_ABL));
            td.Add(this, Decision.NR, new Transition(0, this.KQ, 2, Location.this.B_ABL));
            td.Add(this, Decision.NR, new Transition(0, this.KQ + 1, 1, Location.this.A_ABL));
            return td;
        }

        private TransitionsForDecision DetermineAllTransitions_IZ_KQG_KEG() {
            TransitionsForDecision td = new TransitionsForDecision();
            td.Add(this, Decision.N, new Transition(0, this.KQ + 1, this.KE, Location.this.A_ABL));
            td.Add(this, Decision.N, new Transition(0, this.KQ, this.KE + 1, Location.this.B_ABL));
            td.Add(this, Decision.N, new Transition(0, this.KQ, this.KE, Location.this.L_ABL));
            return td;
        }

        private TransitionsForDecision DetermineAllTransitions_IZ_KQM_KEG() {
            TransitionsForDecision td = new TransitionsForDecision();
            td.Add(this, Decision.N, new Transition(0, Location.this.KQmax, this.KE, Location.this.L_ABL));
            td.Add(this, Decision.N, new Transition(Location.this.orderQuantity, 0, this.KE, Location.this.A_ABL));
            td.Add(this, Decision.N, new Transition(0, Location.this.KQmax, this.KE + 1, Location.this.B_ABL));
            return td;
        }

        private TransitionsForDecision DetermineAllTransitions_IZ_KQG_KEM() {
            TransitionsForDecision td = new TransitionsForDecision();
            td.Add(this, Decision.N, new Transition(0, this.KQ, Location.this.KEmax, Location.this.L_ABL));
            td.Add(this, Decision.N, new Transition(0, this.KQ + 1, Location.this.KEmax, Location.this.A_ABL));
            td.Add(this, Decision.N, new Transition(1, this.KQ, 0, Location.this.B_ABL));
            return td;
        }

        private TransitionsForDecision DetermineAllTransitions_IZ_KQM_KEM() {
            TransitionsForDecision td = new TransitionsForDecision();
            td.Add(this, Decision.N, new Transition(0, Location.this.KQmax, Location.this.KEmax, Location.this.L_ABL));
            td.Add(this, Decision.N, new Transition(Location.this.orderQuantity, 0, Location.this.KEmax, Location.this.A_ABL));
            td.Add(this, Decision.N, new Transition(1, Location.this.KQmax, 0, Location.this.B_ABL));
            return td;
        }

        private TransitionsForDecision DetermineAllTransitions_IgZ_KQZ_KEZ() {
            TransitionsForDecision td = new TransitionsForDecision();
            td.Add(this, Decision.Z, new Transition(this.I - 1, 0, 0, 1.0));
            td.Add(this, Decision.Q, new Transition(this.I - 1, 1, 0, Location.this.L_AL));
            td.Add(this, Decision.Q, new Transition(this.I, 2, 0, Location.this.A_AL));
            td.Add(this, Decision.R, new Transition(this.I - 1, 0, 1, Location.this.L_BL));
            td.Add(this, Decision.R, new Transition(this.I, 0, 2, Location.this.B_BL));
            td.Add(this, Decision.QR, new Transition(this.I - 1, 1, 1, Location.this.L_ABL));
            td.Add(this, Decision.QR, new Transition(this.I, 2, 1, Location.this.A_ABL));
            td.Add(this, Decision.QR, new Transition(this.I, 1, 2, Location.this.B_ABL));
            return td;
        }

        private TransitionsForDecision DetermineAllTransitions_IgZ_KQZ_KEG() {
            TransitionsForDecision td = new TransitionsForDecision();
            td.Add(this, Decision.Z, new Transition(this.I - 1, 0, this.KE, Location.this.L_BL));
            td.Add(this, Decision.Z, new Transition(this.I, 0, this.KE + 1, Location.this.B_BL));
            td.Add(this, Decision.Q, new Transition(this.I - 1, 1, this.KE, Location.this.L_ABL));
            td.Add(this, Decision.Q, new Transition(this.I, 2, this.KE, Location.this.B_ABL));
            td.Add(this, Decision.Q, new Transition(this.I, 1, this.KE + 1, Location.this.L_ABL));
            return td;
        }

        private TransitionsForDecision DetermineAllTransitions_IgZ_KQG_KEZ() {
            TransitionsForDecision td = new TransitionsForDecision();
            td.Add(this, Decision.Z, new Transition(this.I - 1, this.KQ, 0, Location.this.L_AL));
            td.Add(this, Decision.Z, new Transition(this.I, this.KQ + 1, 0, Location.this.A_AL));
            td.Add(this, Decision.R, new Transition(this.I - 1, this.KQ, 1, Location.this.L_ABL));
            td.Add(this, Decision.R, new Transition(this.I, this.KQ + 1, 1, Location.this.A_ABL));
            td.Add(this, Decision.R, new Transition(this.I, this.KQ, 2, Location.this.L_ABL));
            return td;
        }

        private TransitionsForDecision DetermineAllTransitions_IgZ_KQG_KEG() {
            TransitionsForDecision td = new TransitionsForDecision();
            td.Add(this, Decision.Z, new Transition(this.I - 1, this.KQ, this.KE, Location.this.L_ABL));
            td.Add(this, Decision.Z, new Transition(this.I, this.KQ + 1, this.KE, Location.this.A_ABL));
            td.Add(this, Decision.Z, new Transition(this.I, this.KQ, this.KE + 1, Location.this.B_ABL));
            return td;
        }

        private TransitionsForDecision DetermineAllTransitions_IR_KQM_KEG() {
            TransitionsForDecision td = new TransitionsForDecision();
            td.Add(this, Decision.Z, new Transition(this.I - 1, Location.this.KQmax, this.KE, Location.this.L_ABL));
            td.Add(this, Decision.Z, new Transition(this.I + Location.this.KQmax, 0, this.KE, Location.this.A_ABL));
            td.Add(this, Decision.Z, new Transition(this.I, Location.this.KQmax, this.KE, Location.this.B_ABL));
            return td;
        }

        private TransitionsForDecision DetermineAllTransitions_IG_KQG_KEM() {
            TransitionsForDecision td = new TransitionsForDecision();
            td.Add(this, Decision.Z, new Transition(this.I - 1, this.KQ, Location.this.KEmax, Location.this.L_ABL));
            td.Add(this, Decision.Z, new Transition(this.I, this.KQ + 1, Location.this.KEmax, Location.this.A_ABL));
            td.Add(this, Decision.Z, new Transition(this.I + 1, this.KQ, 0, Location.this.B_ABL));
            return td;
        }

        private TransitionsForDecision DetermineAllTransitions_IR_KQM_KEM() {
            TransitionsForDecision td = new TransitionsForDecision();
            td.Add(this, Decision.Z, new Transition(this.I - 1, Location.this.KQmax, Location.this.KEmax, Location.this.L_ABL));
            td.Add(this, Decision.Z, new Transition(this.I + Location.this.orderQuantity, 0, Location.this.KEmax, Location.this.A_ABL));
            td.Add(this, Decision.Z, new Transition(this.I + 1, Location.this.KQmax, 0, Location.this.L_ABL));
            return td;
        }

        private TransitionsForDecision DetermineAllTransitions_IR_KQM_KEZ() {
            TransitionsForDecision td = new TransitionsForDecision();
            td.Add(this, Decision.Z, new Transition(this.I - 1, Location.this.KQmax, 0, Location.this.L_AL));
            td.Add(this, Decision.Z, new Transition(this.I + Location.this.orderQuantity, 0, 0, Location.this.A_AL));
            td.Add(this, Decision.R, new Transition(this.I - 1, Location.this.KQmax, 1, Location.this.L_ABL));
            td.Add(this, Decision.R, new Transition(this.I + Location.this.orderQuantity, 0, 1, Location.this.A_ABL));
            td.Add(this, Decision.R, new Transition(this.I, Location.this.KQmax, 2, Location.this.B_ABL));
            return td;
        }

        private TransitionsForDecision DetermineAllTransitions_IZ_KQZ_KEM() {
            TransitionsForDecision td = new TransitionsForDecision();
            td.Add(this, Decision.N, new Transition(0, 0, Location.this.KEmax, Location.this.L_BL));
            td.Add(this, Decision.N, new Transition(1, 0, 0, Location.this.B_BL));
            td.Add(this, Decision.NQ, new Transition(0, 1, Location.this.KEmax, Location.this.L_ABL));
            td.Add(this, Decision.NQ, new Transition(0, 2, Location.this.KEmax, Location.this.A_ABL));
            td.Add(this, Decision.NQ, new Transition(1, 1, 0, Location.this.B_ABL));
            return td;
        }

        private TransitionsForDecision DetermineAllTransitions_IZ_KQM_KEZ() {
            TransitionsForDecision td = new TransitionsForDecision();
            td.Add(this, Decision.N, new Transition(0, Location.this.KQmax, 0, Location.this.L_AL));
            td.Add(this, Decision.N, new Transition(Location.this.orderQuantity, 0, 0, Location.this.A_AL));
            td.Add(this, Decision.NR, new Transition(0, Location.this.KQmax, 1, Location.this.L_ABL));
            td.Add(this, Decision.NR, new Transition(Location.this.orderQuantity, 0, 1, Location.this.A_ABL));
            td.Add(this, Decision.NR, new Transition(0, Location.this.KQmax, 2, Location.this.B_ABL));
            return td;
        }

        private TransitionsForDecision DetermineAllTransitions_IgZ_KQZ_KEM() {
            TransitionsForDecision td = new TransitionsForDecision();
            td.Add(this, Decision.Z, new Transition(this.I - 1, 0, Location.this.KEmax, Location.this.L_BL));
            td.Add(this, Decision.Z, new Transition(this.I + 1, 0, 0, Location.this.B_BL));
            td.Add(this, Decision.Q, new Transition(this.I - 1, 1, Location.this.KEmax, Location.this.L_ABL));
            td.Add(this, Decision.Q, new Transition(this.I, 2, Location.this.KEmax, Location.this.L_ABL));
            td.Add(this, Decision.Q, new Transition(this.I + 1, 1, 0, Location.this.L_ABL));
            return td;
        }

        private char DetermineZGMCode(int val, int maxval) {
            // used for KQ and KE
            char code;
            if (val == 0) {
                code = 'Z';
            } else if (val < maxval) {
                code = 'G';
            } else {
                code = 'M';
            }
            return code;
        }

        private String DetermineIKQKECode() {
            String code;
            if (this.I == 0) {
                code = "Z";
            } else if (this.I <= Location.this.Imax - Location.this.orderQuantity) {
                code = "R";
            } else if (this.I <= Location.this.Imax - 1) {
                code = "G";
            } else if (this.I.equals(Location.this.Imax)) {
                code = "M";
            } else {
                String err = String.format("I value greater than Imax. val = %d, Imax = %d ", this.I, Location.this.Imax);
                throw new IllegalArgumentException(err);
            }
            code += DetermineZGMCode(this.KQ, Location.this.KQmax);
            code += DetermineZGMCode(this.KE, Location.this.KEmax);
            return code;
        }

        private TransitionsForDecision DetermineAllTransitionsForAllDecisions() {
            //System.out.println("Determining transitions for code " + this.code);
            switch (this.code) {
                case "ZZZ":
                    return this.DetermineAllTransitions_IZ_KQZ_KEZ();

                case "ZZG":
                    return this.DetermineAllTransitions_IZ_KQZ_KEG();

                case "ZZM":
                    return this.DetermineAllTransitions_IZ_KQZ_KEM();

                case "ZGZ":
                    return this.DetermineAllTransitions_IZ_KQG_KEZ();

                case "ZGG":
                    return this.DetermineAllTransitions_IZ_KQG_KEG();

                case "ZGM":
                    return this.DetermineAllTransitions_IZ_KQG_KEM();

                case "ZMG":
                    return this.DetermineAllTransitions_IZ_KQM_KEG();

                case "ZMM":
                    return this.DetermineAllTransitions_IZ_KQM_KEM();

                case "RZZ":
                case "GZZ":
                case "MZZ":
                    return this.DetermineAllTransitions_IgZ_KQZ_KEZ();

                case "RZG":
                case "GZG":
                case "MZG":
                    return this.DetermineAllTransitions_IgZ_KQZ_KEG();

                case "RZM":
                case "GZM":
                    return this.DetermineAllTransitions_IgZ_KQZ_KEM();

                case "RGZ":
                case "GGZ":
                case "MGZ":
                    return this.DetermineAllTransitions_IgZ_KQG_KEZ();

                case "RGG":
                case "GGG":
                case "MGG":
                    return this.DetermineAllTransitions_IgZ_KQG_KEG();

                case "RMG":
                    return this.DetermineAllTransitions_IR_KQM_KEG();

                case "RGM":
                case "GGM":
                    return this.DetermineAllTransitions_IG_KQG_KEM();

                case "RMM":
                    return this.DetermineAllTransitions_IR_KQM_KEM();

                case "RMZ":
                    return this.DetermineAllTransitions_IR_KQM_KEZ();

                case "ZMZ":
                    return this.DetermineAllTransitions_IZ_KQM_KEZ();

                //
                // These cases are not covered in document
                //
                case "GMZ":
                    //throw new IllegalArgumentException("State not implemented: " + code);
                    return this.NotImplementedState("GMZ");
                case "GMG":
                    //throw new IllegalArgumentException("State not implemented: " + code);
                    return this.NotImplementedState("GMG");
                case "GMM":
                    //throw new IllegalArgumentException("State not implemented: " + code);
                    return this.NotImplementedState("GMM");
                case "MZM":
                    //throw new IllegalArgumentException("State not implemented: " + code);
                    return this.NotImplementedState("MZM");
                case "MGM":
                    //throw new IllegalArgumentException("State not implemented: " + code);
                    return this.NotImplementedState("MGM");
                case "MMZ":
                    //throw new IllegalArgumentException("State not implemented: " + code);
                    return this.NotImplementedState("MMZ");
                case "MMG":
                    //throw new IllegalArgumentException("State not implemented: " + code);
                    return this.NotImplementedState("MMG");
                case "MMM":
                    //throw new IllegalArgumentException("State not implemented: " + code);
                    return this.NotImplementedState("MMM");
                default:
                    throw new IllegalArgumentException("Unknown state code value for transitions: " + this.code);
            }
        }

        private TransitionsForDecision NotImplementedState(String notImplemented) {
            Location.this.unimplementedStates.add((notImplemented));
            System.out.println(String.format("*** %s Transitions Not Implemented State", this));
            TransitionsForDecision td = new TransitionsForDecision();
            td.Add(this, Decision.Z, new Transition(this.I, this.KQ, this.KE, 1.0));
            return td;
        }

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
