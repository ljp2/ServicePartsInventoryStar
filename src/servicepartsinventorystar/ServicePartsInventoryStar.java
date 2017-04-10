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
public class ServicePartsInventoryStar {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Location loc = new Location("testloc", 3, 2, 6, 3, 3, .05, .03, .07);
        
        loc.SetState(0, 0, 0);
        
        System.out.println("Unimplemented states = " + loc.unimplementedStates);
    }
    
}
