/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;


import com.google.common.base.Preconditions;
import de.monticore.scbasis._symboltable.ISCBasisScope;
import de.monticore.symbols.basicsymbols._visitor.BasicSymbolsTraverser;

import java.util.Optional;

public class MontiArcScope extends MontiArcScopeTOP {

  ///////////////////////////////////////////////
  // messing with statecharts
  ///////////////////////////////////////////////

  ISCBasisScope statechart = null;

  public Optional<ISCBasisScope> getStatechartScope(){
    return Optional.ofNullable(statechart);
  }

  public void addSubScope(ISCBasisScope subScope) {
    Preconditions.checkNotNull(subScope);
    Preconditions.checkState(statechart == null || statechart.equals(subScope), "currently one statechart per component at max");
    statechart = subScope;
    // recursion?: statechart.setEnclosingScope(this);
  }

  @Override
  public void removeSubScope(ISCBasisScope subScope) {
    statechart = statechart.equals(subScope) ? null:statechart;
  }

  ///////////////////////////////////////////////
  // redirecting statechart visitors
  ///////////////////////////////////////////////

  public  void accept (de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsTraverser visitor)  {
    Optional.ofNullable(statechart).ifPresent(z -> z.accept((BasicSymbolsTraverser) visitor));
  }

  ///////////////////////////////////////////////
  // pesky constructors
  ///////////////////////////////////////////////

  public MontiArcScope(){
    super();
  }

  public MontiArcScope(boolean shadowing){
    super(shadowing);
  }

  public MontiArcScope(IMontiArcScope enclosingScope){
    super(enclosingScope);
  }

  public MontiArcScope(IMontiArcScope enclosingScope, boolean shadowing)  {
    super(enclosingScope, shadowing);
  }
}