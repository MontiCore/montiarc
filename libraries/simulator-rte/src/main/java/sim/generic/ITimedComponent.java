/* (c) https://github.com/MontiCore/monticore */
/**
 * 
 */
package sim.generic;

/**
 * A timed component.
 *
 *
 */
public interface ITimedComponent extends IComponent {
    
    /**
     * @return the local time from the component
     */
    int getLocalTime();
}
