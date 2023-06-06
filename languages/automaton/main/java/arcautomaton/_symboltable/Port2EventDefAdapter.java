/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._symboltable;

import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.scevents._symboltable.SCEventDefSymbol;
import org.codehaus.commons.nullanalysis.NotNull;

public class Port2EventDefAdapter extends SCEventDefSymbol {
  
  protected PortSymbol adaptee;
  
  public Port2EventDefAdapter(@NotNull PortSymbol adaptee) {
    super(Preconditions.checkNotNull(adaptee).getName());
    this.adaptee = adaptee;
  }
  
  public PortSymbol getAdaptee() {
    return adaptee;
  }
  
  @Override
  public String getName() {
    return this.getAdaptee().getName();
  }
  
  @Override
  public String getFullName() {
    return this.getAdaptee().getFullName();
  }
  
  @Override
  public String getPackageName() {
    return this.getAdaptee().getPackageName();
  }
}
