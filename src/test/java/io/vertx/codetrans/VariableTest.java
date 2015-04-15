package io.vertx.codetrans;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class VariableTest extends ConversionTestBase {

  public static final String constant = "foo";
  public static String o;

  @Test
  public void testVariableJavaScript() throws Exception {
    o = null;
    runJavaScript("variable/Variable");
    Assert.assertEquals("foo", o);
  }

  @Test
  public void testVariableGroovy() throws Exception {
    o = null;
    runGroovy("variable/Variable");
    Assert.assertEquals("foo", o);
  }

  @Test
  public void testVariableRuby() throws Exception {
    o = null;
    runRuby("variable/Variable");
    Assert.assertEquals("foo", o);
  }
}
