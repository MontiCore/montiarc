/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._ast;

import arcbasis._symboltable.ArcPortSymbol;
import org.codehaus.commons.nullanalysis.NotNull;

public interface ASTArcACMode extends ASTArcACModeTOP {

  /**
   * Whether the given source and target are matching in this mode, that is,
   * if auto-connect would create a connector from the source to the target
   * given their properties.
   */
  boolean matches(@NotNull ArcPortSymbol source, @NotNull ArcPortSymbol target);

}
