/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcautomaton._cocos.NoNonSyncInputPortInEpsilonTransition;
import arcbasis.context.WithinComponentResolver;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.NoInputPortInContext4FamilyVisitor;
import variablearc.presence.PresenceResolver;

import static de.monticore.symbols.compsymbols._symboltable.Timing.TIMED_SYNC;

/**
 * Checks that transitions triggered by tick events do not contain references
 * to non-synchronous input ports in any configuration in which the transition
 * and the port are present.
 */
public class NoNonSyncInputPortInEpsilonTransition4Family extends NoNonSyncInputPortInEpsilonTransition {

  public NoNonSyncInputPortInEpsilonTransition4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                                      @NotNull PresenceResolver presenceResolver) {
    super(new NoInputPortInContext4FamilyVisitor(withinComponentResolver, presenceResolver, CONTEXT,
      port -> port.isIncoming() && !port.getTiming().matches(TIMED_SYNC)));
  }
}
