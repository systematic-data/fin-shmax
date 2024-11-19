package com.systematicdata.shmax.modules.venues.fix;

import java.nio.*;
import java.util.*;
import org.slf4j.*;

import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.MarketDataRequest;
import quickfix.fix44.MarketDataIncrementalRefresh;

/**
 * Instance of a Connection to FIX Acceptor.
 */
public class ApplicationImpl extends MessageCracker implements Application {
    private static final Logger log = LoggerFactory.getLogger(ApplicationImpl.class);
    private final SessionSettings settings;
    private final MessageStoreFactory storeFactory;
    private final LogFactory logFactory;
    private final MessageFactory messageFactory;
    private final FixConnection messageHandler;

    private SocketInitiator initiator;
    private SessionID sessionID;

    public ApplicationImpl(final FixConnection messageHandler, final String configFile) {
        try {
            this.settings = new SessionSettings(configFile);
            this.storeFactory = new FileStoreFactory(this.settings);
            this.logFactory = new ScreenLogFactory(this.settings);
            this.messageFactory = new DefaultMessageFactory();
            this.messageHandler = messageHandler;
        } catch(ConfigError e) {
            log.error("Error in configuration FIX", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Starts up the Initiator.
     */
    public void init() {
        try {                   
            this.initiator = new SocketInitiator(
                    this, this.storeFactory, this.settings, 
                    this.logFactory, this.messageFactory);
            initiator.start();
            log.info("QuickFIX Initiator started.");

            // Keep application running
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    initiator.stop();
                    log.info("QuickFIX Initiator stopped.");
                } catch (Exception e) {
                    log.error("Error stopping QuickFIX initiator", e);
                    e.printStackTrace();
                }
            }));
        } catch(ConfigError e) {
            log.error("Error in configuration FIX", e);
            throw new RuntimeException(e);
        }
    }



    @Override
    public void onCreate(SessionID sessionID) {
         log.info("Session created: " + sessionID);
    }

    @Override
    public void onLogon(SessionID sessionID) {
        log.info("Logon successful: " + sessionID);
        this.sessionID = sessionID;
    }

    public boolean sendSubscriptionRequest(final MarketDataRequest request) {
        try {
            Session.sendToTarget(request, sessionID);
            log.info("Market data request sent:" + request + " for session " + sessionID);
            return true;
        } catch (SessionNotFound e) {
            log.error("Error sending market data request: " + request + " for session "
                    + sessionID, e.getMessage());
            return false;
        }
    }

    @Override
    public void onLogout(SessionID sessionID) {
        log.info("Logged out: " + sessionID);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {
        log.info("Admin message sent: " + message);
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID) {
        log.info("Admin message received: " + message);
    }

    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend {
        log.info("Application message sent: " + message);
    }

    @Override
    public void fromApp(Message message, SessionID sessionID) 
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        crack(message, sessionID);
    }

    public void onMessage(MarketDataIncrementalRefresh message, SessionID sessionID) {
        this.messageHandler.onMessage(message, sessionID);
    }
}
