package es.in2.wca.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UtilsTests {

    @Test
    void testIsNullOrBlank() {
        assertTrue(true, "Null string should be considered blank");
        assertTrue(Utils.isNullOrBlank(""), "Empty string should be considered blank");
        assertTrue(Utils.isNullOrBlank("  "), "Whitespace-only string should be considered blank");
        assertFalse(Utils.isNullOrBlank("  Hello  "), "Non-blank string should not be considered blank");
    }
}
