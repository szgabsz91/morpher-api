package com.github.szgabsz91.morpher.api.configuration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MorpherConfigurationPropertiesTest {

    @Autowired
    private MorpherConfigurationProperties morpherConfigurationProperties;

    @Test
    public void testConfigurationProperties() {
        MorpherConfigurationProperties.System system = this.morpherConfigurationProperties.getSystem();
        assertThat(system).isNotNull();

        String loadFrom = system.getLoadFrom();
        assertThat(loadFrom).isEqualTo("data/simple-morpher-system.pb");
    }

}
