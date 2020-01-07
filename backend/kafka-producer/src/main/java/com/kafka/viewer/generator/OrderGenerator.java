package com.kafka.viewer.generator;

import com.kafka.viewer.avro.Order;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 */
public class OrderGenerator implements RecordGenerator<Order> {

    private Random RANDOM;

    public static final class OrderGeneratorProperty extends GeneratorProperty {
        public static final String TIMESTAMP_MIN = "timestamp.min";
        public static final String TIMESTAMP_MAX = "timestamp.max";
    }

    @Override
    public Stream<Order> generateWith(Properties properties) {

        final long idMin = Long
                .parseLong(properties.getProperty(OrderGeneratorProperty.ID_MIN));

        final long timestampMin = Long
                .parseLong(properties.getProperty(OrderGeneratorProperty.TIMESTAMP_MIN));

        final long timestampMax = Long
                .parseLong(properties.getProperty(OrderGeneratorProperty.TIMESTAMP_MAX));

        final long count = Long
                .parseLong(properties.getProperty(OrderGeneratorProperty.COUNT));

        RANDOM = new Random();

        AtomicInteger index = new AtomicInteger();

        return Stream
                .generate(Order::new)
                .limit(count)
                .peek(order -> {
                    order.setId(idMin + index.incrementAndGet());
                    order.setTimestamp(timestampMin +
                            (long) (RANDOM.nextDouble() * (timestampMax - timestampMin)));
                });
    }
}
