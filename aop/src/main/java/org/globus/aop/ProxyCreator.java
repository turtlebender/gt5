package org.globus.aop;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProxyCreator<T> {
    Map<Method, Callback> perMethod = new HashMap<Method, Callback>();
    List<Aspect> interceptors = new ArrayList<Aspect>();

    public void registerInterceptor(MethodFilter filter, AOPInterceptor interceptor) {
        this.interceptors.add(new Aspect(filter, interceptor));
    }

    public T createProxy(T target) {
        Method[] methods = target.getClass().getDeclaredMethods();
        for (Method method : methods) {
            Advice advice = new Advice(target);
            boolean advised = false;
            for (Aspect aspect : interceptors) {
                if (aspect.filter.accept(method)) {
                    advice.getInterceptors().add(aspect.interceptor);
                    advised = true;
                }
            }
            if (!advised) {
                perMethod.put(method, NoOp.INSTANCE);
            }
            perMethod.put(method, advice);
        }
        Callback[] callbacks = new Callback[perMethod.size() + 1];
        callbacks[0] = NoOp.INSTANCE;
        final Map<Method, Integer> callbackMap = new HashMap<Method, Integer>();
        int counter = 1;
        for (Map.Entry<Method, Callback> entry : perMethod.entrySet()) {
            callbacks[counter] = entry.getValue();
            callbackMap.put(entry.getKey(), counter);
            counter++;
        }
        CallbackFilter cbf = new CallbackFilter() {
            public int accept(Method method) {                
               Integer callbackId = callbackMap.get(method);
                if(callbackId == null){
                    return 0;
                }
                return callbackId.intValue();

            }
        };
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(NoOp.INSTANCE);                
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallbacks(callbacks);
        enhancer.setCallbackFilter(cbf);
        return (T) enhancer.create();
    }

    class Aspect {
        Aspect() {
        }

        Aspect(MethodFilter filter, AOPInterceptor interceptor) {
            this.filter = filter;
            this.interceptor = interceptor;
        }

        public MethodFilter filter;
        public AOPInterceptor interceptor;
    }

    class Advice implements MethodInterceptor {
        private List<AOPInterceptor> interceptors = new ArrayList<AOPInterceptor>();
        private T target;

        Advice() {
        }

        Advice(T target) {
            this.target = target;
        }

        public T getTarget() {
            return target;
        }

        public void setTarget(T target) {
            this.target = target;
        }

        public List<AOPInterceptor> getInterceptors() {
            return interceptors;
        }

        public void setInterceptors(List<AOPInterceptor> interceptors) {
            this.interceptors = interceptors;
        }

        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            try {
                for (AOPInterceptor interceptor : interceptors) {
                    interceptor.before(method, objects);
                }
                Object result = methodProxy.invoke(target, objects);
                for (AOPInterceptor interceptor : interceptors) {
                    interceptor.after(method, objects);
                }
                return result;
            } catch (Exception ex) {
                for (AOPInterceptor interceptor : interceptors) {
                    interceptor.afterThrows(method, ex, objects);
                }
                throw ex;
            } finally {
                for (AOPInterceptor interceptor : interceptors) {
                    interceptor.afterFinally(method, objects);
                }
            }
        }
    }
}
