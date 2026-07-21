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

public final class PortTraversalUtil {

  private PortTraversalUtil(){}

  public static Multimap<String, String> connectionsToMultimap(ASTArcComponentType node){
    Multimap<String, String> result = HashMultimap.create();
    for (ASTConnector connection: node.getConnectors()){
      String source = connection.getSource().getQName();
      for (ASTPortAccess target: connection.getTargetList()){
        result.put(source, target.getQName());
      }
    }
    return result;
  }

  public static Multimap<String, String> subcomponentEffectChainsToMultimap(ASTArcComponentType node){
    Multimap<String, String> result = HashMultimap.create();
    for (ASTComponentInstance subComponent: node.getSubComponents()){
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
