package arrays;

import arrays.sub.MyType;
import arrays.sub.MyPortType;
import arrays.sub.MyUnusedType;

import java.util.List;

/**
* Test component for ticket #75.
*
*/
component UnusedImportsArrays(int[] a, String[] s, MyType[] mt) {

    port
        in String[] sIn,
        in List<byte[]> lIn,
        out MyPortType[] mpta,
        out byte[] bOut;

}
