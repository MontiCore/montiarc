/* (c) https://github.com/MontiCore/monticore */
/**
 * 
 */
package sim.port;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * {@link Observer} for tests the counts the amount of update(...) calls and remebers the passed arguments.
 *
 *
 */
public class TestObserver implements Observer {
    public int callAmount;
    public List<Object> arguments;
    
    /**
     * 
     */
    public TestObserver() {
        callAmount = 0;
        arguments = new ArrayList<Object>();
    }
    
    /*
     * (non-Javadoc)
     * @see java.util.Observer#update(java.util.Observable,
     * java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        callAmount++;
        arguments.add(arg);
    }
    
}
