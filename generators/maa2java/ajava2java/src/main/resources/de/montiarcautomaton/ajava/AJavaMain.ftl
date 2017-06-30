${tc.params("de.montiarcautomaton.ajava.generator.helper.AJavaHelper helper", "String _package", "java.util.Collection<de.monticore.symboltable.ImportStatement> imports",
"String name", "String resultName", "String inputName", "String implName",
"java.util.Collection<de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol> portsIn",
"java.util.Collection<de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol> portsOut",
"java.util.Collection<de.monticore.symboltable.types.JFieldSymbol> configParams",
"java.util.Collection<de.monticore.lang.montiarc.montiarc._symboltable.ComponentVariableSymbol> compVariables",
"String ajava", "java.util.List<de.monticore.lang.montiarc.ajava._ast.ASTVariableInitialization> initializations")}
package ${_package};

import ${_package}.${resultName};
import ${_package}.${inputName};
<#list imports as import>
import ${import.getStatement()}<#if import.isStar()>.*</#if>;
</#list>

import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;

public class ${implName} implements IComputable<${inputName}, ${resultName}> {
  
  //component variables
  <#list compVariables as compVariable>
    private ${helper.printVariableTypeName(compVariable)} ${compVariable.getName()};
  </#list>
  
  // config parameters
  <#list configParams as param>
  private final ${helper.getParamTypeName(param)} ${param.getName()};
  </#list>
  
  public ${implName}(<#list configParams as param>${helper.getParamTypeName(param)} ${param.getName()}<#sep>, </#list>) {
    <#list configParams as param>
    this.${param.getName()} = ${param.getName()};
    </#list>
  }   

  @Override
  public ${resultName} getInitialValues() {
    final ${resultName} result = new ${resultName}();
    
    try {
    <#list portsOut as portOut>
    ${helper.getPortTypeName(portOut)} ${portOut.getName()};
    </#list>
        
    <#list initializations as init>
      ${helper.printInit(init)}
    </#list>

    <#list portsOut as portOut>
    result.set${portOut.getName()?cap_first}(${portOut.getName()});
    </#list>
    } catch(Exception e) {
      e.printStackTrace();
    }

    return result;
  }

  @Override
  public ${resultName} compute(${inputName} input) {
    // inputs
    <#list portsIn as portIn>
  	final ${helper.getPortTypeName(portIn)} ${portIn.getName()} = input.get${portIn.getName()?cap_first}();
  	</#list>
  
    final ${resultName} result = new ${resultName}();
    
    <#list portsOut as portOut>
    ${helper.getPortTypeName(portOut)} ${portOut.getName()} = result.get${portOut.getName()?cap_first}();
    </#list>
    
    <#-- print java statements here -->
    ${ajava}
    
    <#-- always add all outgoing values to result -->
    <#list portsOut as portOut>
    result.set${portOut.getName()?cap_first}(${portOut.getName()});
    </#list>
    
    
    return result;
  }
  
}
