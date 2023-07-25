/* (c) https://github.com/MontiCore/monticore */
package montiarc.automata;

public class InverterCompBuilder {
  
  protected String name;
  
  public InverterCompBuilder(String name) {
    this.name = name;
  }
  
  public InverterCompBuilder() {
    this("");
  }
  
  public InverterCompBuilder setName(String name) {
    this.name = name;
    return this;
  }
  
  public String getName() {
    return name;
  }
  
  public boolean isValid() {
    return this.getName() != null && !"".equals(this.getName());
  }
  
  public InverterComp build() {
    
    String name = this.getName();
    
    if(name == null || "".equals(name)) throw new IllegalStateException();
    
    return new InverterComp(name);
  }
}
