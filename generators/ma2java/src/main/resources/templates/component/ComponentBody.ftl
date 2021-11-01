<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/util/Utils.ftl" as Utils>
<#import "/templates/component/Subcomponents.ftl" as Subcomponents>
<#import "/templates/component/Ports.ftl" as Ports>
<#import "/templates/util/Setup.ftl" as Setup>
<#import "/templates/util/Init.ftl" as Init>
<#import "/templates/util/Update.ftl" as Update>
<#import "/templates/component/InnerComponentClass.ftl" as InnerComponent>

<#macro printComponentClassBody comp compHelper identifier isTOPClass>

    <@Ports.printPortsWithGetterAndSetter comp=comp compHelper=compHelper/>

  // component variables
    <@Utils.printVariables comp=comp/>

  // config parameters
    <@Utils.printConfigParameters comp=comp/>

    <#if comp.isDecomposed()>
      // subcomponents
      <@Subcomponents.printSubcomponentsWithGetter comp=comp compHelper=compHelper/>

      <@printComputeComposed comp=comp/>
    <#else>
      <@printComputeAtomic comp=comp identifier=identifier/>

      // the components behavior implementation
      protected final IComputable <<@Utils.componentInputClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/>, <@Utils.componentResultClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/>>
        ${identifier.getBehaviorImplName()};

      protected void initialize() {
        // get initial values from behavior implementation
        final <@Utils.componentResultClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/> ${identifier.getResultName()} = ${identifier.getBehaviorImplName()}.getInitialValues();

        //set results to ports
        setResult(${identifier.getResultName()});
        this.update();
      }

      protected void setResult(<@Utils.componentResultClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/> result) {
        <#list comp.getOutgoingPorts() as port>
          this.getPort${port.getName()?cap_first}().setNextValue(result.get${port.getName()?cap_first}());
        </#list>
      }
    </#if>

    <@Setup.printSetupMethod comp=comp compHelper=compHelper/>

    <@Init.printInitMethod comp=comp compHelper=compHelper/>

    <@Update.printUpdateMethod comp=comp/>

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

    // config parameters
    <#list comp.getParameters() as param>
      this.${param.getName()} = ${param.getName()};
    </#list>
  }
</#macro>

<#macro printComputeAtomic comp identifier>
    <#assign compName = comp.getName()>
  @Override
  public void compute() {
    // collect current input port values
    final <@Utils.componentInputClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/> ${identifier.getInputName()} = new <@Utils.componentInputClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/>(
      <#list comp.getAllIncomingPorts() as inPort>this.getPort${inPort.getName()?cap_first}().getCurrentValue()<#sep>, </#sep></#list>);

    try {
      // perform calculations
      final <@Utils.componentResultClassFQN comp=comp/><@Utils.printFormalTypeParameters comp=comp/> ${identifier.getResultName()} = ${identifier.getBehaviorImplName()}.compute(input);

      // set results to ports
      setResult(${identifier.getResultName()});
    } catch (Exception e) {
      Log.error("${compName}", e);
    }
  }
</#macro>

<#macro printComputeComposed comp>
  @Override
  public void compute() {
    // trigger computation in all subcomponent instances
    <#list comp.getSubComponents() as subcomponent>
      this.${subcomponent.getName()}.compute();
    </#list>
  }
</#macro>