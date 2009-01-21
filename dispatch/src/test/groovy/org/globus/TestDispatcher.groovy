package org.globus

import org.testng.annotations.Test
import javax.xml.namespace.QName
import static org.testng.AssertJUnit.*
import org.testng.annotations.BeforeClass
import org.globus.dispatch.DefaultDispatcher
import org.globus.dispatch.Dispatcher
import org.globus.dispatch.DispatchException
import org.globus.dispatch.DispatchMetaData;


/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Dec 30, 2008
 * Time: 10:52:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestDispatcher {
  def Dispatcher dispatcher
  def operations = [new QName("http://globus.test", "op1"), new QName("http://globus.test", "op2"),
          new QName("http://globus.test", "stringOp1")]

  @BeforeClass
  def void setup() {
    def tester = new Tester()
    dispatcher = new DefaultDispatcher();
    DispatchMetaData md = new DispatchMetaData()
    md.mappedName = this.operations.get(0)
    md.target = tester
    md.method = tester.getClass().getMethod("op1")
    this.dispatcher.addMapping md
    md = new DispatchMetaData()
    md.mappedName = operations.get(1)
    md.target = tester
    md.method = tester.getClass().getMethod("op2")
    this.dispatcher.addMapping md
    md = new DispatchMetaData()
    md.mappedName = operations.get(2)
    md.target = tester
    md.paramTypes = String.class
    md.method = tester.getClass().getMethod("stringOp1", String.class)
    this.dispatcher.addMapping md
  }

  @Test
  def void testDispatch() {
    def result = dispatcher.dispatch(operations.get(0))
    assertEquals(Tester.op1Result, result)
    result = dispatcher.dispatch(operations.get(2), "test")
    assertEquals("${Tester.stringOp1Result} : test", result)
    try {
      dispatcher.dispatch(new QName("http://globus.test", "op3"))
      fail()
    } catch (DispatchException e) {
      println e.getMessage()
    }
    try {
      dispatcher.dispatch(operations.get(0), new Object())
      fail()
    } catch (DispatchException e) {
      println e.getMessage()
    }
    try{
      dispatcher.dispatch(operations.get(2), "test", "test1")
      fail()
    }catch(DispatchException e){
      println e.getMessage()
    }
    try{
      dispatcher.dispatch(operations.get(2), new Object())
      fail();
    }catch(DispatchException e){
      println e.getMessage()
    }
  }

  @Test
  def void testListOperations() {
    dispatcher.listMappedOperations().eachWithIndex {it, idx ->
      assertEquals(operations.get(idx), it)
    }
  }

  @Test
  def void testMapping() {
    assertTrue(dispatcher.isMapped(operations.get(0)))
    assertFalse(dispatcher.isMapped(new QName("http://globus.test", "op3")))
  }
}

def class Tester {
  def static String op1Result = "operation1"
  def static String op2Result = "operation2"
  def static String stringOp1Result = "stringOp1"

  def String op1() {
    return Tester.op1Result
  }

  def String op2() {
    return Tester.op2Result
  }

  def String stringOp1(String param) {
    return "${stringOp1Result} : ${param}"
  }
}