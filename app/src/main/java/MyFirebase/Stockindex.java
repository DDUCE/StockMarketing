package MyFirebase;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Stockindex {

    String title;
    String indexpoint;
    String diff;
    String time;




    public Stockindex() {
    }


    public Stockindex(String title, String indexpoint, String diff, String time) {
        this.title = title;
        this.indexpoint = indexpoint;
        this.diff = diff;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIndexpoint() {
        return indexpoint;
    }

    public void setIndexpoint(String indexpoint) {
        this.indexpoint = indexpoint;
    }

    public String getDiff() {
        return diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


   /* @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("indexpoint", indexpoint);
        result.put("diff", diff);
        result.put("time", time);

        return result;
    }
*/



}
