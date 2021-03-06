package expression;

import io.vertx.codetrans.MethodExpressionTest;
import io.vertx.codetrans.annotations.CodeTranslate;
import io.vertx.support.SubHandler;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class MethodInvocation {

  @CodeTranslate
  public void instanceSelectInvocation() throws Exception {
    Runnable counter = MethodExpressionTest.counter;
    counter.run();
  }

  @CodeTranslate
  public void classSelectInvocation() throws Exception {
    MethodExpressionTest.count();
  }

  @CodeTranslate
  public void instanceIdentInvocation() throws Exception {
    someMethod();
  }

  public void someMethod() {
  }

  @CodeTranslate
  public void instanceHandlerSubtypeArgument() throws Exception {
    SubHandler handler = SubHandler.create();
    handler.instanceHandler(handler);
  }

  @CodeTranslate
  public void classHandlerSubtypeArgument() throws Exception {
    SubHandler handler = SubHandler.create();
    SubHandler.classHandler(handler);
  }

  //

  @CodeTranslate
  public void invokeNullArgument() throws Exception {
    MethodExpressionTest.checkNull(null);
  }
}
