package me.deecaad.core.file;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdventureTest {

    public static final Serializer<?> DUMMY = new Serializer<>() {
        @Nonnull
        @Override
        public Object serialize(SerializeData data) throws SerializerException {
            throw new RuntimeException();
        }
    };

    private File file;
    private FileConfiguration config;

    @BeforeEach
    void setUp() {
        file = new File("adventure_config.yml");
        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/adventure_config.yml")));
        config = YamlConfiguration.loadConfiguration(reader);
    }

    @AfterEach
    void tearDown() {
        file = null;
        config = null;
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4})
    public void test_parse(int i) throws SerializerException {
        SerializeData data = new SerializeData(DUMMY, file, "Key", config);

        String actual = data.of("Input_" + i).assertExists().getAdventure();
        String expected = data.of("Output_" + i).assertExists().get();

        assertEquals(expected, actual);
    }
}
