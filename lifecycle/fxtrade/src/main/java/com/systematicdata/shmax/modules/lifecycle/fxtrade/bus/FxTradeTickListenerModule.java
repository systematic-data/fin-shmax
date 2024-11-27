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
import com.systematicdata.shmax.logic.TickLogic;
import com.systematicdata.shmax.logic.HedgeResultLogic;
import com.systematicdata.shmax.logic.TradeRequestLogic;
import com.systematicdata.shmax.memory.TickMemory;
import com.systematicdata.shmax.memory.TradeMemory;
import com.systematicdata.shmax.modules.lifecycle.fxtrade.logic.*;


/**
 * PriceTick consumer.
 */
@Slf4j
@Component
@ConditionalOnProperty(name="shmax.lifecycle.fxtrade.aeron.use",
                       havingValue="true", matchIfMissing=false)
public class FxTradeTickListenerModule{
    public FxTradeTickListenerModule(
            @Value("${shmax.aeron.lifecycle.fxtrade.price-consumer}") String consumerPrice,
            @Value("${shmax.aeron.lifecycle.fxtrade.price-streamIds}") String priceStreamIds,
            @Value("${shmax.aeron.lifecycle.fxtrade.hedge-publisher}") String publisherHedge,
            @Value("${shmax.aeron.lifecycle.fxtrade.hedge-streamId}") String hedgeStreamIds,
            @Value("${shmax.aeron.lifecycle.fxtrade.hedge-result-consumer}") String consumerHedgeResult,
            @Value("${shmax.aeron.lifecycle.fxtrade.hedge-result-streamId}") String hedgeResultStreamIds,
            @Value("${shmax.aeron.lifecycle.fxtrade.trade-consumer}") String consumerTrade,
            @Value("${shmax.aeron.lifecycle.fxtrade.trade-streamId}") String tradeStreamIds,
            @Value("${shmax.aeron.lifecycle.fxtrade.dataSize}") int dataSize) {
        final List<Integer> iPriceStreamIds = Arrays.stream(priceStreamIds.split(","))
                .map(Integer::parseInt).collect(Collectors.toList());
        final List<Integer> iTradeStreamIds = Arrays.stream(tradeStreamIds.split(","))
                .map(Integer::parseInt).collect(Collectors.toList());
        final List<Integer> iHedgeStreamIds = Arrays.stream(hedgeStreamIds.split(","))
                .map(Integer::parseInt).collect(Collectors.toList());
        final List<Integer> iHedgeResultStreamIds = Arrays.stream(hedgeResultStreamIds.split(","))
                .map(Integer::parseInt).collect(Collectors.toList());

        final List<MessageProcessor> priceProcessors = new ArrayList<>();
        final List<MessageProcessor> tradeProcessors = new ArrayList<>();
        final List<MessageProcessor> hedgeResultProcessors = new ArrayList<>();

        final TickMemory ticksForHedging = new TickMemory();
        final TradeMemory tradesToHedge = new TradeMemory();

        final Hedger hedger = new Hedger(ticksForHedging, tradesToHedge);
        final TickLogic memorizeFXTickLogic = new MemorizeFXTickLogic(ticksForHedging, 
                hedger);
        final HedgeResultLogic retryHedgeLogic = new RetryHedgeLogic(ticksForHedging, 
                tradesToHedge, hedger);
        final TradeRequestLogic tradeRequestLogic = new TradeRequestToHedgeLogic(
                tradesToHedge, hedger);

        final Publisher simpleHedgeRequestPublisher = new SimpleHedgeRequestPublisher();

        priceProcessors.add(new TickPriceMessageProcessor(memorizeFXTickLogic,
                simpleHedgeRequestPublisher));
        tradeProcessors.add(new TradeRequestProcessor(tradeRequestLogic, 
                simpleHedgeRequestPublisher));
        hedgeResultProcessors.add(new HedgeResultProcessor(retryHedgeLogic, 
                simpleHedgeRequestPublisher));

        log.info("Build and starting up agents to process prices");
        final Aeron aeronPrices = AeronAgentFactory.getInstance().buildAeronConnection();
        AeronAgentFactory.getInstance().createAndStartupAgents(aeronPrices, 
                consumerPrice, publisherHedge, dataSize, 
                iPriceStreamIds, iHedgeStreamIds, priceProcessors);

        log.info("Build and starting up agents to process trade requests");
        final Aeron aeronTrades = AeronAgentFactory.getInstance().buildAeronConnection();
        AeronAgentFactory.getInstance().createAndStartupAgents(aeronTrades, 
                consumerTrade, publisherHedge, dataSize, 
                iTradeStreamIds, iHedgeStreamIds, tradeProcessors);

        log.info("Build and starting up agents to process hedge results");
        final Aeron aeronHedges = AeronAgentFactory.getInstance().buildAeronConnection();
        AeronAgentFactory.getInstance().createAndStartupAgents(aeronHedges, 
                consumerHedgeResult, publisherHedge, dataSize, 
                iHedgeResultStreamIds, iHedgeStreamIds, hedgeResultProcessors);
    }

    // DONE: Implement MemorizeTickLogic as a type of TickLogic that keeps the ticks in
    //      an internal table, share with "RetryHedgeLogic"
    // TODO: Implement SimpleHedgeRequestPublisher as a type of Publisher that send HedgeRequest
    // TODO: HedingLogic the logic to cross trades with ticks and trades inside
    //          and sends out hedgeRequests throug SimpleHedgeRequestPublisher
    // TODO: Crear el resources file
    // TODO: Juntarlo todo y crear un script
    // TODO: Poner el puerto REST para que se acepten / anulen / reporten trades
    //      (Quizas otro modulo Spring)
    // TODO: Implement TicksMemory to store and get ticks for hedging
    // TODO: Implement TradeMemory
    // Test test test
}
