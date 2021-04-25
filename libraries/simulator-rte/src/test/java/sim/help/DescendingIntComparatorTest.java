/* (c) https://github.com/MontiCore/monticore */
/**
 * 
 */
package sim.help;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Comparator;

/**
 * Tests for {@link DescendingIntComparator}.
 *
 *
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

