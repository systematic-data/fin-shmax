package com.systematicdata.shmax.testtools.fixacceptor;

import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.MarketDataIncrementalRefresh;
import quickfix.fix44.MarketDataRequest;

import java.util.*;
import java.time.*;


public class MarketDataAcceptor {

    public static void main(String[] args) throws Exception {
        // Load configuration
        SessionSettings settings = new SessionSettings("config/acceptor.cfg");

        // Initialize application
        Application application = new MarketDataApplication();

        // Initialize message store factory and log factory
        MessageStoreFactory storeFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new FileLogFactory(settings);

        // Create acceptor
        Acceptor acceptor = new SocketAcceptor(application, storeFactory, settings, logFactory, new DefaultMessageFactory());

        // Start acceptor
        acceptor.start();
        System.out.println("Acceptor started on port 9876");

        // Keep the program running
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down acceptor...");
            acceptor.stop();
        }));
    }
}

class MarketDataApplication extends MessageCracker implements Application {
    private Timer priceTimer;
    private Random rand = new Random();

    @Override
    public void onCreate(SessionID sessionID) {
        System.out.println("Session created: " + sessionID);
    }

    @Override
    public void onLogon(SessionID sessionID) {
        System.out.println("Logon received: " + sessionID);

        // Start sending prices every second
        priceTimer = new Timer();
        priceTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendPriceUpdate(sessionID);
            }
        }, 0, 10000);
    }

    @Override
    public void onLogout(SessionID sessionID) {
        System.out.println("Logout received: " + sessionID);

        // Stop sending prices
        if (priceTimer != null) {
            priceTimer.cancel();
        }
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {}

    @Override
    public void fromAdmin(Message message, SessionID sessionID) {}

    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend {}

    @Override
    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        crack(message, sessionID);
    }

    public void onMessage(MarketDataRequest request, SessionID sessionID) 
            throws FieldNotFound {
        // Handle incoming MarketDataRequest
        String symbol = request.getString(Symbol.FIELD);
        System.out.println("Received MarketDataRequest for symbol: " + symbol);
    }

    private void sendPriceUpdate(SessionID sessionID) {
        try {
            // Create MarketDataIncrementalRefresh message
            MarketDataIncrementalRefresh refresh = new MarketDataIncrementalRefresh();
            refresh.set(new MDReqID("1234-ABCD"));
           

            // Create NoMDEntries group for the market data entries
            // First bid price (Bid)
            MarketDataIncrementalRefresh.NoMDEntries bidGroup1 = new MarketDataIncrementalRefresh.NoMDEntries();
            bidGroup1.setField(new MDUpdateAction(MDUpdateAction.NEW)); // New price update
            bidGroup1.setField(new Symbol("EUR/USD")); // Symbol
            bidGroup1.setField(new MDEntryType(MDEntryType.BID)); // Entry type: BID
            bidGroup1.setField(new MDEntryPx(1.1000 + rand.nextInt(10) * 0.01)); // Random bid price
            bidGroup1.setField(new MDEntrySize(100_000)); // Volume for bid
            bidGroup1.setField(new MDEntryTime(LocalTime.now())); // Time

            // Second bid price (Bid)
            MarketDataIncrementalRefresh.NoMDEntries bidGroup2 = new MarketDataIncrementalRefresh.NoMDEntries();
            bidGroup2.setField(new MDUpdateAction(MDUpdateAction.NEW)); // New price update
            bidGroup2.setField(new Symbol("EUR/USD")); // Symbol
            bidGroup2.setField(new MDEntryType(MDEntryType.BID)); // Entry type: BID
            bidGroup2.setField(new MDEntryPx(1.0990 + rand.nextInt(40) * 0.01)); // Random second bid price
            bidGroup2.setField(new MDEntrySize(1_000_000)); // Volume for second bid
            bidGroup2.setField(new MDEntryTime(LocalTime.now())); // Time

            // First ask price (Ask)
            MarketDataIncrementalRefresh.NoMDEntries askGroup1 = new MarketDataIncrementalRefresh.NoMDEntries();
            askGroup1.setField(new MDUpdateAction(MDUpdateAction.NEW)); // New price update
            askGroup1.setField(new Symbol("EUR/USD")); // Symbol
            askGroup1.setField(new MDEntryType(MDEntryType.OFFER)); // Entry type: ASK
            askGroup1.setField(new MDEntryPx(1.1020 + rand.nextInt(10) * 0.01)); // Random ask price
            askGroup1.setField(new MDEntrySize(100_000)); // Volume for ask
            askGroup1.setField(new MDEntryTime(LocalTime.now())); // Time

            // Second ask price (Ask)
            MarketDataIncrementalRefresh.NoMDEntries askGroup2 = new MarketDataIncrementalRefresh.NoMDEntries();
            askGroup2.setField(new MDUpdateAction(MDUpdateAction.NEW)); // New price update
            askGroup2.setField(new Symbol("EUR/USD")); // Symbol
            askGroup2.setField(new MDEntryType(MDEntryType.OFFER)); // Entry type: ASK
            askGroup2.setField(new MDEntryPx(1.1030 + rand.nextInt(10) * 0.01)); // Random second ask price
            askGroup2.setField(new MDEntrySize(1_000_000)); // Volume for second ask
            askGroup2.setField(new MDEntryTime(LocalTime.now())); // Time

            // Add the bid and ask groups to the refresh message
            refresh.addGroup(bidGroup1);
            refresh.addGroup(bidGroup2);
            refresh.addGroup(askGroup1);
            refresh.addGroup(askGroup2);

            // Send the message
            Session.sendToTarget(refresh, sessionID);
            System.out.println("Sent price update: " + refresh);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

