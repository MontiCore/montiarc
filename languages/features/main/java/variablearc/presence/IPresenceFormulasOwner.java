/* (c) https://github.com/MontiCore/monticore */
package variablearc.presence;

import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * A component type symbol that stores its {@link PresenceFormulas}.
 */
public interface IPresenceFormulasOwner {

  Optional<PresenceFormulas> getPresenceFormulas();

  void setPresenceFormulas(@NotNull PresenceFormulas formulas);
}
