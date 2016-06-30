${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol port", 
"String type", "String name")}

<#if port.isIncoming()>

  public ${glex.getGlobalValue("IInPort")} <${type}> get${name?cap_first}();

<#else>

  public sim.port.IOutPort<${type}> get${name?cap_first}();
  
  public void set${name?cap_first}(${glex.getGlobalValue("IPort")}<${type}> port);

</#if>