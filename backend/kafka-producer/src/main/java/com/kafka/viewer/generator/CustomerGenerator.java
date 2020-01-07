package com.kafka.viewer.generator;

import com.kafka.viewer.avro.Customer;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * @author Roman Pertsev <roman.pertsev@nordigy.ru>
 */
public class CustomerGenerator implements RecordGenerator<Customer> {

    private Random RANDOM;

    static final class CustomerGeneratorProperty extends GeneratorProperty {
        public static final String NAMES = "generator.names";
    }

    @Override
    public Stream<Customer> generateWith(Properties properties) {

        final int idMin = Integer
                .parseInt(properties.getProperty(CustomerGeneratorProperty.ID_MIN));

        final int count = Integer
                .parseInt(properties.getProperty(CustomerGeneratorProperty.COUNT));

        final String[] names = properties
                .getProperty(CustomerGeneratorProperty.NAMES).split(",");


        RANDOM = new Random();

        AtomicInteger index = new AtomicInteger();

        return Stream
                .generate(Customer::new)
                .limit(count)
                .peek(customer -> {
                    customer.setId(idMin + index.incrementAndGet());
                    customer.setName(names[RANDOM.nextInt(names.length)]);
                });
    }
}
