/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._ast;

import de.monticore.scevents._symboltable.SCEventDefSymbol;

public class ASTMsgEvent extends ASTMsgEventTOP {
  
  protected SCEventDefSymbol eventSymbol;
  
  public SCEventDefSymbol getEventSymbol() {
    return eventSymbol;
  }
  
  public void setEventSymbol(SCEventDefSymbol eventSymbol) {
    this.eventSymbol = eventSymbol;
  }
}
