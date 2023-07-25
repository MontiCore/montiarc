/* (c) https://github.com/MontiCore/monticore */
package montiarc.variability;

public class VariabilityCompBuilder {
  
  protected String name;
  
  public VariabilityCompBuilder(String name) {
    this.name = name;
  }
  
  public VariabilityCompBuilder() {
    this("");
  }
  
  public VariabilityCompBuilder setName(String name) {
    this.name = name;
    return this;
  }
  
  public String getName() {
    return name;
  }
  
  protected Integer param_a;
  
  public VariabilityCompBuilder setParam_a(Integer param_a) {
    this.param_a = param_a;
    return this;
  }
  
  public Integer getParam_a() {
    return param_a;
  }
  
  protected Integer param_b;
  
  public VariabilityCompBuilder setParam_b(Integer param_b) {
    this.param_b = param_b;
    return this;
  }
  
  public Integer getParam_b() {
    return param_b;
  }
  
  protected Boolean feature_f1;
  
  public VariabilityCompBuilder setFeature_f1(Boolean feature_f1) {
    this.feature_f1 = feature_f1;
    return this;
  }
  
  public Boolean getFeature_f1() {
    return feature_f1;
  }
  
  protected Boolean feature_f2;
  
  public VariabilityCompBuilder setFeature_f2(Boolean feature_f2) {
    this.feature_f2 = feature_f2;
    return this;
  }
  
  public Boolean getFeature_f2() {
    return feature_f2;
  }
  
  protected Boolean feature_f3;
  
  public VariabilityCompBuilder setFeature_f3(Boolean feature_f3) {
    this.feature_f3 = feature_f3;
    return this;
  }
  
  public Boolean getFeature_f3() {
    return feature_f3;
  }
  
  public boolean isValid() {
    return !(name == null || "".equals(name))
        && !(param_a == null)
        && !(param_b == null)
        && !(feature_f1 == null)
        && !(feature_f2 == null)
        && !(feature_f3 == null)
        && checkConstraintsFulfilled();
  }
  
  public boolean checkConstraintsFulfilled() {
    boolean f1 = getFeature_f1();
    boolean f2 = getFeature_f2();
    boolean f3 = getFeature_f3();
    
    return (f1 || f2)
        && (!((f2 && !f3) || (!f2 && f3)));
  }
  
  public VariabilityComp build() {
    String name = this.getName();
    Integer param_a = this.getParam_a();
    Integer param_b = this.getParam_b();
    Boolean feature_f1 = this.getFeature_f1();
    Boolean feature_f2 = this.getFeature_f2();
    Boolean feature_f3 = this.getFeature_f3();
    
    if(name == null || "".equals(name)) throw new IllegalStateException();
    if(param_a == null) throw new IllegalStateException();
    if(param_b == null) throw new IllegalStateException();
    if(feature_f1 == null) throw new IllegalStateException();
    if(feature_f2 == null) throw new IllegalStateException();
    if(feature_f3 == null)  throw new IllegalStateException();
    
    if(!checkConstraintsFulfilled()) throw new IllegalStateException();
    
    return new VariabilityComp(name, param_a, param_b, feature_f1, feature_f2, feature_f3);
  }
}
