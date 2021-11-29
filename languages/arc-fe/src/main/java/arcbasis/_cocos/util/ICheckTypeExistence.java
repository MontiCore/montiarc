/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos.util;

import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.codehaus.commons.nullanalysis.NotNull;

public interface ICheckTypeExistence {

  /**
   * Checks whether the symbols to which this astType refers to existent and are resolvable.
   */
  void checkExistenceOf(@NotNull ASTMCType type);
}
