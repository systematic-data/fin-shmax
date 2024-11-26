package com.systematicdata.shmax.modules.lifecycle.fxtrade.bus;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import lombok.extern.slf4j.Slf4j;

import io.aeron.Aeron;
import io.aeron.Subscription;

import com.systematicdata.shmax.bus.*;
import com.systematicdata.shmax.bus.aeron.*;
import com.systematicdata.shmax.modules.aggregator.*;
import com.systematicdata.shmax.modules.aggregator.logic.*;


/**
 * PriceTick consumer.
 */
@Slf4j
@Component
@ConditionalOnProperty(name="shmax.lifecycle.fxtrade.aeron.use",
                       havingValue="true", matchIfMissing=false)
public class FxTradeTickListenerModule{
    private final Aeron aeron;

    public AeronTickPriceAggregatorModule(
            @Value("${shmax.aeron.lifecycle.fxtrade.price-subscribe}") String consumer,
            @Value("${shmax.aeron.lifecycle.fxtrade.dataSize}") int dataSize,
            @Value("${shmax.aeron.lifecycle.fxtrade.rawStreamIds}") String consumerStreamIds,
            @Value("${shmax.aeron.lifecycle.fxtrade.hedgeStreamIds}") String producerStreamIds) {
        final List<Integer> istreamIds = Arrays.stream(consumerStreamIds.split(","))
                .map(Integer::parseInt).collect(Collectors.toList());
        final List<Integer> ostreamIds = Arrays.stream(producerStreamIds.split(","))
                .map(Integer::parseInt).collect(Collectors.toList());

        final List<MessageProcessor> processors = new ArrayList<>();

        final TickMemory ticksForHedging = new TickMemory();
        final TradeMemory tradesToHedge = new TradeMemory();
        final HedgingLogic hedgingLogic = new HedgingLogic(
                ticksForHedging, tradesToHedge, simpleHedgeOrderPublisher);

        final TickLogic memorizeFXTickLogic = new MemorizeFXTickLogic(ticksForHedging, 
                hedgingLogic);
        final HedgeLogic retryHedgeLogic = new RetryHedgeLogic(ticksForHedging, 
                tradesToHedge, hedgingLogic);

        final Publisher simpleHedgeOrderPublisher = new SimpleHedgeOrderPublisher();

        processors.add(new TickPriceMessageProcessor(memorizeFXTickLogic));
        processors.add(new HedgeResultProcessor(retryHedgeLogic));

        this.aeron = AeronAgentFactory.getInstance().buildAeronConnection();

        AeronAgentFactory.getInstance().createAndStartupAgents(
                this.aeron, consumer, dataSize, istreamIds, processors);
    }

    // DONE: Implement MemorizeTickLogic as a type of TickLogic that keeps the ticks in
    //      an internal table, share with "RetryHedgeLogic"
    // TODO: Implement RetryHedgeLogic as a type of HedgeResultLogic that receives HedgeResult
    // TODO: Implement SimpleHedgeOrderPublisher as a type of Publisher that send HedgeOrder
    // TODO: HedingLogic the logic to cross trades with ticks and trades inside
    //          and sends out hedgeOrders throug SimpleHedgeOrderPublisher
    // TODO: Crear el resources file
    // TODO: Juntarlo todo y crear un script
    // TODO: Poner el puerto REST para que se acepten / anulen / reporten trades
    //      (Quizas otro modulo Spring)
    // TODO: Implement TicksMemory to store and get ticks for hedging
}
