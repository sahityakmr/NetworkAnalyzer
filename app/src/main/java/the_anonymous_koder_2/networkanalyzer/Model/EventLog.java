package the_anonymous_koder_2.networkanalyzer.Model;

/**
 * Created by Sahitya on 3/26/2018.
 */

public class EventLog {
    String type;
    String content;
    String date_time;

    public EventLog(String type, String content, String date_time) {
        this.type = type;
        this.content = content;
        this.date_time = date_time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }
}
