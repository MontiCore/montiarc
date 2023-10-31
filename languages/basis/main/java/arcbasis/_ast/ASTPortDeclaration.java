/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.symbols.compsymbols._symboltable.Timing;

import java.util.Optional;

/**
 * Represents a port declaration. Extends {@link ASTPortDeclarationTOP} with utility functionality
 * for easy access.
 */
public class ASTPortDeclaration extends ASTPortDeclarationTOP {

  public static final String DELAY = "delayed";

  protected ASTPortDeclaration() {
    super();
  }

  public boolean isIncoming() {
    return this.getPortDirection().isIn();
  }

  public boolean isOutgoing() {
    return this.getPortDirection().isOut();
  }

  public Optional<Timing> getTiming() {
    if (this.isPresentStereotype()) {
      for (ASTStereoValue v : this.getStereotype().getValuesList()) {
        if (Timing.contains(v.getName())) return Timing.of(v.getName());
      }
    }
    return Optional.empty();
  }

  public boolean hasDelay() {
    return this.isPresentStereotype() && this.getStereotype().streamValues()
      .anyMatch(v -> v.getName().equals(ASTPortDeclaration.DELAY));
  }
}