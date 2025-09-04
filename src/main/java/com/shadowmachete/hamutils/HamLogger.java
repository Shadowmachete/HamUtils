package com.shadowmachete.hamutils;

import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

public class HamLogger {
    private static final String PREFIX = "[HamUtils] ";

    public void info(String message) {
        FMLLog.log(Level.INFO, PREFIX + message);
    }

    public void warn(String message) {
        FMLLog.log(Level.WARN, PREFIX + message);
    }

    public void error(String message) {
        FMLLog.log(Level.ERROR, PREFIX + message);
    }

    public void info(String format, Object... args) {
        info(String.format(format, args));
    }

    public void warn(String format, Object... args) {
        warn(String.format(format, args));
    }

    public void error(String format, Object... args) {
        error(String.format(format, args));
    }
}
