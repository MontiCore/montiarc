/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcautomaton._cocos.NoNonSyncInputPortInDoAction;
import arcbasis.context.WithinComponentResolver;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.NoInputPortInContext4FamilyVisitor;
import variablearc.presence.PresenceResolver;

import static de.monticore.symbols.compsymbols._symboltable.Timing.TIMED_SYNC;

/**
 * Checks that do actions do not contain references to non-synchronous input
 * ports in any configuration in which the do action and the port are present.
 */
public class NoNonSyncInputPortInDoAction4Family extends NoNonSyncInputPortInDoAction {

  public NoNonSyncInputPortInDoAction4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                             @NotNull PresenceResolver presenceResolver) {
    super(new NoInputPortInContext4FamilyVisitor(withinComponentResolver, presenceResolver, CONTEXT,
      port -> port.isIncoming() && !port.getTiming().matches(TIMED_SYNC)));
  }
}
