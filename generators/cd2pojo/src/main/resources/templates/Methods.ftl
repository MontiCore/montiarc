<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/Util.ftl" as Util>
<#macro printMockMethods type helper>
    <#assign hasDefaultConstructor = false>
    <#list type.getMethodSignatureList() as methodSignature>
        <#if methodSignature.isIsConstructor()>
            <#if methodSignature.getParameterList()?size==0><#assign hasDefaultConstructor = true></#if>
            <@printConstructor type=type methodSignature=methodSignature/>

        <#else>
            <@printMethod methodSignature=methodSignature helper=helper withThrowsDeclaration=true/>

        </#if>
    </#list>
    <#if !hasDefaultConstructor>
        <@printNoArgConstructor type=type/>
    </#if>

    //methods required by implemented interfaces
    <#list helper.getCDMethodsFromImplementedInterfaces(type) as methodSignature>
      <@printMethod methodSignature=methodSignature helper=helper withThrowsDeclaration=true/>

    </#list>

    <#list helper.getNonCDMethodsFromImplementedInterfaces(type) as methodSignature>
      <@printMethod methodSignature=methodSignature helper=helper withThrowsDeclaration=false/>
    </#list>
</#macro>

<#macro printMethod methodSignature helper withThrowsDeclaration>
    <#if !methodSignature.getReturnType()??>
      /* ERROR: The return type for method ${methodSignature.getName()} could not be determined. */
        <#return>
    </#if>
<#-- methods will be generated with a body and will have a
dummy return statement (either 'false', empty String, 0 or 'null') -->
    <#local methodHead>
        ${getModifiers(methodSignature)} ${Util.getType(methodSignature.getReturnType())} ${methodSignature.getName()}(
        <@printParams methodSignature=methodSignature/>)
        <#if withThrowsDeclaration><@printThrowsDeclaration methodSignature=methodSignature helper=helper/></#if>
    </#local>
    <#if (methodSignature.getReturnType().getTypeInfo().getName() == "voidType")>
        <#local methodBody = "{ }">
    <#else>
        <#if (methodSignature.getReturnType().getName() == "boolean")
        || (methodSignature.getReturnType().getName() == "Boolean")>
            <#local methodBody = "{ return false; }">
        <#elseif methodSignature.getReturnType().getName() == "String">
            <#local methodBody = "{ return \"\"; }">
        <#elseif (methodSignature.getReturnType().getName() == "byte")
        || (methodSignature.getReturnType().getName() == "Byte")
        || (methodSignature.getReturnType().getName() == "short")
        || (methodSignature.getReturnType().getName() == "Short")
        || (methodSignature.getReturnType().getName() == "int")
        || (methodSignature.getReturnType().getName() == "Integer")
        || (methodSignature.getReturnType().getName() == "long")
        || (methodSignature.getReturnType().getName() == "Long")
        || (methodSignature.getReturnType().getName() == "float")
        || (methodSignature.getReturnType().getName() == "Float")
        || (methodSignature.getReturnType().getName() == "double")
        || (methodSignature.getReturnType().getName() == "Double")
        || (methodSignature.getReturnType().getName() == "char")
        || (methodSignature.getReturnType().getName() == "Character")>
            <#local methodBody = "{ return 0; }">
        <#else>
            <#local methodBody = "{ return null; }">
        </#if>
    </#if>
    ${methodHead}
    ${methodBody}
</#macro>

<#macro printConstructor type methodSignature>
    ${getModifiers(methodSignature)} ${type.getName()}(
    <@printParams methodSignature=methodSignature/>) { }
</#macro>

<#macro printConstructorWithAttributes type helper>
  public ${type.getName()}(
    <#list helper.getConstructorParams(type) as param>${param.getType()} ${param.getName()}<#sep>,
    </#sep></#list>) {
    <#list helper.getConstructorParams(type) as param>
      this.${param.getName()} = ${param.getName()};
    </#list>
  }
</#macro>

<#macro printNoArgConstructor type>
  public ${type.getName()}() { }
</#macro>

<#macro printParams methodSignature>
    <#list methodSignature.getParameterList() as param>
        ${Util.getType(param.getType())}<#if param?is_last && methodSignature.isIsElliptic()>...</#if> ${param.getName()}<#sep>,
    </#list>
</#macro>

<#function getModifiers methodSignature>
    <#local modifier = "">
    <#if methodSignature.isIsPrivate()>
        <#local modifier = modifier + "private ">
    <#elseif  methodSignature.isIsProtected()>
        <#local modifier = modifier + "protected ">
    <#elseif methodSignature.isIsPublic()>
        <#local modifier = modifier + "public ">
    </#if>
    <#if methodSignature.isIsFinal()>
        <#local modifier = modifier + "final ">
    </#if>
    <#if methodSignature.isIsStatic()>
        <#local modifier = modifier + "static ">
    </#if>
    <#return modifier>
</#function>

<#macro printThrowsDeclaration methodSignature helper>
    <#list helper.getThrowsDeclarationEntries(methodSignature)>
      throws <#items as exception>${exception}<#sep>, </#items>
    </#list>
</#macro>

<#macro printInterfaceMethods type helper>
    <#list type.getMethodSignatureList() as methodSignature>
        <@printInterfaceMethod methodSignature=methodSignature helper=helper/>
    </#list>
</#macro>

<#macro printInterfaceMethod methodSignature helper>
    <#if !methodSignature.getReturnType()??>
      /* ERROR: The return type for method ${methodSignature.getName()} could not be determined. */
        <#return>
    </#if>
<#-- methods will be generated without a body -->
    ${Util.getType(methodSignature.getReturnType())} ${methodSignature.getName()}(
    <@printParams methodSignature=methodSignature/>)
    <@printThrowsDeclaration methodSignature=methodSignature helper=helper/>;
</#macro>