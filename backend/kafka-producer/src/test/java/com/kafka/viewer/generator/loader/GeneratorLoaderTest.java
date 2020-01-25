package com.kafka.viewer.generator.loader;

import com.kafka.viewer.generator.CustomerGenerator;
import com.kafka.viewer.generator.RecordGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 */
class GeneratorLoaderTest {

    @Test
    @DisplayName("Contract test right case")
    public void loaderContractTest() {
        // Given
        String generatorClass = "com.kafka.viewer.generator.CustomerGenerator";

        // When
        assertThatCode(() -> {
            final RecordGenerator<?> recordGenerator =
                    GeneratorLoader.loadGenerator(generatorClass);

            // Then
            assertThat(recordGenerator)
                    .isNotNull()
                    .isInstanceOf(CustomerGenerator.class);
        }).doesNotThrowAnyException();
    }
}