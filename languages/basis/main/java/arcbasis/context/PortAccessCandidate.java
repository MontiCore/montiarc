/* (c) https://github.com/MontiCore/monticore */
package arcbasis.context;

import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * A port a port access may refer to: a port of the component itself, or a
 * port of one of its subcomponents.
 */
public record PortAccessCandidate(@NotNull Optional<SubcomponentSymbol> subcomponent, @NotNull PortSymbol port) {

  public PortAccessCandidate {
    Preconditions.checkNotNull(subcomponent);
    Preconditions.checkNotNull(port);
  }
}
