package com.jetbrains.python;

import com.intellij.psi.PsiFileFactory;
import com.jetbrains.python.fixtures.PyTestCase;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.impl.PyBlockEvaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author yole
 */
public class PyBlockEvaluatorTest extends PyTestCase {
  public void testSimple() {
    PyBlockEvaluator eval = doEvaluate("a='b'");
    assertEquals("b", eval.getValue("a"));
  }

  public void testAugAssign() {
    PyBlockEvaluator eval = doEvaluate("a='b'\na+='c'");
    assertEquals("bc", eval.getValue("a"));
  }

  public void testExtend() {
    PyBlockEvaluator eval = doEvaluate("a=['b']\na.extend(['c'])");
    List<String> list = eval.getValueAsStringList("a");
    assertEquals(2, list.size());
    assertEquals("b", list.get(0));
    assertEquals("c", list.get(1));
  }

  public void testVar() {
    PyBlockEvaluator eval = doEvaluate("a='b'\nc='d'\ne=a+c");
    assertEquals("bd", eval.getValue("e"));
  }

  public void testMixedList() {
    PyBlockEvaluator eval = doEvaluate("a=['b',['c','d']]");
    List list = (List)eval.getValue("a");
    assertEquals(2, list.size());
    assertEquals("b", list.get(0));
    assertEquals(new ArrayList<String>(Arrays.asList("c", "d")), list.get(1));
  }

  public void testDict() {
    PyBlockEvaluator eval = doEvaluate("a={'b': 'c'}");
    Map map = (Map) eval.getValue("a");
    assertEquals(1, map.size());
    assertEquals("c", map.get("b"));
  }

  public void testDictAssign() {
    PyBlockEvaluator eval = doEvaluate("a={}\na['b']='c'");
    Map map = (Map) eval.getValue("a");
    assertEquals(1, map.size());
    assertEquals("c", map.get("b"));
  }

  public void testDictUpdate() {
    PyBlockEvaluator eval = doEvaluate("a={}\na.update({'b': 'c'})");
    Map map = (Map) eval.getValue("a");
    assertEquals(1, map.size());
    assertEquals("c", map.get("b"));
  }

  public void testFunction() {
    PyBlockEvaluator eval = new PyBlockEvaluator();
    PyFile file = (PyFile)PsiFileFactory.getInstance(myFixture.getProject()).createFileFromText("a.py", PythonFileType.INSTANCE, "def foo(): return 'a'");
    PyFunction foo = file.findTopLevelFunction("foo");
    eval.evaluate(foo);
    assertEquals("a", eval.getReturnValue());
  }

  private PyBlockEvaluator doEvaluate(String text) {
    PyBlockEvaluator eval = new PyBlockEvaluator();
    PyFile file = (PyFile)PsiFileFactory.getInstance(myFixture.getProject()).createFileFromText("a.py", PythonFileType.INSTANCE, text);
    eval.evaluate(file);
    return eval;
  }
}
