/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable.util;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;

import java.util.Collection;

public final class PortTraversalUtil {

  private PortTraversalUtil(){}

  public static Multimap<String, String> connectionsToMultimap(ASTArcComponentType node){
    return connectionsToMultimap(node.getConnectors());
  }

  public static Multimap<String, String> connectionsToMultimap(Collection<ASTConnector> connectors){
    Multimap<String, String> result = HashMultimap.create();
    for (ASTConnector connection: connectors){
      String source = connection.getSource().getQName();
      for (ASTPortAccess target: connection.getTargetList()){
        result.put(source, target.getQName());
      }
    }
    return result;
  }

  public static Multimap<String, String> subcomponentEffectChainsToMultimap(ASTArcComponentType node){
    return subcomponentEffectChainsToMultimap(node.getSubComponents());
  }

  public static Multimap<String, String> subcomponentEffectChainsToMultimap(Collection<ASTComponentInstance> subComponents){
    Multimap<String, String> result = HashMultimap.create();
    for (ASTComponentInstance subComponent: subComponents){
      SubcomponentSymbol subcomponentSymbol = subComponent.getSymbol();
      if (!subcomponentSymbol.isTypePresent()){
        continue;
      }
      ComponentTypeSymbol component = subcomponentSymbol.getType().getTypeInfo();

      for (PortSymbol key: component.getEffectChains().keySet()){
        String source = subComponent.getName() + "." + key.getName();
        for (PortSymbol value: component.getEffectChains().get(key)){
          result.put(source, subComponent.getName() + "." + value.getName());
        }
      }
    }
    return result;
  }
}
