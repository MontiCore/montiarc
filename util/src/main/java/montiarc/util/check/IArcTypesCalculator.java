/* (c) https://github.com/MontiCore/monticore */
package montiarc.util.check;

import de.monticore.types.check.ITypesCalculator;
import de.monticore.types.check.SymTypeExpression;

import java.util.Optional;

public interface IArcTypesCalculator extends ITypesCalculator {

  Optional<SymTypeExpression> getResult();

  void reset();
}