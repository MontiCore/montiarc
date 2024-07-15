/* (c) https://github.com/MontiCore/monticore */
package mceffect.effect;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.util.*;
import java.util.function.Function;
import mceffect._ast.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class SimpleEffectStorage implements EffectStorage {

  protected final Map<ComponentTypeSymbol, List<Effect>> effectMap = new HashMap<>();
  private final Function<String, Optional<ComponentTypeSymbol>> compResolver;
  private final Function<String, Optional<PortSymbol>> portResolver;

  public SimpleEffectStorage(
      ASTMCEffect ast,
      Function<String, Optional<ComponentTypeSymbol>> componentResolver,
      Function<String, Optional<PortSymbol>> portResolver) {
    this(Set.of(ast), componentResolver, portResolver);
  }

  public SimpleEffectStorage(
      Set<ASTMCEffect> asts,
      Function<String, Optional<ComponentTypeSymbol>> componentResolver,
      Function<String, Optional<PortSymbol>> portResolver) {
    this.compResolver = componentResolver;
    this.portResolver = portResolver;

    for (ASTMCEffect ast : asts) {
      String pName;
      if (ast.isPresentMCPackageDeclaration()) {
        pName = ast.getMCPackageDeclaration().getMCQualifiedName().getQName();
      } else {
        pName = "";
      }

      for (ASTComponentEffect compEffect : ast.getComponentEffectList()) {

        String cName = pName + "." + compEffect.getMCQualifiedName().getQName();
        ComponentTypeSymbol cSymbol = resolveComponent(cName, compEffect.get_SourcePositionStart());

        for (ASTEffectRuleDeclaration decl : compEffect.getEffectRuleDeclarationList()) {
          for (ASTEffectRule rule : decl.getEffectRuleList()) {
            addEffect(cSymbol, buildEffect(rule, decl, cSymbol));
          }
        }
      }
    }

    checkEffectStorage(effectMap);
  }

  private static String printPosition(SourcePosition pos) {
    if (pos.getFileName().isPresent()) {
      String fileName = new File(pos.getFileName().get()).getName();
      return fileName + " <" + pos.getLine() + "," + pos.getColumn() + ">";
    }
    return "";
  }

  @Override
  public List<Effect> getEffectsOfComponent(ComponentTypeSymbol component) {
    return effectMap.containsKey(component) ? effectMap.get(component) : new ArrayList<>();
  }

  /***
   * check if there are multiple effect rules for the same pair port.
   * @param effectMap map containing all the effects
   */
  private void checkEffectStorage(Map<ComponentTypeSymbol, List<Effect>> effectMap) {
    for (List<Effect> compEffects : effectMap.values()) {
      List<Pair<PortSymbol, PortSymbol>> ports = new ArrayList<>();

      for (Effect effect : compEffects) {
        Pair<PortSymbol, PortSymbol> current =
            new ImmutablePair<>(effect.getFrom(), effect.getTo());
        if (ports.contains(current)) {
          Log.error(
              String.format(
                  "Error in  %s and %s multiple effect between the source port %s and the target port"
                      + "%s of the component type %s",
                  printPosition(effect.getFrom().getSourcePosition()),
                  printPosition(effect.getTo().getSourcePosition()),
                  effect.getTo().getName(),
                  effect.getTo().getName(),
                  effect.getComponent().getName()));
          assert false;
        } else {
          ports.add(current);
        }
      }
    }
  }

  public void addEffect(ComponentTypeSymbol symbol, Effect effect) {
    if (effectMap.containsKey(symbol)) {
      effectMap.get(symbol).add(effect);
    } else {
      effectMap.put(symbol, new ArrayList<>(List.of(effect)));
    }
  }

  private Effect buildEffect(
      ASTEffectRule rule, ASTEffectRuleDeclaration ruleDecl, ComponentTypeSymbol cSymbol) {

    String fromPort = cSymbol.getFullName() + "." + rule.getFrom();
    String toPort = cSymbol.getFullName() + "." + rule.getTo();

    return new SimpleEffect(cSymbol,
        resolvePort(fromPort, rule.get_SourcePositionStart()),
        resolvePort(toPort, rule.get_SourcePositionStart()),
        ruleDecl.isIsCheck(),
        ruleDecl.isIsEnsure(),
        getEffectKind(ruleDecl.getEffectKind()),
        rule.get_SourcePositionStart()
    );
  }

  private EffectKind getEffectKind(ASTEffectKind effectKind) {
    switch (effectKind) {
      case NO:
        return EffectKind.NO;
      case POTENTIAL:
        return EffectKind.POTENTIAL;
      case MANDATORY:
        return EffectKind.MANDATORY;
      default:
        return EffectKind.UNKNOWN;
    }
  }

  private ComponentTypeSymbol resolveComponent(String qName, SourcePosition pos) {
    Optional<ComponentTypeSymbol> c = compResolver.apply(qName);
    if (c.isEmpty()) {
      Log.error(
          String.format(
              "At position %s component \"%s\" not found. Check the package name.",
              printPosition(pos), qName));
      assert false;
    }
    return c.get();
  }

  private PortSymbol resolvePort(String qName, SourcePosition pos) {
    Optional<PortSymbol> c = portResolver.apply(qName);
    if (c.isEmpty()) {
      Log.error(
          String.format(
              "At position %s port \"%s\" not found.Check the package name.",
              printPosition(pos), qName));
      assert false;
    }
    return c.get();
  }
}
