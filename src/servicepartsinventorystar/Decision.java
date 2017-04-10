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
public enum Decision {
    Z, // no decision taken
    Q, // regular replenishment initiated for delivery to location j of size Qj
    R, // reserve part restoration from the location j neighborhood of locations
    N, // part demand from zip code z, with a primary inventory location j, is satisfied from a neighborhood of locations associated with zip code j
    NQ, // decisions Q and N are both taken
    NR, // decisions Q and R are both taken
    NQR,// decisions Q, N, and R are all taken
    QR, // decisions Q and R are both taken
}
