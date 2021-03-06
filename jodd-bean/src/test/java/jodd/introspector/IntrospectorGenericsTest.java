// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.datetime.JStopWatch;
import jodd.util.ReflectUtil;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class IntrospectorGenericsTest {

	public static class MethodParameterType<A> {
		List<A> f;
		List f2;
		Map<String, A>f3;
		<T extends List<T>> void m(A a, String p1, T p2, List<?> p3, List<T> p4) { }
		<T extends List<T>> List<T> m2(A a, String p1, T p2, List<?> p3, List<T> p4) { return null; }
		<T extends List<T>> List<A> m3(A a, String p1, T p2, List<?> p3, List<T> p4) { return null; }
	}

	public static class Foo extends MethodParameterType<Integer> {}

	@Test
	public void testFields() throws NoSuchFieldException {
		ClassDescriptor cd = ClassIntrospector.lookup(MethodParameterType.class);

		assertEquals(MethodParameterType.class, cd.getType());
		assertEquals(0, cd.getFieldsCount(false));
		assertEquals(3, cd.getFieldsCount(true));

		FieldDescriptor fd = cd.getFieldDescriptor("f", true);
		FieldDescriptor fd2 = cd.getFieldDescriptor("f2", true);
		FieldDescriptor fd3 = cd.getFieldDescriptor("f3", true);

		assertEquals(List.class, fd.getRawType());
		assertEquals(Object.class, fd.getRawComponentType());

		assertEquals(List.class, fd2.getRawType());
		assertNull(fd2.getRawComponentType());

		assertEquals(Map.class, fd3.getRawType());
		assertEquals(Object.class, fd3.getRawComponentType());

		// impl
		cd = ClassIntrospector.lookup(Foo.class);

		fd = cd.getFieldDescriptor("f", true);
		fd2 = cd.getFieldDescriptor("f2", true);
		fd3 = cd.getFieldDescriptor("f3", true);

		assertEquals(List.class, fd.getRawType());
		assertEquals(Integer.class, fd.getRawComponentType());

		assertEquals(List.class, fd2.getRawType());
		assertNull(fd2.getRawComponentType());

		assertEquals(Map.class, fd3.getRawType());
		assertEquals(Integer.class, fd3.getRawComponentType());
		assertEquals(String.class, ReflectUtil.getComponentType(fd3.getField().getGenericType(), cd.getType(), 0));
	}

	@Test
	public void testMethods() throws NoSuchMethodException {
		ClassDescriptor cd = ClassIntrospector.lookup(MethodParameterType.class);

		assertEquals(MethodParameterType.class, cd.getType());
		assertEquals(0, cd.getMethodsCount(false));
		assertEquals(3, cd.getMethodsCount(true));

		Class[] params = new Class[] {Object.class, String.class, List.class, List.class, List.class};

		Method m = MethodParameterType.class.getDeclaredMethod("m", params);
		assertNotNull(m);

		Method m2 = cd.getMethod("m", params, true);
		assertNotNull(m2);
		assertEquals(m, m2);

		MethodDescriptor md1 = cd.getMethodDescriptor("m", params, true);
		assertNotNull(md1);
		assertEquals(m, md1.getMethod());
		assertArrayEquals(params, md1.getRawParameterTypes());
		assertEquals(void.class, md1.getRawReturnType());
		assertNull(md1.getRawReturnComponentType());

		MethodDescriptor md2 = cd.getMethodDescriptor("m2", params, true);
		assertNotNull(md2);
		assertArrayEquals(params, md2.getRawParameterTypes());
		assertEquals(List.class, md2.getRawReturnType());
		assertEquals(List.class, md2.getRawReturnComponentType());

		MethodDescriptor md3 = cd.getMethodDescriptor("m3", params, true);
		assertNotNull(md3);
		assertArrayEquals(params, md3.getRawParameterTypes());
		assertEquals(List.class, md3.getRawReturnType());
		assertEquals(Object.class, md3.getRawReturnComponentType());


		// impl

		Class[] params2 = new Class[] {Integer.class, String.class, List.class, List.class, List.class};

		ClassDescriptor cd1 = ClassIntrospector.lookup(Foo.class);

		assertEquals(0, Foo.class.getDeclaredMethods().length);

		Method[] allm = cd1.getAllMethods(true);

		assertEquals(3, allm.length);

		md3 = cd1.getMethodDescriptor("m", params, true);
		assertNotNull(md3);

		assertArrayEquals(params2, md3.getRawParameterTypes());

		md3 = cd1.getMethodDescriptor("m3", params, true);
		assertNotNull(md3);
		assertArrayEquals(params2, md3.getRawParameterTypes());
		assertEquals(List.class, md3.getRawReturnType());
		assertEquals(Integer.class, md3.getRawReturnComponentType());


	}
}