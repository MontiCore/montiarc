/* (c) https://github.com/MontiCore/monticore */
package mceffect.effect;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.tagging.ISymbolTagger;
import de.monticore.tagging.tags._ast.ASTTag;
import de.monticore.tagging.tags._ast.ASTValuedTag;
import de.se_rwth.commons.logging.Log;

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
      result.addAll(tags.getTags(port).stream().map(tag -> this.createEffect(tag, port, component)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toUnmodifiableList()));
    }
    return Collections.unmodifiableList(result);
  }

  private Optional<Effect> createEffect(ASTTag tag, PortSymbol port, ComponentTypeSymbol component) {
    Optional<Effect> result = Optional.empty();
    String name = ((ASTValuedTag) tag).getName();
    if (name.endsWith("_effect")) {
      if (tag instanceof ASTValuedTag) {
        String value = ((ASTValuedTag) tag).getValue();
        Optional<PortSymbol> optTargetPort = component.getPort(value);
        if (optTargetPort.isPresent()) {
          PortSymbol targetPort = optTargetPort.get();
          result = Optional.of(new SimpleEffect(component, port, targetPort, isCheck(name), isEnsure(name), getEffectKind(name), tag.get_SourcePositionStart()));
        } else {
          Log.error("0x93b8d Not a valid port: " + value + "\n" + tag);
        }
      }
    } else{
      Log.error("0x762bd Not a valid tag: " + tag);
    }

    return result;
  }

  private EffectKind getEffectKind(String name) {
    if(name.contains("potential")){
      return EffectKind.POTENTIAL;
    } else if (name.contains("mandatory")){
      return EffectKind.MANDATORY;
    } else if (name.contains("no")){
      return EffectKind.NO;
    } else {
      Log.error("0x44d8e Invalid Tag-name for effects: " + name);
      return null;
    }
  }

  private boolean isEnsure(String name) {
    return !isCheck(name);
  }

  private boolean isCheck(String name) {
    return name.startsWith("check");
  }
}
