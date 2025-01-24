package org.gluu.service.logger;

import java.io.File;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.LogManager;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.JsonLayout;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.gluu.model.types.LoggingLayoutType;
import org.gluu.service.cdi.async.Asynchronous;
import org.gluu.service.cdi.event.ConfigurationUpdate;
import org.gluu.service.cdi.event.LoggerUpdateEvent;
import org.gluu.service.cdi.event.Scheduled;
import org.gluu.service.timer.event.TimerEvent;
import org.gluu.service.timer.schedule.TimerSchedule;
import org.gluu.util.StringHelper;
import org.slf4j.Logger;

/**
 * Logger service
 *
 * @author Yuriy Movchan Date: 08/19/2018
 */
public abstract class LoggerService {

    private static final JsonLayout DEFAULT_JSON_PATTERN_LAYOUT = JsonLayout.createDefaultLayout();

	private static final PatternLayout DEFAULT_TEXT_PATTERN_LAYOUT = PatternLayout.newBuilder().withPattern("%d %-5p [%t] [%C{6}] (%F:%L) - %m%n").build();

	private final static int DEFAULT_INTERVAL = 15; // 15 seconds

    @Inject
    private Logger log;

    @Inject
    private Event<TimerEvent> timerEvent;

    private Level prevLogLevel;
	private LoggingLayoutType prevLogLoggingLayout;

    private AtomicBoolean isActive;
    
    @PostConstruct
    public void create() {
        this.isActive = new AtomicBoolean(false);
    }

    public void initTimer() {
    	initTimer(false);
    }

    public void initTimer(boolean updateNow) {
        log.info("Initializing Logger Update Timer");

        final int delay = 15;
        final int interval = DEFAULT_INTERVAL;
        
        this.prevLogLevel = getCurrentLogLevel();
        this.prevLogLoggingLayout = getCurrentLoggingLayout();

        timerEvent.fire(new TimerEvent(new TimerSchedule(delay, interval), new LoggerUpdateEvent(),
                Scheduled.Literal.INSTANCE));
        
        if (updateNow) {
        	updateLoggerTimerEvent(null);
        }
    }


    @Asynchronous
    public void updateLoggerTimerEvent(@Observes @Scheduled LoggerUpdateEvent loggerUpdateEvent) {
        if (this.isActive.get()) {
            return;
        }

        if (!this.isActive.compareAndSet(false, true)) {
            return;
        }

        try {
            updateLoggerConfiguration();
            this.prevLogLevel = getCurrentLogLevel();
            this.prevLogLoggingLayout = getCurrentLoggingLayout();
        } catch (Throwable ex) {
            log.error("Exception happened while updating newly added logger configuration", ex);
        } finally {
            this.isActive.set(false);
        }
    }

    @Asynchronous
    public void updateLoggerSeverity(@Observes @ConfigurationUpdate Object appConfiguration) {
        if (this.isActive.get()) {
            return;
        }

        if (!this.isActive.compareAndSet(false, true)) {
            return;
        }

        try {
            updateLoggerSeverityImpl();
        } catch (Throwable ex) {
            log.error("Exception happened while updating logger configuration after base configuration update", ex);
        } finally {
            this.isActive.set(false);
        }
    }

    public void updateLoggerSeverity() {
    	// Full log4j2 configuration reload
    	updateLoggerSeverityImpl();
    }

    private void updateLoggerSeverityImpl() {
        log.info("Starting logging configuration update after configuration change");

        //resetLoggerConfigLocation();

        setDisableJdkLogger();

        if (setExternalLoggerConfig()) {
        	return;
        }
        
        updateLoggerConfiguration();
    }

    private void updateLoggerConfiguration() {
    	// Do periodic update to apply changes to new loggers as well
        String loggingLevel = getLoggingLevel();
		if (isWrongLoggingConfig(loggingLevel)) {
	        log.warn("Log level is invalid in logging configuration");
			return;
		}

        Level level = getCurrentLogLevel();
        LoggingLayoutType loggingLayout = getCurrentLoggingLayout();

        log.info("Setting layout and loggers level to '{}`, `{}' after logging configuration update", loggingLayout, loggingLevel);

        updateAppendersAndLogLevel(prevLogLoggingLayout, loggingLayout, prevLogLevel, level);
    }

    private void setDisableJdkLogger() {
    	if (isDisableJdkLogger()) {
            log.info("Starting JDK loggers update");
	        java.util.logging.Logger globalLogger = java.util.logging.Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
	        if ((globalLogger != null) && (globalLogger.getLevel() != java.util.logging.Level.OFF)) {
	            log.info("Disabling JDK loggers");
		        LogManager.getLogManager().reset();
	            globalLogger.setLevel(java.util.logging.Level.OFF);
	        }
    	}
    }

    private boolean setExternalLoggerConfig() {
        String externalLoggerConfiguration = getExternalLoggerConfiguration();
        log.info("External log configuration: {}", externalLoggerConfiguration);
        if (StringUtils.isEmpty(externalLoggerConfiguration)) {
            return false;
        }

        File log4jFile = new File(externalLoggerConfiguration);
        if (!log4jFile.exists()) {
            log.info("External log configuration does not exist.");
            return false;
        }

        LoggerContext loggerContext = LoggerContext.getContext(false);
        if (loggerContext.getConfigLocation() != log4jFile.toURI()) {
            log.info("Starting logger context reconfigure after setting path to external configuration: '{}'", log4jFile.toURI());
	        loggerContext.setConfigLocation(log4jFile.toURI());
	        loggerContext.reconfigure();
        } else { 
        	log.debug("Logger context reconfigure is not required. Logconfiguration path is the same");
        }

        return true;
    }

    public void resetLoggerConfigLocation() {
        log.info("Reloading log4j2 configuration");

        LoggerContext loggerContext = LoggerContext.getContext(false);
        if (loggerContext.getConfigLocation() != null) {
            loggerContext.setConfigLocation(null);
        }
        loggerContext.reconfigure();
    }

    private void updateAppendersAndLogLevel(LoggingLayoutType prevLoggingLayout, LoggingLayoutType loggingLayout, Level prevLevel, Level newLevel) {
        final LoggerContext ctx = LoggerContext.getContext(false);

        // Update logging layout if needed
        if (prevLoggingLayout == loggingLayout) {
        	log.info("Updating logging layout configuration from '{}' to '{}'", prevLoggingLayout, loggingLayout);
	        updateLoggerLayout(loggingLayout, newLevel, ctx);
        }
        
        // Update root level if needed
        Level rootLevel = ctx.getConfiguration().getRootLogger().getLevel();
    	if ((newLevel != prevLevel) && (newLevel != rootLevel)) {
        	log.info("Updating root level to '{}'", newLevel);
            ctx.getConfiguration().getRootLogger().setLevel(newLevel);
            ctx.reconfigure();
    	}
    	
    	// Update active loggers
        updateActiveLoggers(newLevel, ctx);
    }

	private void updateActiveLoggers(Level newLevel, final LoggerContext ctx) {
		int count = 0;
		for (org.apache.logging.log4j.core.Logger logger : ctx.getLoggers()) {
		    String loggerName = logger.getName();
		    if (loggerName.startsWith("org.gluu")) {
		        if (logger.getLevel() != newLevel) {
		            count++;
		            logger.setLevel(newLevel);
		        }
		    }
		}

		if (count > 0) {
		    log.info("Updated '{}' loggers to level '{}'", count, newLevel.toString());
		}
	}

    // We need to call this method only on loggingLayout update
	private void updateLoggerLayout(LoggingLayoutType loggingLayout, Level newLevel, final LoggerContext ctx) {
    	int loggerConfigUpdates = 0;
    	int appenderConfigUpdates = 0;
    	
    	AbstractConfiguration config = (AbstractConfiguration) ctx.getConfiguration();
        for (Map.Entry<String, LoggerConfig> loggerConfigEntry : config.getLoggers().entrySet()) {
        	LoggerConfig loggerConfig = loggerConfigEntry.getValue();
        	log.debug("Analyzing log configuration '{}'", loggerConfig.getName());

			if (!loggerConfig.getLevel().equals(newLevel)) {
				loggerConfig.setLevel(newLevel);
	        	log.debug("Updating log level in configuration '{}' to '{}'", loggerConfig.getName(), newLevel);
                loggerConfigUpdates++;
			}

			for (Map.Entry<String, Appender> appenderEntry : loggerConfig.getAppenders().entrySet()) {
	        	Appender appender = appenderEntry.getValue();
	        	log.debug("Analyzing appender '{}'", appender.getName());

	        	Layout<?> layout = appender.getLayout();
	            if (loggingLayout == LoggingLayoutType.TEXT) {
	            	layout = DEFAULT_TEXT_PATTERN_LAYOUT;
	            } else if (loggingLayout == LoggingLayoutType.JSON) {
	            	layout = DEFAULT_JSON_PATTERN_LAYOUT;
	            }

	        	if (appender instanceof RollingFileAppender) {
	                RollingFileAppender rollingFile = (RollingFileAppender) appender;
	                if (rollingFile.getLayout().getClass().isAssignableFrom(layout.getClass())) {
		                log.debug("Skippig appender update '{}'", appender.getName());
	                	// Skip logger which have required logger type
	                	continue;
	                }

	                log.debug("Updating appender '{}'", appender.getName());

	                RollingFileAppender newFileAppender = RollingFileAppender.newBuilder()
	                        .setLayout(layout)
	                        .withStrategy(rollingFile.getManager().getRolloverStrategy())
	                        .withPolicy(rollingFile.getTriggeringPolicy())
	                        .withFileName(rollingFile.getFileName())
	                        .withFilePattern(rollingFile.getFilePattern())
	                        .setName(rollingFile.getName())
	                        .build();
	                newFileAppender.start();
	                appender.stop();
	                loggerConfig.removeAppender(appenderEntry.getKey());
	                loggerConfig.addAppender(newFileAppender, newLevel, null);

	                appenderConfigUpdates++;
	        	} else if (appender instanceof ConsoleAppender) {
	                ConsoleAppender consoleAppender = (ConsoleAppender) appender;
	                if (consoleAppender.getLayout().getClass().isAssignableFrom(layout.getClass())) {
		                log.debug("Skippig appender update '{}'", appender.getName());
	                	// Skip logger which have required logger type
	                	continue;
	                }

	                log.debug("Updating appender '{}'", appender.getName());

	                ConsoleAppender newConsoleAppender = ConsoleAppender.newBuilder()
	                        .setLayout(layout)
	                        .setTarget(consoleAppender.getTarget())
	                        .setName(consoleAppender.getName())
	                        .build();
	                newConsoleAppender.start();
	                appender.stop();
	                loggerConfig.removeAppender(appenderEntry.getKey());
	                loggerConfig.addAppender(newConsoleAppender, newLevel, null);

	                appenderConfigUpdates++;
	            }
	        }
        }

        if ((loggerConfigUpdates > 0) || (appenderConfigUpdates > 0)) {
        	log.trace("Trigger loggers update after '{}' updates", loggerConfigUpdates + appenderConfigUpdates);
        	ctx.updateLoggers();
        }
	}

	private boolean isWrongLoggingConfig(String loggingLevel) {
		return StringHelper.isEmpty(loggingLevel) || StringUtils.isEmpty(this.getLoggingLayout())
				|| StringHelper.equalsIgnoreCase("DEFAULT", loggingLevel);
	}

    private Level getCurrentLogLevel() {
        String loggingLevel = getLoggingLevel();
		if (isWrongLoggingConfig(loggingLevel)) {
			return Level.INFO;
		}

        Level level = Level.toLevel(loggingLevel, Level.INFO);
        
        return level;
    }

    private LoggingLayoutType getCurrentLoggingLayout() {
        String loggingLayout = getLoggingLayout();
		if (isWrongLoggingConfig(loggingLayout)) {
			return LoggingLayoutType.TEXT;
		}

        LoggingLayoutType loggingLayoutType = LoggingLayoutType.getByValue(loggingLayout.toUpperCase());
        if (loggingLayoutType == null) {
			return LoggingLayoutType.TEXT;
        }
        
        return loggingLayoutType;
    }

    public abstract boolean isDisableJdkLogger();

    public abstract String getLoggingLevel();
    
    public abstract String getExternalLoggerConfiguration();

    public abstract String getLoggingLayout();

}
