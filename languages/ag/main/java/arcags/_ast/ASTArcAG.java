/* (c) https://github.com/MontiCore/monticore */
package arcags._ast;

import de.monticore.symbols.compsymbols._symboltable.Timing;

public class ASTArcAG extends ASTArcAGTOP {

  @Override
  public Timing getTiming() {
    return Timing.DEFAULT;
  }
}
