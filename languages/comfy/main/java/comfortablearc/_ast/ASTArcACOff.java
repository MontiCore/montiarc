/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._ast;

import arcbasis._symboltable.PortSymbol;
import de.monticore.types.check.ITypeRelations;
import org.codehaus.commons.nullanalysis.NotNull;

public class ASTArcACOff extends ASTArcACOffTOP {

  /**
   * Iff the auto-connect is turned off, then no two ports match.
   */
  @Override
  public boolean matches(@NotNull PortSymbol source, @NotNull PortSymbol target, @NotNull ITypeRelations tr) {
    return false;
  }
}