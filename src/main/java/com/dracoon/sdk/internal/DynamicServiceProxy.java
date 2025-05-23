package com.dracoon.sdk.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.internal.service.Service;
import com.dracoon.sdk.internal.service.ServiceLocator;

class DynamicServiceProxy {

    private final List<Service> mServices = new ArrayList<>();
    private final Map<Class<?>, Object> mServiceProxies = new HashMap<>();

    DynamicServiceProxy(ServiceLocator servicelocator) {
        init(servicelocator);
    }

    private void init(ServiceLocator serviceLocator) {
        mServices.add(new ServerGroupsService());
        mServices.addAll(serviceLocator.getServices());
    }

    public void prepare() {
        createProxy(DracoonClient.Server.class);
        createProxy(DracoonClient.ServerSettings.class);
        createProxy(DracoonClient.ServerPolicies.class);
        createProxy(DracoonClient.ServerPolicies.class);
        createProxy(DracoonClient.Account.class);
        createProxy(DracoonClient.Users.class);
        createProxy(DracoonClient.Groups.class);
        createProxy(DracoonClient.Nodes.class);
        createProxy(DracoonClient.Shares.class);
    }

    // --- DracoonClient main group function ---

    public DracoonClient.Server server() {
        return findProxy(DracoonClient.Server.class);
    }

    public DracoonClient.Account account() {
        return findProxy(DracoonClient.Account.class);
    }

    public DracoonClient.Users users() {
        return findProxy(DracoonClient.Users.class);
    }

    public DracoonClient.Groups groups() {
        return findProxy(DracoonClient.Groups.class);
    }

    public DracoonClient.Nodes nodes() {
        return findProxy(DracoonClient.Nodes.class);
    }

    public DracoonClient.Shares shares() {
        return findProxy(DracoonClient.Shares.class);
    }

    // --- DracoonClient sub group function ---

    @ClientImpl(DracoonClient.Server.class)
    private class ServerGroupsService implements Service {
        @ClientMethodImpl
        public DracoonClient.ServerSettings settings() {
            return findProxy(DracoonClient.ServerSettings.class);
        }

        @ClientMethodImpl
        public DracoonClient.ServerPolicies policies() {
            return findProxy(DracoonClient.ServerPolicies.class);
        }
    }

    // --- Proxy functions ---

    private <T> void createProxy(Class<T> i) {
        T proxy = createProxy(i, mServices);
        mServiceProxies.put(i, proxy);
    }

    @SuppressWarnings("unchecked")
    private <T> T findProxy(Class<T> i) {
        T proxy = (T) mServiceProxies.get(i);
        if (proxy == null) {
            throw new RuntimeException(String.format("Could not find proxy for: %s", i.getName()));
        }
        return proxy;
    }

    @SuppressWarnings("unchecked")
    private static <I> I createProxy(Class<I> i, List<Service> services) {
        return (I) Proxy.newProxyInstance(
                i.getClassLoader(),
                new Class<?>[]{i},
                new ProxyHandler(i, services)
        );
    }

    private static class ProxyTarget {

        private final Service mService;
        private final Method mMethod;

        ProxyTarget(Service service, Method method) {
            mService = service;
            mMethod = method;
        }

        public Service getService() {
            return mService;
        }

        public Method getMethod() {
            return mMethod;
        }

    }

    private static class ProxyHandler implements InvocationHandler {

        private final Map<Method, ProxyTarget> mTargetMethodCache = new HashMap<>();

        ProxyHandler(Class<?> clazz, List<Service> services) {
            init(clazz, services);
        }

        private void init(Class<?> clazz, List<Service> services) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                ProxyTarget proxyTarget = findProxyTarget(clazz, method, services);
                mTargetMethodCache.put(method, proxyTarget);
            }
        }

        private ProxyTarget findProxyTarget(Class<?> clazz, Method method, List<Service> services) {
            // Search all services for target
            for (Service service : services) {
                // Get @ClientImpl annotation on service
                ClientImpl clientImplAnnotation = service.getClass().getAnnotation(ClientImpl.class);
                // If service does not have annotation: Skip service
                if (clientImplAnnotation == null || clientImplAnnotation.value() != clazz) {
                    continue;
                }
                // Check service
                ProxyTarget proxyTarget = findProxyTarget(method, service);
                // If target was found: Use service method
                if (proxyTarget != null) {
                    return proxyTarget;
                }
            }

            // If no target was found: Throw error
            throw new RuntimeException(String.format("Could not proxy method call for: %s/%s",
                    clazz, method.getName()));
        }

        private ProxyTarget findProxyTarget(Method method, Service service) {
            // Search all service methods for target
            ProxyTarget proxyTarget = null;
            Method[] serviceMethods = service.getClass().getDeclaredMethods();
            for (Method serviceMethod : serviceMethods) {
                // Check service method
                proxyTarget = findProxyTarget(method, service, serviceMethod);
                // If target was found: Use service method
                if (proxyTarget != null) {
                    break;
                }
            }
            return proxyTarget;
        }

        private ProxyTarget findProxyTarget(Method method, Service service, Method serviceMethod) {
            // Check service method is public
            if (!Modifier.isPublic(serviceMethod.getModifiers())) {
                return null;
            }

            // Check service method annotation
            ClientMethodImpl clientMethodImplAnnotation = serviceMethod.getAnnotation(
                    ClientMethodImpl.class);
            if (clientMethodImplAnnotation == null) {
                return null;
            }

            // Check service method name
            String serviceMethodName = clientMethodImplAnnotation.value();
            if (serviceMethodName.isEmpty()) {
                serviceMethodName = serviceMethod.getName();
            }
            if (!Objects.equals(serviceMethodName, method.getName())) {
                return null;
            }

            // Check service method argument types
            if (!Arrays.equals(serviceMethod.getParameterTypes(), method.getParameterTypes())) {
                return null;
            }

            // Check service method return type
            if (!Objects.equals(serviceMethod.getReturnType(), method.getReturnType())) {
                return null;
            }

            // Use service method as target
            return new ProxyTarget(service, serviceMethod);
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            ProxyTarget target = mTargetMethodCache.get(method);
            if (target == null) {
                throw new RuntimeException(String.format("Could not proxy method call for: %s/%s",
                        method.getDeclaringClass(), method.getName()));
            }
            try {
                return target.getMethod().invoke(target.getService(), args);
            } catch (Exception e) {
                throw e.getCause();
            }
        }

    }

}
