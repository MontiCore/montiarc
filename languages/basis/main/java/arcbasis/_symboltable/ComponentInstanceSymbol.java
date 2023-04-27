/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis._ast.ASTArcArgument;
import arcbasis.check.CompTypeExpression;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.*;

public class ComponentInstanceSymbol extends ComponentInstanceSymbolTOP {

  protected CompTypeExpression type;

  /**
   * @param name the name of this component.
   */
  public ComponentInstanceSymbol(String name) {
    super(name);
  }

  /**
   * @return the type of this component
   */
  public CompTypeExpression getType() {
    Preconditions.checkState(this.type != null);
    return this.type;
  }

  /**
   * @return whether the type for this component instance has already been set.
   */
  public boolean isPresentType() {
    return this.type != null;
  }

  /**
   * @param type the type of this component
   */
  public void setType(CompTypeExpression type) {
    this.type = type;
  }

}
