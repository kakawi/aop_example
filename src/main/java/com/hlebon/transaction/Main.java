package com.hlebon.transaction;

import com.hlebon.transaction.service.ProjectService;
import com.hlebon.transaction.service.ProjectServiceImpl;
import com.hlebon.transaction.service.UserService;
import com.hlebon.transaction.service.UserServiceImpl;
import org.springframework.util.StopWatch;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Main {

	public static void main(String[] args) {
		UserService originalUserService = new UserServiceImpl();

		UserService dummyProxy = (UserService) wrapDummy(originalUserService, UserService.class);
		UserService proxy = (UserService) wrapService(dummyProxy, UserService.class);

		String userName = proxy.getUserName(1);
		System.out.println(userName);
	}

	private static Object wrapService(Object service, Class... interfaces) {
		Wrapper wrapper = new Wrapper(service);
		return Proxy.newProxyInstance(
				Main.class.getClassLoader(),
				interfaces,
				wrapper
		);
	}

	private static Object wrapDummy(Object service, Class... interfaces) {
		DummyProxy handler = new DummyProxy();
		return Proxy.newProxyInstance(
				Main.class.getClassLoader(),
				interfaces,
				handler
		);
	}
}

class Wrapper implements InvocationHandler {
	private final Object original;

	public Wrapper(Object original) {
		this.original = original;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		Object result = method.invoke(original, args); // invoke Real method

		stopWatch.stop();
		System.out.println("TotalTime: " + stopWatch.getTotalTimeNanos());
		return result;
	}
}

class DummyProxy implements InvocationHandler {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
		return "42";
	}
}
