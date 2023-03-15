/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._cocos;

import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentBodyCoCo;
import com.google.common.base.Preconditions;
import comfortablearc._ast.ASTArcAutoConnect;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ComfortableArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * Checks that {@link ASTArcAutoConnect} does not appear in atomic components.
 * Atomic components have no subcomponents that could be automatically connected.
 */
public class AtomicNoAutoConnect implements ArcBasisASTComponentBodyCoCo {

  @Override
  public void check(@NotNull ASTComponentBody comp) {
    Preconditions.checkNotNull(comp);

    boolean isAtomic = countComponentInstantiationsIn(comp) == 0;
    Optional<ASTArcAutoConnect> ac = getFirstAutoConnectIn(comp);

    if (isAtomic && ac.isPresent()) {
      Log.error(
        ComfortableArcError.AUTOCONNECT_IN_ATOMIC_COMPONENT.format(),
        ac.get().get_SourcePositionStart(),
        ac.get().get_SourcePositionEnd()
      );
    }
  }

  private long countComponentInstantiationsIn(@NotNull ASTComponentBody comp) {
    long normalInstantiations = comp.streamArcElements()
      .filter(ASTComponentInstantiation.class::isInstance)
      .count();

    // The following counts simultaneous component type declarations directly paired instantiations,
    // e.g.: component MyComp myInstance1, myInstance2 { ... }
    int directInstantiations = comp.streamArcElements()
      .filter(ASTComponentType.class::isInstance)
      .map(ASTComponentType.class::cast)
      .mapToInt(ASTComponentType::sizeComponentInstances)
      .sum();

    return normalInstantiations + directInstantiations;
  }

  /**
   * Gets the first {@link ASTArcAutoConnect} declaration within {@code comp}, or an empty optional if there is none.
   */
  protected Optional<ASTArcAutoConnect> getFirstAutoConnectIn(@NotNull ASTComponentBody comp) {
    return comp.streamArcElements()
      .filter(ASTArcAutoConnect.class::isInstance)
      .map(ASTArcAutoConnect.class::cast)
      .findFirst();
  }
}
