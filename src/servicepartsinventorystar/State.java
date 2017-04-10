/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicepartsinventorystar;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author ljp2
 */
public class State {

    Location location;
    Integer I;
    Integer KQ;
    Integer KE;
    ArrayList<Decision> possibleDecisions;
    TransitionsForDecision transitionsForDecision;
    String code;

    public State(Location location) {
        this.location = location;
    }

    public State(Location location, Integer I, Integer KQ, Integer KE) {
        this.location = location;
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

    private char DetermineZGMcode(int val, int maxval) {
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

    private String DetermineIcode() {
        String icode;
        if (this.I == 0) {
            icode = "Z";
        } else if (this.I <= location.Istar) {
            icode = "S";
        } else if (this.I < location.Imax) {
            icode = "Sp";
        } else if (this.I == location.Imax) {
            icode = "M";
        } else {
            String err = String.format("I value greater than Imax. val = %d, Imax = %d ", this.I, location.Imax);
            throw new IllegalArgumentException(err);
        }
        return icode;
    }

    private String DetermineIKQKECode() {
        return DetermineIcode() 
                + DetermineZGMCode(this.KQ, location.KQmax) 
                + DetermineZGMCode(this.KE, location.KEmax);
    }

    private TransitionsForDecision DetermineAllTransitions_IZ_KQZ_KEZ() {
        TransitionsForDecision td = new TransitionsForDecision();
        td.Add(this, Decision.N, new Transition(this.location, 0, 0, 0, 1.0));
        td.Add(this, Decision.NQ, new Transition(this.location, 0, 2, 0, location.A_AL));
        td.Add(this, Decision.NQ, new Transition(this.location, 0, 1, 0, location.L_AL));
        td.Add(this, Decision.NQR, new Transition(this.location, 0, 1, 1, location.L_ABL));
        td.Add(this, Decision.NQR, new Transition(this.location, 0, 2, 1, location.A_ABL));
        td.Add(this, Decision.NQR, new Transition(this.location, 0, 1, 2, location.B_ABL));
        return td;
    }

    private TransitionsForDecision DetermineAllTransitions_IZ_KQZ_KEG() {
        TransitionsForDecision td = new TransitionsForDecision();
        td.Add(this, Decision.N, new Transition(this.location, 0, 0, this.KE, location.L_BL));
        td.Add(this, Decision.N, new Transition(this.location, 0, 0, this.KE + 1, location.B_BL));
        td.Add(this, Decision.NQ, new Transition(this.location, 0, 1, this.KE, location.L_ABL));
        td.Add(this, Decision.NQ, new Transition(this.location, 0, 2, this.KE, location.A_ABL));
        td.Add(this, Decision.NQ, new Transition(this.location, 0, 1, this.KE + 1, location.B_ABL));
        return td;
    }

    private TransitionsForDecision DetermineAllTransitions_IZ_KQG_KEZ() {
        TransitionsForDecision td = new TransitionsForDecision();
        td.Add(this, Decision.N, new Transition(this.location, 0, this.KQ, 0, location.L_AL));
        td.Add(this, Decision.N, new Transition(this.location, 0, this.KQ + 1, 0, location.A_AL));
        td.Add(this, Decision.NR, new Transition(this.location, 0, this.KQ, 1, location.L_ABL));
        td.Add(this, Decision.NR, new Transition(this.location, 0, this.KQ, 2, location.B_ABL));
        td.Add(this, Decision.NR, new Transition(this.location, 0, this.KQ + 1, 1, location.A_ABL));
        return td;
    }

    private TransitionsForDecision DetermineAllTransitions_IZ_KQG_KEG() {
        TransitionsForDecision td = new TransitionsForDecision();
        td.Add(this, Decision.N, new Transition(this.location, 0, this.KQ + 1, this.KE, location.A_ABL));
        td.Add(this, Decision.N, new Transition(this.location, 0, this.KQ, this.KE + 1, location.B_ABL));
        td.Add(this, Decision.N, new Transition(this.location, 0, this.KQ, this.KE, location.L_ABL));
        return td;
    }

    private TransitionsForDecision DetermineAllTransitions_IZ_KQM_KEG() {
        TransitionsForDecision td = new TransitionsForDecision();
        td.Add(this, Decision.N, new Transition(this.location, 0, location.KQmax, this.KE, location.L_ABL));
        td.Add(this, Decision.N, new Transition(this.location, location.orderQuantity, 0, this.KE, location.A_ABL));
        td.Add(this, Decision.N, new Transition(this.location, 0, location.KQmax, this.KE + 1, location.B_ABL));
        return td;
    }

    private TransitionsForDecision DetermineAllTransitions_IZ_KQG_KEM() {
        TransitionsForDecision td = new TransitionsForDecision();
        td.Add(this, Decision.N, new Transition(this.location, 0, this.KQ, location.KEmax, location.L_ABL));
        td.Add(this, Decision.N, new Transition(this.location, 0, this.KQ + 1, location.KEmax, location.A_ABL));
        td.Add(this, Decision.N, new Transition(this.location, 1, this.KQ, 0, location.B_ABL));
        return td;
    }

    private TransitionsForDecision DetermineAllTransitions_IZ_KQM_KEM() {
        TransitionsForDecision td = new TransitionsForDecision();
        td.Add(this, Decision.N, new Transition(this.location, 0, location.KQmax, location.KEmax, location.L_ABL));
        td.Add(this, Decision.N, new Transition(this.location, location.orderQuantity, 0, location.KEmax, location.A_ABL));
        td.Add(this, Decision.N, new Transition(this.location, 1, location.KQmax, 0, location.B_ABL));
        return td;
    }

    private TransitionsForDecision DetermineAllTransitions_IgZ_KQZ_KEZ() {
        TransitionsForDecision td = new TransitionsForDecision();
        td.Add(this, Decision.Z, new Transition(this.location, this.I - 1, 0, 0, 1.0));
        td.Add(this, Decision.Q, new Transition(this.location, this.I - 1, 1, 0, location.L_AL));
        td.Add(this, Decision.Q, new Transition(this.location, this.I, 2, 0, location.A_AL));
        td.Add(this, Decision.R, new Transition(this.location, this.I - 1, 0, 1, location.L_BL));
        td.Add(this, Decision.R, new Transition(this.location, this.I, 0, 2, location.B_BL));
        td.Add(this, Decision.QR, new Transition(this.location, this.I - 1, 1, 1, location.L_ABL));
        td.Add(this, Decision.QR, new Transition(this.location, this.I, 2, 1, location.A_ABL));
        td.Add(this, Decision.QR, new Transition(this.location, this.I, 1, 2, location.B_ABL));
        return td;
    }

    private TransitionsForDecision DetermineAllTransitions_IgZ_KQZ_KEG() {
        TransitionsForDecision td = new TransitionsForDecision();
        td.Add(this, Decision.Z, new Transition(this.location, this.I - 1, 0, this.KE, location.L_BL));
        td.Add(this, Decision.Z, new Transition(this.location, this.I, 0, this.KE + 1, location.B_BL));
        td.Add(this, Decision.Q, new Transition(this.location, this.I - 1, 1, this.KE, location.L_ABL));
        td.Add(this, Decision.Q, new Transition(this.location, this.I, 2, this.KE, location.B_ABL));
        td.Add(this, Decision.Q, new Transition(this.location, this.I, 1, this.KE + 1, location.L_ABL));
        return td;
    }

    private TransitionsForDecision DetermineAllTransitions_IgZ_KQG_KEZ() {
        TransitionsForDecision td = new TransitionsForDecision();
        td.Add(this, Decision.Z, new Transition(this.location, this.I - 1, this.KQ, 0, location.L_AL));
        td.Add(this, Decision.Z, new Transition(this.location, this.I, this.KQ + 1, 0, location.A_AL));
        td.Add(this, Decision.R, new Transition(this.location, this.I - 1, this.KQ, 1, location.L_ABL));
        td.Add(this, Decision.R, new Transition(this.location, this.I, this.KQ + 1, 1, location.A_ABL));
        td.Add(this, Decision.R, new Transition(this.location, this.I, this.KQ, 2, location.L_ABL));
        return td;
    }

    private TransitionsForDecision DetermineAllTransitions_IgZ_KQG_KEG() {
        TransitionsForDecision td = new TransitionsForDecision();
        td.Add(this, Decision.Z, new Transition(this.location, this.I - 1, this.KQ, this.KE, location.L_ABL));
        td.Add(this, Decision.Z, new Transition(this.location, this.I, this.KQ + 1, this.KE, location.A_ABL));
        td.Add(this, Decision.Z, new Transition(this.location, this.I, this.KQ, this.KE + 1, location.B_ABL));
        return td;
    }

    private TransitionsForDecision DetermineAllTransitions_IR_KQM_KEG() {
        TransitionsForDecision td = new TransitionsForDecision();
        td.Add(this, Decision.Z, new Transition(this.location, this.I - 1, location.KQmax, this.KE, location.L_ABL));
        td.Add(this, Decision.Z, new Transition(this.location, this.I + location.KQmax, 0, this.KE, location.A_ABL));
        td.Add(this, Decision.Z, new Transition(this.location, this.I, location.KQmax, this.KE, location.B_ABL));
        return td;
    }

    private TransitionsForDecision DetermineAllTransitions_IG_KQG_KEM() {
        TransitionsForDecision td = new TransitionsForDecision();
        td.Add(this, Decision.Z, new Transition(this.location, this.I - 1, this.KQ, location.KEmax, location.L_ABL));
        td.Add(this, Decision.Z, new Transition(this.location, this.I, this.KQ + 1, location.KEmax, location.A_ABL));
        td.Add(this, Decision.Z, new Transition(this.location, this.I + 1, this.KQ, 0, location.B_ABL));
        return td;
    }

    private TransitionsForDecision DetermineAllTransitions_IR_KQM_KEM() {
        TransitionsForDecision td = new TransitionsForDecision();
        td.Add(this, Decision.Z, new Transition(this.location, this.I - 1, location.KQmax, location.KEmax, location.L_ABL));
        td.Add(this, Decision.Z, new Transition(this.location, this.I + location.orderQuantity, 0, location.KEmax, location.A_ABL));
        td.Add(this, Decision.Z, new Transition(this.location, this.I + 1, location.KQmax, 0, location.L_ABL));
        return td;
    }

    private TransitionsForDecision DetermineAllTransitions_IR_KQM_KEZ() {
        TransitionsForDecision td = new TransitionsForDecision();
        td.Add(this, Decision.Z, new Transition(this.location, this.I - 1, location.KQmax, 0, location.L_AL));
        td.Add(this, Decision.Z, new Transition(this.location, this.I + location.orderQuantity, 0, 0, location.A_AL));
        td.Add(this, Decision.R, new Transition(this.location, this.I - 1, location.KQmax, 1, location.L_ABL));
        td.Add(this, Decision.R, new Transition(this.location, this.I + location.orderQuantity, 0, 1, location.A_ABL));
        td.Add(this, Decision.R, new Transition(this.location, this.I, location.KQmax, 2, location.B_ABL));
        return td;
    }

    private TransitionsForDecision DetermineAllTransitions_IZ_KQZ_KEM() {
        TransitionsForDecision td = new TransitionsForDecision();
        td.Add(this, Decision.N, new Transition(this.location, 0, 0, location.KEmax, location.L_BL));
        td.Add(this, Decision.N, new Transition(this.location, 1, 0, 0, location.B_BL));
        td.Add(this, Decision.NQ, new Transition(this.location, 0, 1, location.KEmax, location.L_ABL));
        td.Add(this, Decision.NQ, new Transition(this.location, 0, 2, location.KEmax, location.A_ABL));
        td.Add(this, Decision.NQ, new Transition(this.location, 1, 1, 0, location.B_ABL));
        return td;
    }

    private TransitionsForDecision DetermineAllTransitions_IZ_KQM_KEZ() {
        TransitionsForDecision td = new TransitionsForDecision();
        td.Add(this, Decision.N, new Transition(this.location, 0, location.KQmax, 0, location.L_AL));
        td.Add(this, Decision.N, new Transition(this.location, location.orderQuantity, 0, 0, location.A_AL));
        td.Add(this, Decision.NR, new Transition(this.location, 0, location.KQmax, 1, location.L_ABL));
        td.Add(this, Decision.NR, new Transition(this.location, location.orderQuantity, 0, 1, location.A_ABL));
        td.Add(this, Decision.NR, new Transition(this.location, 0, location.KQmax, 2, location.B_ABL));
        return td;
    }

    private TransitionsForDecision DetermineAllTransitions_IgZ_KQZ_KEM() {
        TransitionsForDecision td = new TransitionsForDecision();
        td.Add(this, Decision.Z, new Transition(this.location, this.I - 1, 0, location.KEmax, location.L_BL));
        td.Add(this, Decision.Z, new Transition(this.location, this.I + 1, 0, 0, location.B_BL));
        td.Add(this, Decision.Q, new Transition(this.location, this.I - 1, 1, location.KEmax, location.L_ABL));
        td.Add(this, Decision.Q, new Transition(this.location, this.I, 2, location.KEmax, location.L_ABL));
        td.Add(this, Decision.Q, new Transition(this.location, this.I + 1, 1, 0, location.L_ABL));
        return td;
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

    public TransitionsForDecision NotImplementedState(String notImplemented) {
        this.location.unimplementedStates.add((notImplemented));
        System.out.println(String.format("*** %s Transitions Not Implemented State", this));
        TransitionsForDecision td = new TransitionsForDecision();
        td.Add(this, Decision.Z, new Transition(this.location, this.I, this.KQ, this.KE, 1.0));
        return td;
    }
}
