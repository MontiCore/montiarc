<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/util/Utils.ftl" as Utils>
<#import "/templates/component/Subcomponents.ftl" as Subcomponents>
<#import "/templates/component/Ports.ftl" as Ports>
<#import "/templates/util/Setup.ftl" as Setup>
<#import "/templates/util/Init.ftl" as Init>
<#import "/templates/util/Update.ftl" as Update>
<#import "/templates/component/InnerComponentClass.ftl" as InnerComponent>

<#macro printComponentClassBody comp compHelper identifier isTOPClass>

    <@printInstanceNameAttributeWithGetterSetter/>

    <@Ports.printPortsWithGetterAndSetter comp=comp compHelper=compHelper/>

  // config parameters
    <@Utils.printConfigParameters comp=comp/>

  // component variables
    <@Utils.printVariables comp=comp compHelper=compHelper/>

    <#if comp.isDecomposed()>
      // subcomponents
      <@Subcomponents.printSubcomponentsWithGetter comp=comp compHelper=compHelper/>

      <@printGetAllSubcomponents comp=comp/>

      <@printComputeComposed/>
    <#else>
      <@printComputeAtomic comp=comp identifier=identifier/>

      // the component's behavior implementation
      protected final IComputable <<@Utils.componentInputClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/>, <@Utils.componentResultClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/>>
        ${identifier.getBehaviorImplName()};

      protected void initialize() {
        // get initial values from behavior implementation
        final <@Utils.componentResultClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/> ${identifier.getResultName()} = ${identifier.getBehaviorImplName()}.getInitialValues();

        //set results to ports
        setResult(${identifier.getResultName()});
      }

      protected void setResult(<@Utils.componentResultClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/> result) {
        <#list comp.getOutgoingPorts() as port>
          this.getPort${port.getName()?cap_first}().setValue(result.get${port.getName()?cap_first}());
          this.getPort${port.getName()?cap_first}().setSynced(true);

        </#list>
      }
    </#if>

    <@printInputsSynced comp=comp/>

    <@Setup.printSetupRegion comp=comp compHelper=compHelper/>

    <@Init.printInitMethod comp=comp compHelper=compHelper/>

    <@Update.printUpdateMethod comp=comp/>

    <@printLogPortValuesMethod comp=comp compHelper=compHelper/>

    <@printConstructor comp=comp compHelper=compHelper identifier=identifier isTOPClass=isTOPClass/>

    <#list comp.getInnerComponents() as innerComp>
      <#assign innerIdentifier=identifier.getNewIdentifier(innerComp)>
        <@InnerComponent.printInnerComponentClass innerComp=innerComp compHelper=compHelper identifier=innerIdentifier isTOPClass=isTOPClass/>
    </#list>

</#macro>


<#macro printConstructor comp compHelper identifier isTOPClass>
  <#assign componentClassName>${comp.getName()}<#if isTOPClass>TOP</#if></#assign>
  public ${componentClassName}(<@Utils.printConfigurationParametersAsList comp=comp/>) {
    <#if comp.isPresentParentComponent()>
      super(<#list compHelper.getInheritedParams(comp) as inhParam>${inhParam}<#sep>, </#sep></#list>);
    </#if>

    <#if comp.isAtomic()>
      ${identifier.getBehaviorImplName()} = new <@Utils.componentImplClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/>
      (
      <#if comp.hasParameters()>
        <#list comp.getParameters() as param>
          ${param.getName()} <#sep>, </#sep>
        </#list>
      </#if>
      );
    </#if>

    <#list comp.getParameters() as param>
      this.${param.getName()} = ${param.getName()};
    </#list>

    <@Utils.printVariablesConstructor comp=comp compHelper=compHelper/>
  }
</#macro>

<#macro printComputeAtomic comp identifier>
    <#assign compName = comp.getName()>
  @Override
  public void compute() {
    logPortValues();
    // collect current input port values
    final <@Utils.componentInputClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/> ${identifier.getInputName()} = new <@Utils.componentInputClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/>(
      <#list comp.getAllIncomingPorts() as inPort>this.getPort${inPort.getName()?cap_first}().getCurrentValue()<#sep>, </#sep></#list>);

    try {
      // perform calculations
      final <@Utils.componentResultClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/> ${identifier.getResultName()} = ${identifier.getBehaviorImplName()}.compute(${identifier.getInputName()});

      // set results to ports
      setResult(${identifier.getResultName()});
    } catch (Exception e) {
      Log.error("An exception occurred during the computation cycle", "${compName}", e);
    }
  }
</#macro>

<#macro printInputsSynced comp>
    @Override
    public boolean allInputsSynced() {
      return <#list comp.getAllIncomingPorts() as inPort>this.getPort${inPort.getName()?cap_first}().isSynced()<#sep> && </#sep><#else>true</#list>;
    }
</#macro>

<#macro printComputeComposed>
  @Override
  public void compute() {
    logPortValues();
    java.util.List${r"<IComponent>"} notYetComputed = new java.util.ArrayList<>(getAllSubcomponents());
    while(notYetComputed.size() > 0) {
      java.util.Set${r"<IComponent>"} computedThisIteration = new java.util.HashSet<>();
      for(IComponent subcomponent : notYetComputed) {
        if(subcomponent.allInputsSynced()) {
          subcomponent.compute();
          computedThisIteration.add(subcomponent);
        }
      }
      if(computedThisIteration.isEmpty()) {
        throw new RuntimeException("Could not complete compute cycle due to not all ports being synced. Likely reasons: Forgot to call init() or cyclic connector loop.");
      } else {
        notYetComputed.removeAll(computedThisIteration);
      }
    }
  }
</#macro>

<#macro printInstanceNameAttributeWithGetterSetter>
  private String instanceName = null;

  public void setInstanceName(String instanceName) {
    this.instanceName = instanceName;
  }

  public String getInstanceName() {
    return this.instanceName;
  }
</#macro>

<#macro printLogPortValuesMethod comp compHelper>
  public void logPortValues() {
    if (this.instanceName == null || this.instanceName == "") return;
    StringBuilder sb;
    <#list comp.getPorts() as port>
      sb = new StringBuilder();
      sb.append("Value of port '").append(this.instanceName).append(".")
        .append("${port.getName()} = ")
        .append(this.${port.getName()}.getCurrentValue());
      Log.trace(sb.toString());
    </#list>
  }
</#macro>

<#macro printGetAllSubcomponents comp>
  protected java.util.List${r"<IComponent>"} getAllSubcomponents() {
    return java.util.Arrays.asList(new IComponent[] {
    <#list comp.getSubComponents() as subcomponent>
      ${subcomponent.getName()}<#sep>, </#sep>
    </#list>
    });
  }
</#macro>
