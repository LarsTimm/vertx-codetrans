package expression;

import io.vertx.codetrans.BinaryOperatorExpressionTest;
import io.vertx.codetrans.annotations.CodeTranslate;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class Multiply {

  @CodeTranslate
  public void start() throws Exception {
    BinaryOperatorExpressionTest.result = 2 * 3;
  }
}
