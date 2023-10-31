/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._visitor.ArcAutomatonTraverser;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import org.codehaus.commons.nullanalysis.NotNull;

public class NoEventsInSyncAutomata implements ArcAutomatonASTArcStatechartCoCo {

  public void check(@NotNull ASTArcStatechart node) {
    if (node.getTiming().matches(Timing.TIMED_SYNC)) {
      Preconditions.checkNotNull(node);
      ArcAutomatonTraverser traverser = ArcAutomatonMill.traverser();
      traverser.add4SCTransitions4Code(new NoEventVisitor());
      traverser.traverse(node);
    }
  }
}