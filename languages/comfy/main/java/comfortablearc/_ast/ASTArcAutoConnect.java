/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._ast;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import com.google.common.base.Preconditions;
import comfortablearc.ComfortableArcMill;
import org.codehaus.commons.nullanalysis.NotNull;

public class ASTArcAutoConnect extends ASTArcAutoConnectTOP {

  @Override
  public void setArcACMode(@NotNull ASTArcACMode mode) {
    Preconditions.checkNotNull(mode);
    super.setArcACMode(mode);
  }

  /**
   * Creates a connector from a copy of {@code source} to a copy of {@code target}.
   *
   * @param source The source to copy for the newly created connector
   * @param target The target to copy for the newly created connector
   */
  public ASTConnector connectPorts(@NotNull ASTPortAccess source,
                                   @NotNull ASTPortAccess target) {
    Preconditions.checkNotNull(source);
    Preconditions.checkNotNull(target);


    ASTPortAccess newSource = preparedClone(source);
    ASTPortAccess newTarget = preparedClone(target);

    ASTConnector connector = ComfortableArcMill.connectorBuilder()
      .setSource(newSource)
      .addTarget(newTarget)
      .set_SourcePositionStart(this.get_SourcePositionStart())
      .set_SourcePositionEnd(this.get_SourcePositionEnd())
      .build();

    connector.setEnclosingScope(this.getEnclosingScope());

    return connector;
  }

  protected ASTPortAccess preparedClone(@NotNull ASTPortAccess orig) {
    Preconditions.checkNotNull(orig);
    Preconditions.checkNotNull(enclosingScope);

    ASTPortAccess clone = orig.deepClone();
    clone.setEnclosingScope(this.getEnclosingScope());

    if (orig.isPresentComponentSymbol()) {
      clone.setComponentSymbol(orig.getComponentSymbol());
    }
    if (orig.isPresentPortSymbol()) {
      clone.setPortSymbol(orig.getPortSymbol());
    }

    clone.set_SourcePositionStart(this.get_SourcePositionStart());
    clone.set_SourcePositionEnd(this.get_SourcePositionEnd());

    return clone;
  }
}