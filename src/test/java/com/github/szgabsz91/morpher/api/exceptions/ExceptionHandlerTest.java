package com.github.szgabsz91.morpher.api.exceptions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionHandlerTest {

    private ExceptionHandler exceptionHandler;

    @Mock
    private ServerWebExchange exchange;

    @Before
    public void setUp() {
        this.exceptionHandler = new ExceptionHandler();
    }

    @Test
    public void testHandleWithUnknownException() {
        Throwable exception = new RuntimeException();
        Mono<Void> result = this.exceptionHandler.handle(exchange, exception);
        result.block();
    }

}
