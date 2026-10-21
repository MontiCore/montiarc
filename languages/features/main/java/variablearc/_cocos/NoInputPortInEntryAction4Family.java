/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcautomaton._cocos.NoInputPortInEntryAction;
import arcbasis.context.WithinComponentResolver;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.NoInputPortInContext4FamilyVisitor;
import variablearc.presence.PresenceResolver;

/**
 * Checks that entry actions do not contain references to input ports in any
 * configuration in which the entry action and the input port are present.
 */
public class NoInputPortInEntryAction4Family extends NoInputPortInEntryAction {

  public NoInputPortInEntryAction4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                         @NotNull PresenceResolver presenceResolver) {
    super(new NoInputPortInContext4FamilyVisitor(withinComponentResolver, presenceResolver, CONTEXT, PortSymbol::isIncoming));
  }
}
