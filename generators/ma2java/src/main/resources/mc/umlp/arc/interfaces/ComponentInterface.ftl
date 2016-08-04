${tc.params("String _package", "String name", "java.util.List<de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol> ports", "String superInterface", "String portInterfaces","boolean needsTimingPort", "String formalTypeArgs","de.montiarc.generator.codegen.GeneratorHelper helper", "String comment")}
package ${_package};


/**
 * ${comment}
 * <br><br>
 * Java interface of component ${name}.<br>
 * <br>
 * Generated with MontiArc <#-- ${MontiArcVersion}-->.<br>
 * @date <#--${TIME_NOW}--><br>
 *
 */
 
public interface ${name}${formalTypeArgs} extends ${superInterface} ${portInterfaces} {

<#list ports as port>
  ${_templates.mc.umlp.arc.interfaces.PortInterfaces.generate(port, helper.printType(port.getTypeReference()), port.getName())}
</#list>

<#if needsTimingPort>
${_templates.mc.umlp.arc.interfaces.PortTimeInInterface.generate()}
</#if>

}
