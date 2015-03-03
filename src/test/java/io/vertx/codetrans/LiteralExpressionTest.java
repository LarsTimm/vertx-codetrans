package io.vertx.codetrans;

import groovy.lang.GString;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class LiteralExpressionTest extends ConversionTestBase {

  public static Object result;

  @Before
  public void before() {
    result = null;
  }

  @Test
  public void testLiteralString() throws Exception {
    runAll("expression/LiteralString", "value", () -> {
      assertEquals("foobar", result);
    });
    runAll("expression/LiteralString", "concat", () -> {
      assertEquals("_3_", result.toString());
    });
    String expected = "\n\r\t\f\b\"\\'\u0000\u0041\u007F";
    runAll("expression/LiteralString", "escape", () -> {
      assertEquals(expected, result.toString());
    });
    Lang[] langs = { new JavaScriptLang(), new GroovyLang() };
    for (Lang lang : langs) {
      CodeWriter writer = new CodeWriter(lang);
      lang.renderCharacters(expected, writer);
      assertEquals("\\n\\r\\t\\f\\b\\\"\\\\'\\u0000A\\u007F", writer.getBuffer().toString());
    }
  }

  @Test
  public void testLiteralChar() throws Exception {
    runAll("expression/LiteralChar", () -> {
      assertEquals("a", result);
    });
  }

  @Test
  public void testLiteralNull() throws Exception {
    runAll("expression/LiteralNull", () -> {
      assertEquals(null, result);
    });
  }

  @Test
  public void testLiteralInteger() throws Exception {
    runAll("expression/LiteralInteger", "positiveValue", () -> {
      assertEquals(4, result);
    });
    runAll("expression/LiteralInteger", "negativeValue", () -> {
      assertEquals(-4, result);
    });
  }

  @Test
  public void testLiteralLong() throws Exception {
    runAll("expression/LiteralLong", "positiveValue", () -> {
      assertEquals(4L, ((Number)result).longValue());
    });
    runAll("expression/LiteralLong", "negativeValue", () -> {
      assertEquals(-4L, ((Number)result).longValue());
    });
  }

  @Test
  public void testLiteralBoolean() throws Exception {
    runAll("expression/LiteralBoolean", "trueValue", () -> {
      assertEquals(true, result);
    });
    runAll("expression/LiteralBoolean", "falseValue", () -> {
      assertEquals(false, result);
    });
  }

  @Test
  public void testLiteralFloat() throws Exception {
    runAll("expression/LiteralFloat", "positiveValue", () -> {
      assertEquals(4.0f, ((Number)result).floatValue(), 0.001);
    });
    runAll("expression/LiteralFloat", "negativeValue", () -> {
      assertEquals(-4.0f, ((Number)result).floatValue(), 0.001);
    });
  }

  @Test
  public void testLiteralDouble() throws Exception {
    runAll("expression/LiteralDouble", "positiveValue", () -> {
      assertEquals(4.0d, ((Number)result).doubleValue(), 0.001);
    });
    runAll("expression/LiteralDouble", "negativeValue", () -> {
      assertEquals(-4.0d, ((Number)result).doubleValue(), 0.001);
    });
  }

  @Test
  public void testEnumConstant() {
    runAll("expression/LiteralEnum", "enumConstant", () -> {
      assertTrue(result instanceof String);
      assertEquals("THE_CONSTANT", result);
    });
  }

  @Test
  public void testEnumConstantInString() {
    runJavaScript("expression/LiteralEnum", "enumConstantInString");
    assertTrue(result instanceof String);
    assertEquals("->THE_CONSTANT<-", result);
    runGroovy("expression/LiteralEnum", "enumConstantInString");
    assertTrue(result instanceof GString);
    assertEquals("->THE_CONSTANT<-", result.toString());
  }
}
