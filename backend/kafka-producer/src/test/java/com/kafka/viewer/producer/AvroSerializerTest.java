package com.kafka.viewer.producer;

import com.kafka.viewer.avro.Customer;
import com.kafka.viewer.avro.Order;
import org.apache.kafka.common.errors.SerializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Roman Pertsev <roman.pertsev@nordigy.ru>
 */
class AvroSerializerTest {

    private final String topicName = "topic-name";
    // Serializers
    private AvroSerializer<Customer> customerSerializer;

    private AvroSerializer<Order> orderSerializer;

    @BeforeEach
    void setUp() {
        customerSerializer = new AvroSerializer<>();

        orderSerializer = new AvroSerializer<>();
    }

    @Test
    @DisplayName("Right customer test")
    void rightCustomerSerializationTest() {
        final Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Customer name");

        assertThatCode(() -> {
            final byte[] serializedCustomer = customerSerializer.serialize(topicName, customer);

            assertThat(serializedCustomer)
                    .hasSizeGreaterThan(0);
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Wrong customer test")
    void wrongCustomerSerializationTest() {
        final Customer customer = new Customer();
        customer.setId(1L);
        customer.setName(null);

        assertThat(catchThrowableOfType(() ->
                customerSerializer.serialize(topicName, customer), SerializationException.class
        )).isNotNull();
    }

}