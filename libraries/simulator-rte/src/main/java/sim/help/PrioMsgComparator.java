/**
 * 
 */
package sim.help;

import java.util.Comparator;

import sim.generic.Message;
import sim.generic.PrioMessage;

/**
 * Compares {@link PrioMessage}s according to their priority.
 *
 * <br>
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $Author: ahaber $
 * @version $Date: 2013-04-09 16:37:35 +0200 (Di, 09 Apr 2013) $<br>
 *          $Revision: 2169 $
 */
public class PrioMsgComparator implements Comparator<Message<?>> {
    
    /**
     * Inner comparator. 
     */
    private final DescendingIntComparator intComp = new DescendingIntComparator();
    
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Message<?> msg1, Message<?> msg2) {
        int prio1, prio2;
        if (msg1 instanceof PrioMessage) {
            prio1 = ((PrioMessage<?>) msg1).getPriority();
        }
        else {
            prio1 = PrioMessage.DEFAULT_PRIORITY;
        }
        if (msg2 instanceof PrioMessage) {
            prio2 = ((PrioMessage<?>) msg2).getPriority();
        }
        else {
            prio2 = PrioMessage.DEFAULT_PRIORITY;
        }
        return intComp.compare(prio1, prio2);
    }
    
}
