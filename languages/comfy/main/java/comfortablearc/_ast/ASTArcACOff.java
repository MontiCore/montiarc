/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._ast;

import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import org.codehaus.commons.nullanalysis.NotNull;

public class ASTArcACOff extends ASTArcACOffTOP {

  /**
   * Iff the auto-connect is turned off, then no two ports match.
   */
  @Override
  public boolean matches(@NotNull PortSymbol source, @NotNull PortSymbol target) {
    return false;
  }
}
