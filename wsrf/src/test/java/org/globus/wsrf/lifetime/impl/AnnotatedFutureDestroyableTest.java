package org.globus.wsrf.lifetime.impl;

import org.globus.wsrf.lifetime.FutureDestroyable;
import org.globus.wsrf.common.SampleResource;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.oasis.wsrf.lifetime.ObjectFactory;
import org.oasis.wsrf.lifetime.SetTerminationTime;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

import javax.xml.datatype.DatatypeFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Test(groups = {"lifetimeTests", "wsrf"})
public class AnnotatedFutureDestroyableTest {


    public void testSetTerminationTime() throws Exception {
        Mockery context = new Mockery();
        SampleResource resource = new SampleResource();
        final Future future = context.mock(Future.class);
        final ScheduledExecutorService schedule = context.mock(ScheduledExecutorService.class);
        context.checking(new Expectations() {{
            oneOf(schedule).schedule(with(any(Callable.class)), with(any(Integer.TYPE)), with(equal(TimeUnit.MILLISECONDS)));
            with(future);
            will(addTimerAction());
        }});
        AnnotatedFutureDestroyable destroyable = new AnnotatedFutureDestroyable(resource, schedule);
        destroyable.setSchedule(schedule);
        destroyable.init();
        SetTerminationTime termTime = new SetTerminationTime();
        termTime.setRequestedLifetimeDuration(DatatypeFactory.newInstance().newDuration(1000));
        assertEquals(resource.getState("My Id"), SampleResource.ResourceState.ACTIVE);
        destroyable.setTerminationTime("My Terminating Id", termTime);
        assertEquals(resource.getState("My Id"), SampleResource.ResourceState.DESTROYED);

    }

    public void testNullParameters() throws Exception {
        try {
            Mockery context = new Mockery();
            final ScheduledExecutorService schedule = context.mock(ScheduledExecutorService.class);
            context.checking(new Expectations() {{
            }});
            AnnotatedFutureDestroyable destroyable = new AnnotatedFutureDestroyable();
            destroyable.setSchedule(schedule);
            destroyable.init();
            fail();
        } catch (Exception ex) {
            //should happen
        }
        try {
            AnnotatedFutureDestroyable destroyable = new AnnotatedFutureDestroyable();
            destroyable.setResource(new SampleResource());
            destroyable.init();
            fail();
        } catch (Exception ex) {
            //should happen
        }
    }


    public void setNullTerminationTime() throws Exception {
        Mockery context = new Mockery();
        final ScheduledExecutorService schedule = context.mock(ScheduledExecutorService.class);
        context.checking(new Expectations() {{
        }});
        AnnotatedFutureDestroyable destroyable = new AnnotatedFutureDestroyable(new SampleResource(), schedule);
        destroyable.setSchedule(schedule);
        destroyable.init();
        SetTerminationTime termTime = new SetTerminationTime();
        termTime.setRequestedLifetimeDuration(null);
        destroyable.setTerminationTime("My Terminating Id", termTime);
    }

    public void cancelTerminationTime() throws Exception {
        Mockery context = new Mockery();
        final ScheduledExecutorService schedule = context.mock(ScheduledExecutorService.class);
        final Future future = context.mock(Future.class);
        context.checking(new Expectations() {{
            oneOf(schedule).schedule(with(any(Callable.class)), with(any(Integer.TYPE)), with(equal(TimeUnit.MILLISECONDS)));
            with(future);
            oneOf(future).cancel(true);
        }});
        AnnotatedFutureDestroyable destroyable = new AnnotatedFutureDestroyable(new SampleResource(), schedule);
        destroyable.setSchedule(schedule);
        destroyable.init();
        SetTerminationTime termTime = new SetTerminationTime();
        termTime.setRequestedLifetimeDuration(DatatypeFactory.newInstance().newDuration(1000));
        destroyable.setTerminationTime("My Terminating Id", termTime);
        destroyable.setTerminationTime("My Terminating Id", new SetTerminationTime());
    }

    public void setAbsoluteTerminationTime() throws Exception {
        ObjectFactory fac = new ObjectFactory();
        Mockery context = new Mockery();
        final Future future = context.mock(Future.class);
        final ScheduledExecutorService schedule = context.mock(ScheduledExecutorService.class);
        context.checking(new Expectations() {{
            oneOf(schedule).schedule(with(any(Callable.class)), with(any(Integer.TYPE)), with(equal(TimeUnit.MILLISECONDS)));
            with(future);
        }});
        AnnotatedFutureDestroyable destroyable = new AnnotatedFutureDestroyable(new SampleResource(), schedule);
        destroyable.setSchedule(schedule);
        destroyable.init();
        SetTerminationTime termTime = new SetTerminationTime();
        GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 5);
        termTime.setRequestedTerminationTime(
                fac.createSetTerminationTimeRequestedTerminationTime(
                        DatatypeFactory.newInstance().newXMLGregorianCalendar(cal)));
        destroyable.setTerminationTime("My Terminating Id", termTime);
    }


    public void testFactory() throws Exception {
        Mockery context = new Mockery();
        SampleResource resource = new SampleResource();
        final Future future = context.mock(Future.class);
        final ScheduledExecutorService schedule = context.mock(ScheduledExecutorService.class);
        context.checking(new Expectations() {{
            oneOf(schedule).schedule(with(any(Callable.class)), with(any(Integer.TYPE)), with(equal(TimeUnit.MILLISECONDS)));
            with(future);
            will(addTimerAction());
        }});
        AnnotatedFutureDestroyableFactory fac = new AnnotatedFutureDestroyableFactory(schedule);
        assertTrue(fac.supports(resource));
        assertFalse(fac.supports(new ArrayList()));
        assertEquals(fac.getInterface(), FutureDestroyable.class);
        FutureDestroyable destroyable = fac.getDelegate(resource);
        SetTerminationTime termTime = new SetTerminationTime();
        termTime.setRequestedLifetimeDuration(DatatypeFactory.newInstance().newDuration(1000));
        assertEquals(resource.getState("My Id"), SampleResource.ResourceState.ACTIVE);
        destroyable.setTerminationTime("My Terminating Id", termTime);
        assertEquals(resource.getState("My Id"), SampleResource.ResourceState.DESTROYED);
    }


    public static Action addTimerAction() {
        return new TimerAction();
    }

    static class TimerAction implements Action {


        public void describeTo(Description description) {
            description.appendText("executing Callable");
        }

        public Object invoke(Invocation invocation) throws Throwable {
            return ((Callable) invocation.getParameter(0)).call();
        }
    }

}
