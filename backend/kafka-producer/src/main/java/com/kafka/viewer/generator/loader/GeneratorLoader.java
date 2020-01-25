package com.kafka.viewer.generator.loader;

import com.kafka.viewer.generator.RecordGenerator;
import org.joda.time.IllegalInstantException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 */
public class GeneratorLoader {

    /**
     * Create new {@link RecordGenerator} class instance
     * from subclass
     *
     * @implNote Current implementation expects zero-length
     * args constructor
     *
     * @param generatorClass Generator class name
     * {@link com.kafka.viewer.generator.CustomerGenerator}
     * {@link com.kafka.viewer.generator.OrderGenerator}
     *
     * @return {@link RecordGenerator} instance
     *
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static RecordGenerator<?> loadGenerator(String generatorClass)
            throws ClassNotFoundException, IllegalAccessException,
            InvocationTargetException, InstantiationException {

        final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

        final Class<?> dataClass = systemClassLoader.loadClass(generatorClass);

        if (!RecordGenerator.class.isAssignableFrom(dataClass)) {
            throw new IllegalInstantException(generatorClass + " isn't implement " +
                    "com.kafka.viewer.generator.RecordGenerator class");
        }

        final Constructor<?>[] constructors = dataClass.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                return (RecordGenerator<?>) constructor.newInstance();
            }
        }

        throw new IllegalInstantException("No empty args constructor in " +
                generatorClass + " class");
    }
}
