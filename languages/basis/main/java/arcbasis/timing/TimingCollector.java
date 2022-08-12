/* (c) https://github.com/MontiCore/monticore */
package arcbasis.timing;

import arcbasis.ArcBasisMill;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.collect.HashBiMap;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.monticore.umlstereotype._visitor.UMLStereotypeVisitor2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A visitor responsible for collecting and mapping stereotypes to timings
 */
public class TimingCollector implements UMLStereotypeVisitor2 {

  private final List<Timing> result;
  protected HashBiMap<String, Timing> availableTimings = HashBiMap.create();

  public TimingCollector() {
    this("untimed", "instant", "delayed", "sync", "causalsync");
  }

  public TimingCollector(String... timings) {
    for (String name : timings) {
      availableTimings.put(name, new Timing(name));
    }
    result = new ArrayList<>();
  }

  /**
   * Method for getting the timings of a stereotype
   *
   * @param node the stereotype we want to start with
   * @return List of timings specified in the stereotype
   */
  public static List<Timing> getTimings(ASTStereotype node) {
    TimingCollector collector = new TimingCollector();
    ArcBasisTraverser traverser = ArcBasisMill.traverser();
    traverser.add4UMLStereotype(collector);
    node.accept(traverser);
    return collector.getTimings();
  }

  public List<Timing> getTimings() {
    return result;
  }

  public Set<Timing> getAvailableTimings() {
    return availableTimings.values();
  }

  @Override
  public void visit(ASTStereoValue node) {
    if (availableTimings.containsKey(node.getName())) {
      result.add(availableTimings.get(node.getName()));
    }
  }
}
