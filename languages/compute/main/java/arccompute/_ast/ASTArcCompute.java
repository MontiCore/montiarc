/* (c) https://github.com/MontiCore/monticore */
package arccompute._ast;

import de.monticore.symbols.compsymbols._symboltable.Timing;

import java.util.Optional;

public class ASTArcCompute extends ASTArcComputeTOP {

  protected Timing timing;

  @Override
  public Timing getTiming() {
    if (this.timing != null) {
      return this.timing;
    } else if (this.isPresentStereotype()) {
      this.timing = this.getStereotype().streamValues()
        .map(v -> Timing.of(v.getName()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst().orElse(Timing.DEFAULT);
      return this.timing;
    } else {
      this.timing = Timing.DEFAULT;
      return this.timing;
    }
  }
}
