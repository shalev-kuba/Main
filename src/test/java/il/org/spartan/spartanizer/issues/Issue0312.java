package il.org.spartan.spartanizer.issues;

import static il.org.spartan.spartanizer.testing.TestsUtilsTrimmer.*;

import org.eclipse.jdt.core.dom.*;
import org.junit.*;
import org.junit.runners.*;

import il.org.spartan.spartanizer.tippers.*;

/** Unit tests for the GitHub issue thus numbered
 * @author Yossi Gil {@code Yossi.Gil@GMail.COM}
 * @since 2016 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings({ "static-method", "javadoc" }) //
public class Issue0312 {
  @Test public void a() {
    trimmingOf("int i=1;while(i<7){if(i==5){tipper+=9;return x;}y+=15;return x;}return x;")
        .gives("for(int i=1;i<7;){if(i==5){tipper+=9;return x;}y+=15;return x;}return x;")
        .gives("for(int i=1;i<7;){if(i==5){tipper+=9;return x;}y+=15;break;}return x;")
        .gives("for(int i=1;i<7;){if(i==5){tipper+=9;break;}y+=15;break;}return x;")
        .gives("for(int ¢=1;¢<7;){if(¢==5){tipper+=9;break;}y+=15;break;}return x;")//
        .gives("for(int ¢=1;¢<7;){if(¢==5){tipper+=9;} else {y+=15;} break;}return x;")//
        .gives("for(int ¢=1;¢<7;){if(¢!=5)y+=15;else tipper+=9;break;}return x;") //
        .stays();
  }

  /** Introduced by Yogi on Fri-Mar-31-00:30:47-IDT-2017 (code automatically in
   * class 'JUnitTestMethodFacotry') */
  @Test public void forInta1a7Ifa5b9Breakc15BreakReturnd() {
    trimmingOf("for (int a = 1; a < 7;) { if (a == 5) { b += 9; break; } c += 15; break; } return d;") //
        .using(IfStatement.class, new IfStatementBlockSequencerBlockSameSequencer()) //
        .gives("for(int a=1;a<7;){if(a==5){b+=9;}else{c+=15;}break;}return d;") //
        .using(Block.class, new BlockSingleton()) //
        .gives("for(int a=1;a<7;){if(a==5)b+=9;else c+=15;break;}return d;") //
        .stays() //
    ;
  }

  /** Introduced by Yogi on Fri-Mar-31-00:39:20-IDT-2017 (code automatically in
   * class 'JUnitTestMethodFacotry') */
  @Test public void test_forInta1a7Ifa5b9Breakc15BreakReturnd() {
    trimmingOf("for (int a = 1; a < 7;) { if (a == 5) { b += 9; break; } c += 15; break; } return d;") //
        .using(IfStatement.class, new IfStatementBlockSequencerBlockSameSequencer()) //
        .gives("for(int a=1;a<7;){if(a==5){b+=9;}else{c+=15;}break;}return d;") //
        .using(Block.class, new BlockSingleton()) //
        .gives("for(int a=1;a<7;){if(a==5)b+=9;else c+=15;break;}return d;") //
        .stays() //
    ;
  }

  @Test public void bugInLastIfInMethod1() {
    trimmingOf("       @Override public void f() {\n          if (!isMessageSuppressed(message)) {\n"
        + "            final List<LocalMessage> messages = new ArrayList<LocalMessage>();\n            messages.add(message);\n"
        + "            stats.unreadMessageCount += message.isSet(Flag.SEEN) ? 0 : 1;\n"
        + "            stats.flaggedMessageCount += message.isSet(Flag.FLAGGED) ? 1 : 0;\n            if (listener != null)\n"
        + "              listener.listLocalMessagesAddMessages(account, null, messages);\n          }\n        }")//
            .gives(
                "@Override public void f(){if(isMessageSuppressed(message))return;final List<LocalMessage>messages=new ArrayList<LocalMessage>();messages.add(message);stats.unreadMessageCount+=message.isSet(Flag.SEEN)?0:1;stats.flaggedMessageCount+=message.isSet(Flag.FLAGGED)?1:0;if(listener!=null)listener.listLocalMessagesAddMessages(account,null,messages);}");
  }

  @Test public void chocolate1() {
    trimmingOf("for(int $=0;$<a.length;++$)sum +=$;")//
        .stays();
  }

  @Test public void chocolate2() {
    trimmingOf("for(int i=0, j=0;i<a.length;++j)sum +=i+j;")//
        .gives("for(int ¢=0, j=0;¢<a.length;++j)sum +=¢+j;")//
        .stays();
  }

  @Test public void issue54ForPlain() {
    trimmingOf("int a  = f(); for (int i = 0; i <100;  ++i) b[i] = a;")//
        .gives("for (int i = 0; i <100;  ++i) b[i] = f();")//
        .gives("for (int ¢ = 0; ¢ <100;  ++¢) b[¢] = f();")//
        .stays();
  }

  @Test public void postfixToPrefixAvoidChangeOnLoopInitializer() {
    trimmingOf("for (int s = i++; i <10; ++s) sum+=s;")//
        .gives("for (int ¢ = i++; i <10; ++¢) sum+=¢;")//
        .stays();
  }

  @Test public void t18() {
    trimmingOf("while(b==q){int i;double tipper; x=tipper+i;}")//
        .stays();
  }
}
