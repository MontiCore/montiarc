<#-- sig:
Does something
@params de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol symbol, String componentPackage, String componentComments, de.montiarc.generator.codegen.GeneratorHelper helper
@result de.monticore.lang.montiarc.montiarc._ast.ASTComponent  
-->

${tc.signature("symbol", "componentPackage", "componentComments", "helper")}

<#assign genHelper = helper>

<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

package ${componentPackage};

/**
 * ${componentComments}
 * <br><br>
 * Java representation of component ${symbol.getFullName()}.<br>
 * <br>
 * Generated with MontiArc ${genHelper.getMontiArcVersion()}.<br>
 * @date ${genHelper.getTimeNow()}<br>
 *
 */
public class ${symbol.getName()} <#t>
{
  // TODO
  <#list symbol.getPorts() as port>
    <#assign portNameCap=port.getName()?cap_first>
    private ${helper.printType(port.getTypeReference())} ${port.getName()};
    
    public ${helper.printType(port.getTypeReference())} get${portNameCap}() {
        return ${port.getName()};
    }

    <#if port.isOutgoing() >
      /**
       * Is used to send messages through the outgoing port 
       * ${port.getName()}.
       */
      protected void send${portNameCap}(final ${helper.printType(port.getTypeReference())} message) {
        this.get${portNameCap}();
      }
    </#if>
  </#list>

}
