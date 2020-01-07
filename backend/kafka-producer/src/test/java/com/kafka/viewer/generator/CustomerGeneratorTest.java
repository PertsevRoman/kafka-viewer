package com.kafka.viewer.generator;

import com.kafka.viewer.avro.Customer;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Properties;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roman Pertsev <roman.pertsev@nordigy.ru>
 */
class CustomerGeneratorTest {

    private Stream<Customer> customerStream;

    private TreeSet<String> namesSet;

    // Records count
    private Long count = 100L;

    // ID boundary
    private Long idMin = 0L;

    @BeforeEach
    public void init() {
        CustomerGenerator customerGenerator = new CustomerGenerator();

        final String namesBlock = "John,Sam,Adam";

        Properties generatorProperties = new Properties();

        generatorProperties.setProperty(CustomerGenerator.CustomerGeneratorProperty.ID_MIN, String.valueOf(idMin));
        generatorProperties.setProperty(CustomerGenerator.CustomerGeneratorProperty.COUNT, String.valueOf(count));
        generatorProperties.setProperty(CustomerGenerator.CustomerGeneratorProperty.NAMES, namesBlock);

        namesSet = Sets.newTreeSet(namesBlock.split(","));

        customerStream = customerGenerator
                .generateWith(generatorProperties);
    }

    @Test
    @DisplayName("Customer generation count test")
    public void customerGenerationCountTest() {
        long customersCount = customerStream.count();

        assertThat(customersCount).isEqualTo(count);
    }

    @Test
    @DisplayName("Customer generation names test")
    public void customerGenerationNamesTest() {
        customerStream.map(Customer::getName)
                .forEach(name -> assertThat(namesSet)
                        .contains(String.valueOf(name)));
    }

    @Test
    @DisplayName("Customer generation ID test")
    public void customerGenerationIdTest() {
        customerStream.map(Customer::getId)
                .forEach(id -> assertThat(id).isBetween(idMin, idMin + count));
    }
}
