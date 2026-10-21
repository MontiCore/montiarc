/* (c) https://github.com/MontiCore/monticore */
package variablearc.context;

import arcbasis.context.WithinComponentResolver;
import de.monticore.symboltable.ISymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.ArcFeature2VariableAdapter;
import variablearc._symboltable.ArcFeatureSymbol;

public class VariableArcWithinComponentResolver extends WithinComponentResolver {

  @Override
  protected boolean isAdapter(@NotNull ISymbol symbol) {
    return super.isAdapter(symbol) || symbol instanceof ArcFeature2VariableAdapter;
  }

  /**
   * Features are shared with super components. Other members that are
   * redeclared replace the inherited ones only where they are present.
   */
  @Override
  protected boolean hides(@NotNull ISymbol declared, @NotNull ISymbol inherited) {
    return super.hides(declared, inherited)
      && declared instanceof ArcFeatureSymbol && inherited instanceof ArcFeatureSymbol;
  }
}
