<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("package", "kind", "type", "super", "typeHelper", "imports")}

package ${package};

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
<#list imports as import>
import ${import};
</#list>

public ${kind} ${type.getName()} ${super} {

  <#if type.isIsEnum()>
    <#-- enum -->
    <#list type.getFields() as field>
        ${field.getName()}
        <#if !field?is_last>,<#else>;</#if>
    </#list>

  <#elseif type.isIsClass()>
    <#-- class -->

    <#-- mandatoryFields are those required in the constructor -->
    <#-- They may originate from attributes or associations with cardinality [1] -->
    <#assign mandatoryFields = []>
    
    <#list type.getFields() as field>
        <#-- attributes -->
        <#assign mandatoryFields = mandatoryFields + [{"name": field.getName(), "type":field.getType().getLoadedSymbol().getStringRepresentation()}]>
        private ${field.getType().getLoadedSymbol().getStringRepresentation()} ${field.getName()};
        public ${field.getType().getLoadedSymbol().getStringRepresentation()} get${field.getName()?cap_first}() {
          return ${field.getName()};
        }
        public void set${field.getName()?cap_first}(${field.getType().getLoadedSymbol().getStringRepresentation()} ${field.getName()}) {
          this.${field.getName()} = ${field.getName()};
        }
    </#list>

    <#list type.getMethods() as method>
      <#-- methods -->
      <#-- methods will be generated with a body if they are not abstract and will have a
      dummy return statement (either 'true', empty String, 0 or 'null') -->
      <#assign methodStub = "">
      <#if method.isIsPrivate()>
        <#assign methodStub = methodStub + "private ">
      <#elseif  method.isIsProtected()>
        <#assign methodStub = methodStub + "protected ">
      <#elseif method.isIsPublic()>
        <#assign methodStub = methodStub + "public ">
      </#if>
      <#if method.isIsDerived()>
        <#assign methodStub = methodStub + "derived ">
      </#if>
      <#if method.isIsFinal()>
        <#assign methodStub = methodStub + "final ">
      </#if>
      <#if method.isIsAbstract()>
        <#assign methodStub = methodStub + "abstract ">
      </#if>
      <#if method.isIsStatic()>
        <#assign methodStub = methodStub + "static ">
      </#if>
      <#if method.getReturnType()??>
        <#assign methodStub = methodStub + method.getReturnType().getName() + " ">
      </#if>
      <#assign methodStub = methodStub + method.getName() + " (">
      <#list method.getSpannedScope().getLocalFieldSymbols() as param>
        <#assign methodStub = methodStub + param.getType().getName() + " " + param.getName()>
        <#if param_has_next>
          <#assign methodStub = methodStub + ", ">
        </#if>
      </#list>
      <#assign methodStub = methodStub + ") ">
<#--  <#if (method.getExceptionList())?? && !method.getExceptionList().isEmpty()>
        <#assign methodStub = methodStub + "throws ">
        <#list method.getExceptionList() as exception>
          <#assign methodStub = methodStub + exception.getName()>
          <#if exception_has_next>
            <#assign methodStub = methodStub + ", ">
          </#if>
        </#list>
      </#if> -->
      <#if !method.isIsAbstract()>
        <#if !method.getReturnType()?? || method.isIsConstructor()>
          <#-- Constructors are handled rather comprehensively below, so we skip them here -->
          <#continue>
        </#if>
        <#if (method.getReturnType().getName() == "void")>
          <#assign methodStub = methodStub + "{ }">
        <#else>
          <#if method.getReturnType().getName() == "boolean">
            <#assign methodStub = methodStub + "{ return true; }">
          <#elseif method.getReturnType().getName() == "String">
            <#assign methodStub = methodStub + "{ return \"\"; }">
          <#elseif (method.getReturnType().getName() == "byte")
          || (method.getReturnType().getName() == "char")
          || (method.getReturnType().getName() == "double")
          || (method.getReturnType().getName() == "float")
          || (method.getReturnType().getName() == "int")
          || (method.getReturnType().getName() == "long")
          || (method.getReturnType().getName() == "short")>
            <#assign methodStub = methodStub + "{ return 0; }">
          <#else>
            <#assign methodStub = methodStub + "{ return null; }">
          </#if>
        </#if>
      <#else>
        <#assign methodStub = methodStub + ";">
      </#if>
      <#assign methodStub = methodStub + "\n">
      ${methodStub}
    </#list> <#-- /methods -->

    <#-- associations -->
    <#list type.getAssociations() as assoc>
      <#assign t=typeHelper.printType(assoc.getTargetType().getLoadedSymbol())>
      <#assign n=assoc.getDerivedName()>

      <#if assoc.getTargetCardinality().isMultiple()>
        <#-- [*] -->

        private List<${t}> ${n} = new ArrayList<>();
        public void set${n?cap_first}(List<${t}> ${n}){
            this.${n} = ${n};
        }
        public List<${t}> get${n?cap_first}(){
            return this.${n};
        }
        public void add${n?cap_first}(${t} ${n}){
            this.${n}.add(${n});
        }
        public void remove${n?cap_first}(${t} ${n}){
            this.${n}.remove(${n});
        }

      <#elseif assoc.getTargetCardinality().getMin()==0>
        <#-- [0..1]-->

        <#assign tOpt="Optional<"+t+">">
        private ${tOpt} ${n} = Optional.empty();
        public ${tOpt} get${n?cap_first}() {
          return ${n};
        }
        public void set${n?cap_first}(${t} ${n}) {
          this.${n} = Optional.of(${n});
        }

      <#else>
        <#-- [1] -->

        <#assign mandatoryFields = mandatoryFields + [{"name": n, "type": t}]>
        private ${t} ${n};
        public ${t} get${n?cap_first}() {
          return ${n};
        }
        public void set${n?cap_first}(${t} ${n}) {
          this.${n} = ${n};
        }
      </#if>
    </#list> <#-- /associations -->

    <#-- constructor -->
    public ${type.getName()}(
    <#list mandatoryFields as mandatoryField>
        ${mandatoryField.type} ${mandatoryField.name}
        <#if !mandatoryField?is_last>,</#if>
    </#list>
    ){
       <#list mandatoryFields as mandatoryField>
         this.${mandatoryField.name} = ${mandatoryField.name};
       </#list>
    }
    
    <#-- no-args constructor, if any arguments are present -->
    <#if mandatoryFields?size != 0>
    public ${type.getName()}() {
    }
    </#if>   
    
  </#if><#-- /class -->
}
