/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import java.util.ArrayList;
import java.util.Collection;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.monticore.umlcd4a.CD4AnalysisModelLoader;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDMethodSymbol;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import montiarc.helper.CD4ALanguage;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class MontiArcCD4ALanguage extends CD4ALanguage {
  
  Collection<ResolvingFilter<? extends Symbol>> resolvingFilters;
  
  @Override
  protected CD4AnalysisModelLoader provideModelLoader() {
    resolvingFilters = new ArrayList<>();
    resolvingFilters.add(CommonResolvingFilter.create(CDSymbol.KIND));
    resolvingFilters.add(CommonResolvingFilter.create(CDTypeSymbol.KIND));
    resolvingFilters.add(CommonResolvingFilter.create(CDFieldSymbol.KIND));
    resolvingFilters.add(CommonResolvingFilter.create(CDMethodSymbol.KIND));
    resolvingFilters.add(CommonResolvingFilter.create(CDAssociationSymbol.KIND));

    return new MontiArcCD4AModelLoader(this);
  }
  
  /**
   * @see de.monticore.CommonModelingLanguage#getResolvingFilters()
   */
  @Override
  public Collection<ResolvingFilter<? extends Symbol>> getResolvingFilters() {
    return resolvingFilters;
  }
}
