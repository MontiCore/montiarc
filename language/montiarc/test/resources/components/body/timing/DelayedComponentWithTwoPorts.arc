/* (c) https://github.com/MontiCore/monticore */
package components.body.timing;

/*
 * Valid model.
 * Formerly named 'B'
 */
component DelayedComponentWithTwoPorts {
    
    timing delayed;
    
    port
        in String sIn,
        out String sOut;
}
