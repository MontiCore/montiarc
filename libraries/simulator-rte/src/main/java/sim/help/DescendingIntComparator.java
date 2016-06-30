/**
 * 
 */
package sim.help;

import java.util.Comparator;

/**
 * Comparator that swaps the natural order of integers. 
 * 
 * <br>
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 * 
 * @author (last commit) $Author: ahaber $
 * @version $Date: 2012-11-14 15:43:58 +0100 (Mi, 14 Nov 2012) $<br>
 *          $Revision: 1686 $
 */
public class DescendingIntComparator implements Comparator<Integer> {
    
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Integer o1, Integer o2) {
        int result;
        if (o1.intValue() < o2.intValue()) {
            result = 1;
        }
        else if (o1.intValue() == o2.intValue()) {
            result = 0;
        }
        else {
            result = -1;
        }
        return result;
    }
    
}
