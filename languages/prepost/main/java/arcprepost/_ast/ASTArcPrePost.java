/* (c) https://github.com/MontiCore/monticore */
package arcprepost._ast;

import de.monticore.symbols.compsymbols._symboltable.Timing;

public class ASTArcPrePost extends ASTArcPrePostTOP {

  @Override
  public Timing getTiming() {
    return Timing.DEFAULT;
  }
}
