/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTPort;
import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import montiarc.util.NameCapitalizationHelper;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * {@code implements} [RRW14a] C2: The names of [...] and ports start with
 * lower-case letters. (p. 31, Lst. 6.5)
 */
public class PortNameCapitalisation implements ArcBasisASTPortCoCo {

  @Override
  public void check(@NotNull ASTPort port) {
    Preconditions.checkNotNull(port);
    if(NameCapitalizationHelper.isNotLowerCase(port.getName())) {
      NameCapitalizationHelper.warning(ArcError.PORT_LOWER_CASE, port, port.getName(),
        port.getEnclosingScope().getSpanningSymbol().getName());
    }
  }
}