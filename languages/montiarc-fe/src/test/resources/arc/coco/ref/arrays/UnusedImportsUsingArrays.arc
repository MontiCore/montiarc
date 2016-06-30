package arrays;

import arrays.sub.MyType;
import arrays.sub.MyPortType;
import arrays.sub.MyUnusedType;
import java.util.List;

component UnusedImportsUsingArrays {

    autoconnect port;
    
    port
        in String[] sIn,
        in List<byte[]> lIn,
        out MyPortType[] mpta,
        out byte[] bOut;
        
    component UnusedImportsArrays(new int[] {1, 2, 3}, new String[] {"Hallo", "Du"}, new MyType[] {new MyType(), new MyType(42)}) a;
}
