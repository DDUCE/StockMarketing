package MyFirebase;

public class Tips_dataobject {

    String title;
    String details;
    String time;




    public Tips_dataobject() {
    }

    public Tips_dataobject(String title, String details, String time) {
        this.title = title;
        this.details = details;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
