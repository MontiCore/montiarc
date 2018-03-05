/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.helper;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import montiarc._symboltable.PortSymbol;

/**
 * Checks type compatibility of connected ports.
 *
 * @author ahaber, Robert Heim
 */
public class PortCompatibilityChecker {
  /**
   * Checks whether the sourcePort's type can be connected to the targetPort's type. For example,
   * consider a generic subcomponent {@code s<Int>} of type {@code S<T>} with a port {@code p} of
   * type {@code T}. If {@code p} is the sourcePort, the sourceFormalTypeParameters list is
   * {@code [T]}, the sourceTypeArguments is {@code [Int]}.<br>
   * <br>
   * This type-check would allow a typed based auto-connection of {@code aOut} with {@code p} in the
   * following example:
   * 
   * <pre>
   * component A {
   *   port out Int aOut;
   *   component S&lt;T&gt; s&lt;Int&gt; {
   *     port out T p;
   *   }
   * }
   * </pre>
   *
   * @param sourcePort the port that outputs data
   * @param sourceFormalTypeParameters the defined formal type parameters of the component that the
   * sourcePort is defined in. They define additional valid types that might be bound by the
   * sourceTypeArguments. This list might be empty.
   * @param sourceTypeArguments Defines the current bindings for the formal type-parameters. This
   * list might be empty.
   * @param targetPort the port that receives data
   * @param targetTypeFormalTypeParameters analog to source, but for the target port.
   * @param targetTypeArguments analog to source, but for the target port.
   * @return
   */
  public static boolean doPortTypesMatch(PortSymbol sourcePort,
      List<JTypeSymbol> sourceFormalTypeParameters,
      List<JTypeReference<? extends JTypeSymbol>> sourceTypeArguments,
      PortSymbol targetPort,
      List<JTypeSymbol> targetTypeFormalTypeParameters,
      List<JTypeReference<? extends JTypeSymbol>> targetTypeArguments) {
    checkNotNull(sourcePort);
    checkNotNull(targetPort);
    return TypeCompatibilityChecker.doTypesMatch(sourcePort.getTypeReference(),
        sourceFormalTypeParameters,
        sourceTypeArguments, targetPort.getTypeReference(), targetTypeFormalTypeParameters,
        targetTypeArguments);
  }
  
}
