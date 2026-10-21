/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis.context.WithinComponentResolver;
import arccompute._cocos.NoInputPortsInInitialCompute;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.NoInputPortInContext4FamilyVisitor;
import variablearc.presence.PresenceResolver;

/**
 * Checks that initial actions do not contain references to input ports in any
 * configuration in which the initial action and the input port are present.
 */
public class NoInputPortsInInitialCompute4Family extends NoInputPortsInInitialCompute {

  public NoInputPortsInInitialCompute4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                             @NotNull PresenceResolver presenceResolver) {
    super(new NoInputPortInContext4FamilyVisitor(withinComponentResolver, presenceResolver, CONTEXT, PortSymbol::isIncoming));
  }
}
