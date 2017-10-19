package ru.sbt.terminal;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BeanUtils {

        /**
          * Scans object "from" for all getters. If object "to"
          * contains correspondent setter, it will invoke it
          * to set property value for "to" which equals to the property
          * of "from".
          * <p/>
          * The type in setter should be compatible to the value returned
          * by getter (if not, no invocation performed).
          * Compatible means that parameter type in setter should
          * be the same or be superclass of the return type of the getter.
          * <p/>
          * The method takes care only about public methods.
          *
          * @param to   Object which properties will be set.
          * @param from Object which properties will be used to get values.
          */

    public static void findMethods(Object obj, String namePrefix, Integer paramCount, Map<String, Class<?>> map) {
        Method[] methods = obj.getClass().getMethods();
        for (Method method : methods)
            if (method.getParameterCount() == paramCount.intValue()) {
                String name = method.getName();

                if (name.indexOf(namePrefix) == 0) {
                    /* add the name of method without get or set */

                    if (name.length() == namePrefix.length())
                        throw new RuntimeException("Invalid Name");

                    String methodName = name.substring(3);
                    if (paramCount.equals(0))
                        map.put(methodName, method.getReturnType());
                    else {
                        Class<?>[] clazz = method.getParameterTypes();
                        map.put(methodName, clazz[0]);
                    }
                }
            }
    }

    public static boolean check(Map<String, Class<?>> mapGetters, Map<String, Class<?>> mapSetters) {

        for (String s : mapGetters.keySet()) {
            if (mapSetters.containsKey(s)) {
                /* define correspodent getter & setter */

                Class<?> clazzOut = mapGetters.get(s);
                Class<?> clazzIn  = mapSetters.get(s);

                /* check */
                boolean badAns = false;
                while (clazzOut != null) {
                    if (clazzIn == clazzOut) {
                        badAns = true;
                        break;
                    }

                    clazzOut = clazzOut.getSuperclass();
                }

                if (!badAns)
                    return false;
            }
        }

        return true;
    }

    public static boolean assign(Object to, Object from) {
        Map<String, Class<?>> mapGetters = new HashMap<>();
        Map<String, Class<?>> mapSetters = new HashMap<>();

        findMethods(to, "set", 1, mapSetters);
        findMethods(from, "get", 0, mapSetters);

        return check(mapGetters, mapSetters);
    }


}
