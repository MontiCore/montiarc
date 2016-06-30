package de.monticore.lang.montiarc.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.se_rwth.commons.logging.Log;

/**
 * Created by Michael von Wenckstern on 01.06.2016.
 */
//

public class IndentPrinter {

  public static IndentPrinterGroup groups(String... groups) {
    return new IndentPrinterGroup(groups);
  }

  public static class IndentPrinterGroup {
    protected ArrayList<String> groups = new ArrayList<>();

    protected IndentPrinterGroup(String... groups) {
      for(String s : groups) {
        this.groups.add(s);
      }
    }

    @Override
    public String toString() {
      return (new IndentPrinterHandler(groups, new ArrayList<>(), new LinkedHashMap<>())).toString();
    }


    // 26 methods overloaded here, b/c
    // public <T...> IndentPrinterParameter setParams(T... params)
    // is not possible in Java


    public <A> IndentPrinterParameter params(A param1) {
      return new IndentPrinterParameter(groups, param1);
    }

    public <A, B> IndentPrinterParameter params(A param1, B param2) {
      return new IndentPrinterParameter(groups, param1, param2);
    }

    public <A, B, C> IndentPrinterParameter params(A param1, B param2, C param3) {
      return new IndentPrinterParameter(groups, param1, param2, param3);
    }

    public <A, B, C, D> IndentPrinterParameter params(A param1, B param2, C param3, D param4) {
      return new IndentPrinterParameter(groups, param1, param2, param3, param4);
    }

    public <A, B, C, D, E> IndentPrinterParameter params(A param1, B param2, C param3, D param4, E param5) {
      return new IndentPrinterParameter(groups, param1, param2, param3, param4, param5);
    }

    public <A, B, C, D, E, F> IndentPrinterParameter params(A param1, B param2, C param3, D param4, E param5, F param6) {
      return new IndentPrinterParameter(groups, param1, param2, param3, param4, param5, param6);
    }

    public <A, B, C, D, E, F, G> IndentPrinterParameter params(A param1, B param2, C param3, D param4, E param5, F param6, G param7) {
      return new IndentPrinterParameter(groups, param1, param2, param3, param4, param5, param6, param7);
    }

    public <A, B, C, D, E, F, G, H> IndentPrinterParameter params(A param1, B param2, C param3, D param4, E param5, F param6, G param7, H param8) {
      return new IndentPrinterParameter(groups, param1, param2, param3, param4, param5, param6, param7, param8);
    }

    // ...

  }

  public static class IndentPrinterParameter {
    protected ArrayList<Object> params = new ArrayList<>();
    protected ArrayList<String> groups;

    protected IndentPrinterParameter(ArrayList<String> groups, Object... params) {
      this.groups = groups;
      for(Object p : params) {
        this.params.add(p);
      }
    }

    @Override
    public String toString() {
      return (new IndentPrinterHandler(groups, params, new LinkedHashMap<>()).toString());
    }

    public <A> IndentPrinterHandler handle(Class<? extends A> clazz, Function<A, String> handle1) {
      Map<Class, Function<Object, String>> map = new LinkedHashMap<>();
      map.put(clazz, (Function<Object, String>)handle1);
      return new IndentPrinterHandler(groups, params, map);
    }

//    public <A, B> IndentPrinterHandler handle(Supplier<A> handle1, Supplier<B> handle2) {
//      return new IndentPrinterHandler(groups, params, handle1, handle2);
//    }
  }

  public static class IndentPrinterHandler {
    protected Map<Class, Function<Object, String>> functions;
    protected ArrayList<Object> params;
    protected ArrayList<String> groups;

    // \{(\d)(?:\s*:\s*'([^']+)')?(?:\s*:\s*'([^']+)')?(?:\s*:\s*'([^']+)')?\}
    // this finds (and groups it):
    // {0}
    // {1 : ','}
    // {2 : 'before' : 'end'}
    // {3 : 'before' : 'middle' : 'end'}
    protected final static Pattern pattern = Pattern.compile("\\{(\\d)(?:\\s*:\\s*'([^']+)')?(?:\\s*:\\s*'([^']+)')?(?:\\s*:\\s*'([^']+)')?\\}");

    protected IndentPrinterHandler(ArrayList<String> groups, ArrayList<Object> params, Map<Class, Function<Object, String>> functions) {
      this.groups = groups;
      this.params = params;
      this.functions = functions;
    }

    @Override
    public String toString() {
      for(int i = 0; i < groups.size(); i++) {
        int j = 0;
        Matcher m = pattern.matcher(groups.get(i));
        while(m.find()) {
          if(j > 0) {
            String left = groups.get(i).substring(0, m.start());
            String right = groups.get(i).substring(m.start());
            groups.remove(i);
            groups.add(i, left);
            groups.add(i+1, right);
          }
          j++;
        }
      }

      IndentPrinter ip = new IndentPrinter();
      for(String g : groups) {
        // only one variable place holder per group
        Matcher m = pattern.matcher(g);
        if (m.find()) {
          int pos = Integer.parseInt(m.group(1));
          if (pos < params.size()) {
            Object param = params.get(pos);
            Function<Object, String> fnc = o -> o.toString();
            if(functions.containsKey(param.getClass())) {
              fnc = functions.get(param.getClass());
            }

            if (param instanceof Collection) {
              List c = Lists.newArrayList((Collection)param);
              if(m.group(2) != null && m.group(3) != null &&
                  !c.isEmpty()) {
                ip.print(m.group(2));
              }
              String delim = "";
              if(m.group(2) != null && m.group(3) == null) {
                delim = m.group(2);
              } else if(m.group(2) != null && m.group(3) != null && m.group(4) != null) {
                delim = m.group(3);
              }
              for(int i = 0; i < c.size(); i++) {
                if(functions.containsKey(c.get(i).getClass())) {
                  ip.print(g.replace(m.group(0), functions.get(c.get(i).getClass()).apply(c.get(i))));
                } else {
                  ip.print(g.replace(m.group(0), c.get(i).toString()));
                }
                if(i != c.size() - 1) {
                  ip.print(delim);
                }
              }
              if(m.group(2) != null && m.group(3) != null && m.group(4) == null && !c.isEmpty()) {
                ip.print(m.group(3));
              } else if(m.group(2) != null && m.group(3) != null && m.group(4) != null && !c.isEmpty()) {
                ip.print(m.group(4));
              }
            }
            else if(param instanceof Optional) {
              if(((Optional)param).isPresent()) {
                if(functions.containsKey(((Optional)param).get().getClass())) {
                  fnc = functions.get(param.getClass());
                }
                ip.print(g.replace(m.group(0), fnc.apply(((Optional)param).get())));
              }
            } else {
              ip.print(g.replace(m.group(0), fnc.apply(param)));
            }
          }
          else {
            Log.warn("Index " + pos +
                " is out of range. No object at this position is specified, please start at 0. The string " +
                m.group(0) + " will not replaced with empty string.");
            ip.print(g.replace(m.group(0), ""));
          }
        }
      }
      return ip.getContent();
    }
  }


  protected int indent;
  protected String spacer;
  protected String sp;
  protected int maxlinelength;
  protected boolean optionalBreak;
  private int optionalBreakPosition;
  protected StringBuilder linebuffer;
  protected StringBuilder writtenbuffer;

  public IndentPrinter() {
    this(new StringBuilder());
  }

  public IndentPrinter(StringBuilder writtenbuffer) {
    this.indent = 0;
    this.spacer = "";
    this.sp = "  ";
    this.maxlinelength = -1;
    this.optionalBreak = false;
    this.optionalBreakPosition = -1;
    this.linebuffer = new StringBuilder();
    this.writtenbuffer = writtenbuffer;
  }

  public IndentPrinter(String startContent, int indention) {
    this();
    this.indent(indention);
    this.addLine(startContent);
  }

  public String getContent() {
    this.flushBuffer();
    return this.writtenbuffer.toString();
  }

  public void setIndentLength(int l) {
    this.sp = "";

    int i;
    for(i = 0; i < l; ++i) {
      this.sp = this.sp + " ";
    }

    this.spacer = "";

    for(i = 0; i < this.indent; ++i) {
      this.spacer = this.spacer + this.sp;
    }

  }

  public int getIndentLength() {
    return this.sp.length();
  }

  protected void doPrint(String s) {
    for(int pos = s.indexOf("\n"); pos >= 0; pos = s.indexOf("\n")) {
      String substring = s.substring(0, pos);
      if(this.maxlinelength != -1 && pos + this.linebuffer.length() > this.maxlinelength) {
        this.handleOptionalBreak();
      }

      this.linebuffer.append(substring);
      this.flushBuffer();
      this.writtenbuffer.append("\n");
      s = s.substring(pos + 1);
    }

    if(this.maxlinelength != -1 && s.length() + this.linebuffer.length() > this.maxlinelength) {
      this.handleOptionalBreak();
    }

    this.linebuffer.append(s);
  }

  private void handleOptionalBreak() {
    if(this.optionalBreak) {
      if(this.optionalBreakPosition > 0) {
        String sub2 = this.linebuffer.substring(this.optionalBreakPosition);
        this.linebuffer = this.linebuffer.delete(this.optionalBreakPosition, this.linebuffer.length());
        this.flushBuffer();
        this.writtenbuffer.append("\n");
        this.linebuffer.append(sub2);
      }
    } else {
      this.flushBuffer();
      this.writtenbuffer.append("\n");
    }

  }

  public void flushBuffer() {
    this.optionalBreakPosition = 0;
    if(this.linebuffer.length() != 0) {
      this.writtenbuffer.append(this.spacer);
      this.writtenbuffer.append(this.linebuffer);
    }

    this.linebuffer.setLength(0);
  }

  public IndentPrinter indent(int i) {
    if(i > 0) {
      this.indent += i;

      for(int j = 0; j < i; ++j) {
        this.spacer = this.spacer + this.sp;
      }
    } else if(i < 0) {
      while(i < 0 && this.indent > 0) {
        --this.indent;
        this.spacer = this.spacer.substring(this.sp.length());
        ++i;
      }
    }
    return this;

  }

  public IndentPrinter indent() {
    ++this.indent;
    this.spacer = this.spacer + this.sp;
    return this;
  }

  public IndentPrinter unindent() {
    if(this.indent > 0) {
      --this.indent;
      this.spacer = this.spacer.substring(this.sp.length());
    }
    return this;
  }

  public IndentPrinter print(Object o) {
    this.doPrint(o == null?"null":o.toString());
    return this;
  }

  public IndentPrinter printWithoutProcessing(Object o) {
    this.linebuffer.append(o.toString());
    return this;
  }

  public IndentPrinter println(Object o) {
    this.print(o);
    this.println();
    return this;
  }

  public IndentPrinter println() {
    this.doPrint("\n");
    return this;
  }

  public IndentPrinter println(int n) {
    for(int i = 0; i < n; ++i) {
      this.doPrint("\n");
    }
    return this;
  }

  public StringBuilder getWrittenbuffer() {
    return this.writtenbuffer;
  }

  public boolean isStartOfLine() {
    return this.linebuffer.length() == 0;
  }

  public IndentPrinter addLine(Object newContent) {
    String nc = newContent.toString().trim();
    int counter = 0;

    for(int i = 0; i < nc.length(); ++i) {
      if(nc.charAt(i) == 123) {
        ++counter;
      } else if(nc.charAt(i) == 125) {
        --counter;
      }
    }

    if(counter < 0) {
      this.indent(counter);
    }

    this.print(newContent);
    this.println();
    if(counter > 0) {
      this.indent(counter);
    }
    return this;
  }

  public int getMaxlinelength() {
    return this.maxlinelength;
  }

  public void setMaxlinelength(int maxlinelength) {
    this.maxlinelength = maxlinelength;
  }

  public boolean isOptionalBreak() {
    return this.optionalBreak;
  }

  public IndentPrinter setOptionalBreak(boolean optionBreak) {
    this.optionalBreak = optionBreak;
    return this;
  }

  public IndentPrinter optionalBreak() {
    this.optionalBreakPosition = this.linebuffer.length();
    return this;
  }

  public IndentPrinter clearBuffer() {
    this.flushBuffer();
    this.writtenbuffer.setLength(0);
    return this;
  }
}
