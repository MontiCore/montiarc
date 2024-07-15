/* (c) https://github.com/MontiCore/monticore */
package mceffect.graph;

import java.util.Optional;

import mceffect.effect.Effect;
import mceffect.effect.EffectKind;
import org.jgrapht.graph.DefaultEdge;

public class EffectEdge extends DefaultEdge {
  protected Optional<Effect> effect;

  public EffectEdge(Effect effect) {
    this.effect = Optional.of(effect);
  }

  public EffectEdge() {
    this.effect = Optional.empty();
  }

  @Override
  public EffectNode getSource() {
    return (EffectNode) super.getSource();
  }

  @Override
  public EffectNode getTarget() {
    return (EffectNode) super.getTarget();
  }

  public boolean isConnectorEdge() {
    return effect.isEmpty();
  }

  public boolean hasNoEffect() {
    return effect.isPresent() && effect.get().getEffectKind() == EffectKind.NO;
  }

  public boolean hasMandatoryEffect() {
    return effect.isPresent() && effect.get().getEffectKind() == EffectKind.MANDATORY;
  }

  public boolean hasPotentialEffect() {
    return effect.isPresent() && effect.get().getEffectKind() == EffectKind.POTENTIAL;
  }

  public boolean isEffectEdge() {
    return effect.isPresent();
  }

  public String toString() {
    return effect.isPresent() ? effect.get().getEffectKind().toString() : "Connector";
  }
}
