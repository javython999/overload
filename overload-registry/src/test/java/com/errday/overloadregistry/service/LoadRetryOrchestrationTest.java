package com.errday.overloadregistry.service;

import com.errday.overloadregistry.entity.Load;
import com.errday.overloadregistry.entity.Script;
import com.errday.overloadregistry.enums.LoadStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class LoadRetryOrchestrationTest {

    @Mock
    private LoadService loadService;

    @Mock
    private ScriptService scriptService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private LoadRetryOrchestration orchestration;

    @Test
    void retry_resetsStatusToRegisteredAndSendsKafkaMessage() {
        Load load = Load.builder().id(1L).loadName("test").status(LoadStatus.FAILED).build();
        Script script = Script.builder().id(10L).originFileName("test.js").build();

        given(loadService.findById(1L)).willReturn(load);
        given(scriptService.findByLoadId(1L)).willReturn(script);

        orchestration.retry(1L);

        var order = inOrder(loadService, scriptService, kafkaProducerService);
        order.verify(loadService).findById(1L);
        order.verify(loadService).updateStatus(1L, LoadStatus.REGISTERED);
        order.verify(scriptService).findByLoadId(1L);
        order.verify(kafkaProducerService).send(load, script);
    }
}