package com.banking.infrastructure.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.OutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StreamUtilsTest {
    @Mock
    private OutputStream mockedOutputStream;

    @Test
    void givenOutputStreamWhenCloseOutputStreamThenOutputStreamIsClosed() throws IOException {
        doNothing().when(mockedOutputStream).close();
        assertDoesNotThrow(() -> StreamUtils.closeOutputStream(mockedOutputStream));
        verify(mockedOutputStream).close();
    }

    @Test
    void givenOutputStreamWhenCloseOutputStreamThenThrowIOException() throws IOException {
        doThrow(new IOException("Output stream close test exception")).when(mockedOutputStream).close();
        assertThrows(IOException.class, () -> StreamUtils.closeOutputStream(mockedOutputStream));
        verify(mockedOutputStream).close();
    }
}
