package the_anonymous_koder_2.networkanalyzer.Extras;

/**
 * Created by dell on 3/27/2018.
 */

public class Constants {
    public interface ACTION {
        String MAIN_ACTION = "com.marothiatechs.foregroundservice.action.main";
        String INIT_ACTION = "com.marothiatechs.foregroundservice.action.init";
        String PREV_ACTION = "com.marothiatechs.foregroundservice.action.prev";
        String PLAY_ACTION = "com.marothiatechs.foregroundservice.action.play";
        String NEXT_ACTION = "com.marothiatechs.foregroundservice.action.next";
        String STARTFOREGROUND_ACTION = "com.marothiatechs.foregroundservice.action.startforeground";
        String STOPFOREGROUND_ACTION = "com.marothiatechs.foregroundservice.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }
}
