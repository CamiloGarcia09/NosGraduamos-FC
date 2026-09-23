package co.edu.uco.application.usecase.validator.specification.impl;

import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.application.secondaryports.repository.ReferenceCatalog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogReferenceExistsSpecificationTest {

    @Mock
    private RecordExistsCatalogPort recordExistsCatalogPort;

    @Test
    void isSatisfiedBy_returnsTrue_whenReferenceExists() {
        when(recordExistsCatalogPort.exists(ReferenceCatalog.LANGUAGE_BASE, "lang-1")).thenReturn(true);
        CatalogReferenceExistsSpecification specification =
                new CatalogReferenceExistsSpecification(recordExistsCatalogPort, ReferenceCatalog.LANGUAGE_BASE);

        assertTrue(specification.isSatisfiedBy("lang-1"));
        verify(recordExistsCatalogPort).exists(ReferenceCatalog.LANGUAGE_BASE, "lang-1");
    }

    @Test
    void isSatisfiedBy_returnsFalse_whenReferenceDoesNotExist() {
        when(recordExistsCatalogPort.exists(ReferenceCatalog.LANGUAGE_BASE, "unknown")).thenReturn(false);
        CatalogReferenceExistsSpecification specification =
                new CatalogReferenceExistsSpecification(recordExistsCatalogPort, ReferenceCatalog.LANGUAGE_BASE);

        assertFalse(specification.isSatisfiedBy("unknown"));
    }
}