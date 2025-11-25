package org.gluu.oxauth.model.crypto;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

public class AbstractCryptoProviderTest {

    @Test
    public void shouldWarnKeysExpiration_whenDaysAreLess_shouldReturnTrue() {
        assertTrue(AbstractCryptoProvider.shouldWarnKeysExpiration(2, 3));
    }

    @Test
    public void shouldWarnKeysExpiration_whenDaysAreMore_shouldReturnFalse() {
        assertFalse(AbstractCryptoProvider.shouldWarnKeysExpiration(4, 3));
    }

    @Test
    public void shouldWarnKeysExpiration_whenConfigurationValueIsZero_shouldReturnTrue() {
        assertTrue(AbstractCryptoProvider.shouldWarnKeysExpiration(2, 0));
    }
}
