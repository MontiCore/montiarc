/* (c) https://github.com/MontiCore/monticore */
package mceffect.effect;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.tagging.ISymbolTagger;
import de.monticore.tagging.tags._ast.ASTTag;
import de.monticore.tagging.tags._ast.ASTValuedTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaggingEffectStorage implements EffectStorage {
  private final ISymbolTagger tags;

  public TaggingEffectStorage(ISymbolTagger tags) {
    this.tags = tags;
  }

  @Override
  public List<Effect> getEffectsOfComponent(ComponentTypeSymbol component) {
    List<Effect> result = new ArrayList<>();
    for (PortSymbol port : component.getAllIncomingPorts()) {
      result.addAll(
          tags.getTags(port).stream()
              .map(tag -> this.createEffect(tag, port, component))
              .filter(Optional::isPresent)
              .map(Optional::get)
              .collect(Collectors.toUnmodifiableList()));
    }
    return Collections.unmodifiableList(result);
  }

  private Optional<Effect> createEffect(ASTTag tag, PortSymbol port, ComponentTypeSymbol component) {
    // ToDo: Implement this!
    assert (false);

    if (tag instanceof ASTValuedTag) {
      ((ASTValuedTag) tag).getName();
    }

    return Optional.empty();
  }
}
