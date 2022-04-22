/* (c) https://github.com/MontiCore/monticore */
package sim.schedhelp;

import java.util.Comparator;

/**
 * Comparator that swaps the natural order of integers.
 */
public class DescendingIntComparator implements Comparator<Integer> {

  /**
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare(Integer o1, Integer o2) {
    int result;
    if (o1.intValue() < o2.intValue()) {
      result = 1;
    } else if (o1.intValue() == o2.intValue()) {
      result = 0;
    } else {
      result = -1;
    }
    return result;
  }
}
