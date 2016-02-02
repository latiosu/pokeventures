package engine;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static SimpleDateFormat sdf = new SimpleDateFormat(Config.Engine.DATE_FORMAT);

    public static void log(Level level, String string) {
        log(level, string, "");
    }

    public static void log(Level level, String fmt, Object... args) {
        System.out.print("[" + getDate() + "] " + level.prepend() + String.format(fmt, args));
    }

    public static String getDate() {
        return sdf.format(new Date());
    }

    public enum Level {
        INFO,
        WARNING,
        ERROR;

        public String prepend() {
            return this.toString() + ": ";
        }
    }

}
