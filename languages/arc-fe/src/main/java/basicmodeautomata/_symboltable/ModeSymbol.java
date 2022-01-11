/* (c) https://github.com/MontiCore/monticore */
package basicmodeautomata._symboltable;

import arcbasis._ast.ASTComponentBody;
import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.scbasis._symboltable.SCStateSymbol;

public class ModeSymbol extends SCStateSymbol {
  
  protected ASTComponentBody body;
  
  public ModeSymbol(String name) {
    super(name);
  }

  public void setBody(ASTComponentBody body) {
    this.body = body;
  }

  /**
   * @return the dynamic elements that are active in this mode, additionally to the static ones
   */
  public ASTComponentBody getBody() {
    return body;
  }
}
