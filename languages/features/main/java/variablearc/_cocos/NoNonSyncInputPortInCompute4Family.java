/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis.context.WithinComponentResolver;
import arccompute._cocos.NoNonSyncInputPortInCompute;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.NoInputPortInContext4FamilyVisitor;
import variablearc.presence.PresenceResolver;

import static de.monticore.symbols.compsymbols._symboltable.Timing.TIMED_SYNC;

/**
 * Checks that compute blocks do not contain references to non-synchronous
 * input ports in any configuration in which the compute block and the port
 * are present.
 */
public class NoNonSyncInputPortInCompute4Family extends NoNonSyncInputPortInCompute {

  public NoNonSyncInputPortInCompute4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                            @NotNull PresenceResolver presenceResolver) {
    super(new NoInputPortInContext4FamilyVisitor(withinComponentResolver, presenceResolver, CONTEXT,
      port -> port.isIncoming() && !port.getTiming().matches(TIMED_SYNC)));
  }
}
