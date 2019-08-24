/* (c) https://github.com/MontiCore/monticore */
package sim.generic;

import static org.junit.Assert.*;

import org.junit.Test;

public class StreamPrinterTest {
    
    @Test
    public void testPreconditions1() {
        IStream<?>[] streams = null;
        try {
            StreamPrinter.print(streams);
            fail("NullPointerException expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }
    
    @Test
    public void testPreconditions2() {
        IStream<String> s1 = new Stream<String>();
        IStream<String> s2 = new Stream<String>();
        String[] label = null;
        try {
            StreamPrinter.print(label, s1, s2);
            fail("NullPointerException expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }
    
    @Test
    public void testPreconditions3() {
        IStream<?>[] streams = null;
        String[] label = new String[] {"a", "b"};
        try {
            StreamPrinter.print(label, streams);
            fail("NullPointerException expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }
    
    @Test
    public void testPreconditionsLength1() {
        IStream<String> s1 = new Stream<String>();
        IStream<String> s2 = new Stream<String>();
        String[] label = new String[] {"a", "b", "c"};
        try {
            StreamPrinter.print(label, s1, s2);
            fail("IllegalArgumentException expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
    }
    
    @Test
    public void testPreconditionsLength2() {
        IStream<String> s1 = new Stream<String>();
        IStream<String> s2 = new Stream<String>();
        String[] label = new String[] {"a"};
        try {
            StreamPrinter.print(label, s1, s2);
            fail("IllegalArgumentException expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
    }
    
    @Test
    public void testPrintWithoutLabels() {
        IStream<String> s1 = new Stream<String>();
        IStream<String> s2 = new Stream<String>();
        
        for (int i = 0; i < 100; i++) {
            if (i % 3 == 0) {
                s1.add(Tick.<String> get());
                s1.pollLastMessage();
            }
            if (i % 5 == 0) {
                s2.add(Tick.<String> get());
                s2.pollLastMessage();
            }
            s1.add(Message.of("" + i));
            s1.pollLastMessage();
            s2.add(Message.of("" + i));
            s2.pollLastMessage();
        }
        
        String res = StreamPrinter.print(s1, s2);
        
        String[] lines = res.split("\n");
        
        // check, that all printed lines have the same length
        int length = -1;
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (!line.isEmpty() && line.length() > length) {
                length = line.length();
            }
        }
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (!line.isEmpty()) {
                assertEquals(length, line.length());
            }
        }
    }
    
    @Test
    public void testPrintWithLabels() {
        IStream<String> s1 = new Stream<String>();
        IStream<String> s2 = new Stream<String>();
        
        for (int i = 0; i < 100; i++) {
            if (i % 3 == 0) {
                s1.add(Tick.<String> get());
                s1.pollLastMessage();
            }
            if (i % 5 == 0) {
                s2.add(Tick.<String> get());
                s2.pollLastMessage();
            }
            s1.add(Message.of("" + i));
            s1.pollLastMessage();
            s2.add(Message.of("" + i));
            s2.pollLastMessage();
        }
        
        String res = StreamPrinter.print(new String[]{"LongName", "s"}, s1, s2);
        
        String[] lines = res.split("\n");
        
        // check, that all printed lines have the same length
        int length = -1;
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (!line.isEmpty() && line.length() > length) {
                length = line.length();
            }
        }
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (!line.isEmpty()) {
                assertEquals(length, line.length());
            }
        }
    }
    
    @Test
    public void testPrintWithoutTicks() {
        IStream<String> s1 = new Stream<String>();
        IStream<String> s2 = new Stream<String>();
        
        for (int i = 0; i < 100; i++) {
            if (i % 3 == 0) {
                s1.add(Message.of("" + i));
                s1.pollLastMessage();                
            }
            if (i % 5 == 0) {
                s2.add(Message.of("" + i));
                s2.pollLastMessage();
            }
            
        }
        
        String res = StreamPrinter.print(s1, s2);
        
        String[] lines = res.split("\n");
        
        // check, that all printed lines have the same length
        int length = -1;
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (!line.isEmpty() && line.length() > length) {
                length = line.length();
            }
        }
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (!line.isEmpty()) {
                assertEquals(length, line.length());
            }
        }
    }

}
