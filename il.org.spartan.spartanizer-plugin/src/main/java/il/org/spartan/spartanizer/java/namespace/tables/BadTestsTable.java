package il.org.spartan.spartanizer.java.namespace.tables;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.spartanizer.ast.navigate.*;
import il.org.spartan.spartanizer.ast.safety.*;
import il.org.spartan.spartanizer.cmdline.*;
import il.org.spartan.tables.*;
import il.org.spartan.utils.*;

/** Generates a table of the class fields
 * @author Dor Ma'ayan
 * @since 2017-10-16 */
public class BadTestsTable extends NominalTables {
  //
  static boolean isJunitAnnotation(List<String> annotations) {
    String[] anno = { "After", "AfterClass", "Before", "BeforeClass" };
    List<String> annoList = Arrays.asList(anno);
    for (String s : annotations) {
      if (annoList.contains(s))
        return true;
    }
    return false;
  }
  //
  static boolean isNeedFix(ASTNode x) {
    return iz.infixExpression(x) || iz.prefixExpression(x);
  }
  //
  static boolean isIgnoredTest(List<String> annotations) {
    String[] anno = { "Ignore" };
    List<String> annoList = Arrays.asList(anno);
    for (String s : annotations) {
      if (annoList.contains(s))
        return true;
    }
    return false;
  }
  @SuppressWarnings("boxing") public static void main(final String[] args) {
    final HashMap<String, Integer> map = new HashMap<>();
    map.put("#Tests", 0);
    map.put("#JavaAsserts", 0);
    map.put("#JunitAsserts", 0);
    map.put("#assertArrayEquals", 0);
    map.put("#assertEquals", 0);
    map.put("#assertFalseGood", 0);
    map.put("#assertNotEquals", 0);
    map.put("#assertNotNull", 0);
    map.put("#assertNotSame", 0);
    map.put("#assertNull", 0);
    map.put("#assertSame", 0);
    map.put("#assertTrueGood", 0);
    map.put("#assertTrueBad", 0);
    map.put("#assertFalseBad", 0);
    map.put("#fail", 0);
    //
    map.put("#NeedFix", 0);
    new GrandVisitor(args) {
      {
        listen(new Tapper() {
          @Override public void endLocation() {
            done(CurrentData.location);
          }
        });
      }

      void reset() {
        map.put("#Tests", 0);
        map.put("#JavaAsserts", 0);
        map.put("#JunitAsserts", 0);
        map.put("#assertArrayEquals", 0);
        map.put("#assertEquals", 0);
        map.put("#assertFalseGood", 0);
        map.put("#assertNotEquals", 0);
        map.put("#assertNotNull", 0);
        map.put("#assertNotSame", 0);
        map.put("#assertNull", 0);
        map.put("#assertSame", 0);
        map.put("#assertTrueGood", 0);
        map.put("#assertTrueBad", 0);
        map.put("#assertFalseBad", 0);
        map.put("#fail", 0);
        //
        map.put("#NeedFix", 0);
      }
      protected void done(final String path) {
        summarize(path);
        reset();
      }
      public void summarize(final String path) {
        initializeWriter();
        if (map.get("#Tests") != 0) {
          table.col("Project", path).col("#Files", map.get("#Files")).col("#Tests", map.get("#Tests")).col("#JavaAsserts", map.get("#JavaAsserts"))
              .col("#JunitAsserts", map.get("#JunitAsserts")).col("#assertArrayEquals", map.get("#assertArrayEquals"))
              .col("#assertEquals", map.get("#assertEquals")).col("#assertNotEquals", map.get("#assertNotEquals"))
              .col("#assertNotNull", map.get("#assertNotNull")).col("#assertNotSame", map.get("#assertNotSame"))
              .col("#assertNull", map.get("#assertNull")).col("#assertSame", map.get("#assertSame"))
              .col("#assertTrueGood", map.get("#assertTrueGood")).col("#assertFalseGood", map.get("#assertFalseGood"))
              .col("#assertTrueBad", map.get("#assertTrueBad")).col("#assertFalseBad", map.get("#assertFalseBad")).col("#fail", map.get("#fail"))
              .col("#NeedFix", map.get("#NeedFix")).nl();
        }
      }
      void initializeWriter() {
        if (table == null)
          table = new Table(corpus, outputFolder);
      }
    }.visitAll(new ASTVisitor(true) {
      @Override public boolean visit(final CompilationUnit ¢) {
        ¢.accept(new ASTVisitor() {
          @Override public boolean visit(final MethodDeclaration x) {
            if (x != null) {
              List<String> annotations = extract.annotations(x).stream().map(a -> a.getTypeName().getFullyQualifiedName())
                  .collect(Collectors.toList());
              if (annotations.contains("Test") || (iz.typeDeclaration(x.getParent()) && az.typeDeclaration(x.getParent()).getSuperclassType() != null
                  && az.typeDeclaration(x.getParent()).getSuperclassType().toString().equals("TestCase"))) {
                // This is real test!
                final Int counter = new Int(); // asseerts counter
                map.put("#Tests", map.get("#Tests") + 1);
                x.accept(new ASTVisitor() {
                  /** handle regular assert */
                  @Override public boolean visit(final AssertStatement x) {
                    map.put("#JavaAsserts", map.get("#JavaAsserts") + 1);
                    counter.step();
                    return true;
                  }
                  /** handle JunitAssert */
                  @Override public boolean visit(final ExpressionStatement x) {
                    if (iz.junitAssert(x)) {
                      map.put("#JunitAsserts", map.get("#JunitAsserts") + 1);
                      MethodInvocation m = az.methodInvocation(az.expressionStatement(x).getExpression());
                      String s = m.getName().getIdentifier();
                      if (s.equals("assertTrue") || s.equals("assertFalse")) {
                        if (m.arguments().size() == 1) {
                          
                          map.put("#" + s + "Good", map.get("#" + s + "Good") + 1);
                          if (isNeedFix(az.astNode(m.arguments().get(0))))
                            map.put("#NeedFix", map.get("#NeedFix") + 1);
                        } else {
                          map.put("#" + s + "Bad", map.get("#" + s + "Bad") + 1);
                          if (isNeedFix(az.astNode(m.arguments().get(1))))
                            map.put("#NeedFix", map.get("#NeedFix") + 1);
                        }
                      } else {
                        map.put("#" + s, map.get("#" + s) + 1);
                      }
                      counter.step();
                    }
                    return true;
                  }
                });
              }
            }
            return true;
          }
        });
        return super.visit(¢);
      }
    });
    table.close();
    System.err.println(table.description());
  }
}
