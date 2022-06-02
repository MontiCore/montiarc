/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import de.monticore.symboltable.serialization.JsonPrinter;
import montiarc._visitor.MontiArcTraverser;
import montiarc._visitor.MontiArcVariableRootOnlyHandler;

public class MontiArcSymbols2Json extends MontiArcSymbols2JsonTOP {
  public MontiArcSymbols2Json() {
    super();
    this.getTraverser().setMontiArcHandler(new MontiArcVariableRootOnlyHandler()); // VariableArc: ignore Symbols in variation points
  }

  public MontiArcSymbols2Json(MontiArcTraverser traverser, JsonPrinter printer) {
    super(traverser, printer);
    traverser.setMontiArcHandler(new MontiArcVariableRootOnlyHandler()); // VariableArc: ignore Symbols in variation points
  }
}
