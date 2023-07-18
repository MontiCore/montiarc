/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._ast;

import arcbasis._symboltable.PortSymbol;
import de.monticore.types3.SymTypeRelations;
import org.codehaus.commons.nullanalysis.NotNull;

public interface ASTArcACMode extends ASTArcACModeTOP {

  /**
   * Whether the given source and target are matching in this mode, that is,
   * if auto-connect would create a connector from the source to the target
   * given their properties.
   */
  boolean matches(@NotNull PortSymbol source, @NotNull PortSymbol target, @NotNull SymTypeRelations tr);

}
