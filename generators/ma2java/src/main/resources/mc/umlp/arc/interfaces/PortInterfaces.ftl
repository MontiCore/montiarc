${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol port", 
"String type", "String name")}

<#if port.isIncoming()>

  public ${glex.getGlobalVar("IInPort")} <${type}> get${name?cap_first}();

<#else>

  public sim.port.IOutPort<${type}> get${name?cap_first}();
  
  public void set${name?cap_first}(${glex.getGlobalVar("IPort")}<${type}> port);

</#if>