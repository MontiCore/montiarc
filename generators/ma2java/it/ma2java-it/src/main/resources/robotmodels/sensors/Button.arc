package robotmodels.sensors;

/**
 * A conceptual component representing a button with memory. Initial, it 
 * is considered switched off. After being pressed, active becomes true 
 * and holds until the button is pressed again.
 */
component Button {
    port 
        out Boolean active;
}