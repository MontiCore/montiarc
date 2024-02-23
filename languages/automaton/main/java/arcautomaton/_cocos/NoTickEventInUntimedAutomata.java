/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._visitor.ArcAutomatonTraverser;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import org.codehaus.commons.nullanalysis.NotNull;

public class NoTickEventInUntimedAutomata implements ArcAutomatonASTArcStatechartCoCo {

  protected NoTickEventVisitor noTickEvent = new NoTickEventVisitor();
  protected NoEventInUntimedVisitor noEvent = new NoEventInUntimedVisitor();

  protected NoTickEventVisitor getNoTickEvent() {
    return this.noTickEvent;
  }

  public void check(@NotNull ASTArcStatechart node) {
    Preconditions.checkNotNull(node);
    if (node.getTiming().matches(Timing.UNTIMED)) {
      ArcAutomatonTraverser traverser = ArcAutomatonMill.traverser();
      traverser.add4SCTransitions4Code(this.noEvent);
      traverser.add4ArcAutomaton(this.getNoTickEvent());
      traverser.traverse(node);
    }
  }
}
