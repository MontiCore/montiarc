/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symboltable.serialization.JsonPrinter;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.VariableArcRootOnlyHandler;
import variablearc._visitor.VariableArcTraverser;

public class VariableArcSymbols2Json extends VariableArcSymbols2JsonTOP {

  public VariableArcSymbols2Json() {
    super();
    this.getTraverser().setVariableArcHandler(new VariableArcRootOnlyHandler()); // ignore Symbols in variation points
  }

  public VariableArcSymbols2Json(@NotNull VariableArcTraverser traverser, @NotNull JsonPrinter printer) {
    super(Preconditions.checkNotNull(traverser), Preconditions.checkNotNull(printer));
    traverser.setVariableArcHandler(new VariableArcRootOnlyHandler()); // ignore Symbols in variation points
  }
}
