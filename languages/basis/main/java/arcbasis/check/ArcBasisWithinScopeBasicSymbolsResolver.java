/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;

public class ArcBasisWithinScopeBasicSymbolsResolver extends WithinScopeBasicSymbolsResolver {

  public ArcBasisWithinScopeBasicSymbolsResolver() {
    super(new ArcBasisTypeContextCalculator(), new WithinTypeBasicSymbolsResolver());
  }
}
