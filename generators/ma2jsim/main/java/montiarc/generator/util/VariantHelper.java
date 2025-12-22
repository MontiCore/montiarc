/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.util;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTConnector;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._ast.ASTComponentType;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.ISymbol;
import montiarc.MontiArcMill;
import montiarc._symboltable.MontiArcComponentTypeSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTVariantArcComponentType;
import variablearc._symboltable.ArcFeatureSymbol;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc._symboltable.VariantArcComponentTypeSymbol;
import variablearc._symboltable.VariantPortSymbol;
import variablearc._symboltable.VariantSubcomponentSymbol;
import variablearc.evaluation.expressions.Expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class VariantHelper {

  public List<ASTConnector> getVariationPointConnectors(@NotNull VariableArcVariationPoint vp) {
    return vp.getArcElements().stream().filter(e -> MontiArcMill.typeDispatcher().isArcBasisASTConnector(e)).map(e -> MontiArcMill.typeDispatcher().asArcBasisASTConnector(e)).collect(Collectors.toList());
  }

  public List<VariableArcVariantComponentTypeSymbol> getVariants(@NotNull ASTArcComponentType ast) {
    if (ast.getSymbol() instanceof MontiArcComponentTypeSymbol) {
      return ((MontiArcComponentTypeSymbol) ast.getSymbol()).getVariableArcVariants();
    }
    return Collections.emptyList();
  }

  public Map<ArcFeatureSymbol, Boolean> getFeaturesMappedToBool(VariantSubcomponentSymbol subcomponent) {
    return ((VariableArcVariantComponentTypeSymbol) subcomponent.getType().getTypeInfo()).getFeatureSymbolBooleanMap();
  }

  public String variantSuffix(VariableArcVariantComponentTypeSymbol variant) {
    if (variant.getTypeSymbol().getVariableArcVariants().size() == 1) return "";
    return Integer.toString(variant.hashCode());
  }

  public String subcomponentVariantSuffix(ASTArcComponentType comp, SubcomponentSymbol subcomponent) {
    if (subcomponent instanceof VariantSubcomponentSymbol) {
      subcomponent = ((VariantSubcomponentSymbol) subcomponent).getOriginal();
    }
    if (comp instanceof ASTVariantArcComponentType) {
      comp = ((ASTVariantArcComponentType) comp).getOriginal();
    }
    List<SubcomponentSymbol> subs = ISymbol.sortSymbolsByPosition(comp.getSpannedScope().resolveSubcomponentMany(subcomponent.getName()));
    return subs.size() <= 1 ? "" : Integer.toString(subs.indexOf(subcomponent));
  }

  public String portVariantSuffix(ASTComponentType comp, PortSymbol port) {
    if (port instanceof VariantPortSymbol) {
      port = ((VariantPortSymbol) port).getOriginal();
    }
    if (comp instanceof ASTVariantArcComponentType) {
      comp = ((ASTVariantArcComponentType) comp).getOriginal();
    }
    List<PortSymbol> ports = ISymbol.sortSymbolsByPosition(comp.getSpannedScope().resolvePortMany(port.getName()));
    return ports.size() <= 1 ? "" : Integer.toString(ports.indexOf(port));
  }

  public String portVariantSuffix(SubcomponentSymbol sub, PortSymbol port) {
    if (port instanceof VariantPortSymbol) {
      port = ((VariantPortSymbol) port).getOriginal();
    }
    if (sub instanceof VariantSubcomponentSymbol) {
      sub = ((VariantSubcomponentSymbol) sub).getOriginal();
    }
    List<PortSymbol> ports = ISymbol.sortSymbolsByPosition(sub.getType().getTypeInfo().getSpannedScope().resolvePortMany(port.getName()));
    return ports.size() <= 1 ? "" : Integer.toString(ports.indexOf(port));
  }

  public String fieldVariantSuffix(ASTArcComponentType comp, VariableSymbol field) {
    if (comp instanceof ASTVariantArcComponentType) {
      comp = ((ASTVariantArcComponentType) comp).getOriginal();
    }
    List<VariableSymbol> fields = ISymbol.sortSymbolsByPosition(comp.getSpannedScope().resolveVariableMany(field.getName()));
    return fields.size() <= 1 ? "" : Integer.toString(fields.indexOf(field));
  }

  public List<VariableArcVariantComponentTypeSymbol> getVariantsWithPort(ASTArcComponentType comp, PortSymbol port) {
    List<VariableArcVariantComponentTypeSymbol> variants = getVariants(comp);
    List<VariableArcVariantComponentTypeSymbol> varsWithPort = new ArrayList<>(variants.size());

    for (VariableArcVariantComponentTypeSymbol variant : variants) {
      Collection<PortSymbol> allVariantPorts = new HashSet<>();
      allVariantPorts.addAll(variant.getAllIncomingPorts());
      allVariantPorts.addAll(variant.getAllOutgoingPorts());

      for (PortSymbol variantPort : allVariantPorts) {
        if (variantPort == port ||
          (variantPort instanceof VariantPortSymbol && ((VariantPortSymbol) variantPort).getOriginal() == port)
        ) {
          varsWithPort.add(variant);
        }
      }
    }

    return varsWithPort;
  }

  public List<VariableArcVariantComponentTypeSymbol> getVariantsWithSubcomponent(ASTArcComponentType comp, SubcomponentSymbol sub) {
    List<VariableArcVariantComponentTypeSymbol> variants = getVariants(comp);
    List<VariableArcVariantComponentTypeSymbol> varsWithSub = new ArrayList<>(variants.size());

    for (VariableArcVariantComponentTypeSymbol variant : variants) {
      for (SubcomponentSymbol variantSub : variant.getSubcomponents()) {
        if (variantSub == sub ||
          (variantSub instanceof VariantSubcomponentSymbol && ((VariantSubcomponentSymbol) variantSub).getOriginal() == sub)
        ) {
          varsWithSub.add(variant);
        }
      }
    }

    return varsWithSub;
  }

  public Map<PortSymbol, String> getInPortsWithSuffixesOfOtherVariants(VariantArcComponentTypeSymbol variantCompSym) {
    return getPortsWithSuffixesOfOtherVariants(variantCompSym).entrySet().stream()
      .filter(p -> p.getKey().isIncoming())
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public Map<PortSymbol, String> getPortsWithSuffixesOfOtherVariants(VariantArcComponentTypeSymbol variantCompSymbol) {
    List<PortSymbol> ownOriginalPorts = variantCompSymbol.getAllPorts().stream().map(p -> ((VariantPortSymbol) p).getOriginal()).collect(Collectors.toList());
    ComponentTypeSymbol original = variantCompSymbol.getAdaptee();

    return original.getAllPorts().stream()
      .filter(p -> !ownOriginalPorts.contains(p))
      .map(p -> Map.entry(p, portVariantSuffix(variantCompSymbol.getAstNode(), p)))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public List<Expression> getExistenceCondition(@NotNull ASTArcComponentType ast, @NotNull ISymbol symbol) {
    if (ast.getSymbol() instanceof IVariableArcComponentTypeSymbol) {
      return ((IVariableArcComponentTypeSymbol) ast.getSymbol()).getAllVariationPoints()
        .stream()
        .filter(vp -> vp.containsSymbol(symbol))
        .findAny()
        .map(VariableArcVariationPoint::getAllConditions)
        .orElseGet(Collections::emptyList);
    }
    return Collections.emptyList();
  }
}
