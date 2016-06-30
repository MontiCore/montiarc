/**
 * 
 */
package sim.help;

import static org.junit.Assert.assertTrue;

import java.util.Comparator;

import org.junit.Test;

/**
 * Tests for {@link DescendingIntComparator}.
 *
 * <br>
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $Author: ahaber $
 * @version $Date: 2012-11-14 15:43:58 +0100 (Mi, 14 Nov 2012) $<br>
 *          $Revision: 1686 $
 */
public class DescendingIntComparatorTest {
    
    Comparator<Integer> testling = new DescendingIntComparator();
    
    /**
     * Test method for {@link sim.help.DescendingIntComparator#compare(java.lang.Integer, java.lang.Integer)}.
     */
    @Test
    public void testCompare_a_st_b_asInt() {
        assertTrue(testling.compare(10, 20) > 0);
        assertTrue(testling.compare(1, 2) > 0);
    }
 
    /**
     * Test method for {@link sim.help.DescendingIntComparator#compare(java.lang.Integer, java.lang.Integer)}.
     */
    @Test
    public void testCompare_a_st_b_asInteger() {
        assertTrue(testling.compare(Integer.valueOf(10), Integer.valueOf(20)) > 0);
        assertTrue(testling.compare(Integer.valueOf(1), Integer.valueOf(2)) > 0);
        assertTrue(testling.compare(Integer.MIN_VALUE, Integer.MAX_VALUE) > 0);
    }
    
    /**
     * Test method for {@link sim.help.DescendingIntComparator#compare(java.lang.Integer, java.lang.Integer)}.
     */
    @Test
    public void testCompare_a_eq_b_asInt() {
        assertTrue(testling.compare(10, 10) == 0);
    }
 
    /**
     * Test method for {@link sim.help.DescendingIntComparator#compare(java.lang.Integer, java.lang.Integer)}.
     */
    @Test
    public void testCompare_a_eq_b_asInteger() {
        assertTrue(testling.compare(Integer.valueOf(10), Integer.valueOf(10)) == 0);
    }
    
    /**
     * Test method for {@link sim.help.DescendingIntComparator#compare(java.lang.Integer, java.lang.Integer)}.
     */
    @Test
    public void testCompare_a_gt_b_asInt() {
        assertTrue(testling.compare(20, 10) < 0);
        assertTrue(testling.compare(2, 1) < 0);
    }
 
    /**
     * Test method for {@link sim.help.DescendingIntComparator#compare(java.lang.Integer, java.lang.Integer)}.
     */
    @Test
    public void testCompare_a_gt_b_asInteger() {
        assertTrue(testling.compare(Integer.valueOf(20), Integer.valueOf(10)) < 0);
        assertTrue(testling.compare(Integer.valueOf(2), Integer.valueOf(1)) < 0);
        assertTrue(testling.compare(Integer.MAX_VALUE, Integer.MIN_VALUE) < 0);
    }
}

