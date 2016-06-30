package a;

// unused imports
import a.CorrectCompInA;
import b.GenericComponent;
import java.math.BigDecimal;
import java.io.Serializable;
import java.io.IOException;
import java.util.List;
import java.util.Set;

// used imports
import b.CorrectCompInB;
import b.CorrectCompInB2;
import java.lang.Integer;
import java.lang.Number;
import java.lang.Double;
import java.lang.Boolean;

component UnusedImports <T extends Float, G extends java.util.Set> (Boolean m, java.util.Set s) extends b.GenericComponent<java.math.BigDecimal, java.util.Set<java.io.Serializable>> {

    port 
        in Integer bigInt,
        in Boolean<java.util.Set<String>, Double> mapIn,
        out java.math.BigDecimal bigDec;
    
    component CorrectCompInB ccib;
    component a.CorrectCompInA ccia;
    
    component b.GenericComponent<Number, Integer> generic1;
    component b.GenericComponent<java.io.IOException, Integer> generic2;
    
    component InnerComponent extends CorrectCompInB2 {
    
    }
}