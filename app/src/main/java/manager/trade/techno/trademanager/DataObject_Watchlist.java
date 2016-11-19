package manager.trade.techno.trademanager;



public class DataObject_Watchlist {

    private String diff_index,full_name;
    private String current_index;
    private String company_code,diff_per_index,time_index,preivous_close;

    public DataObject_Watchlist() {
    }



    public DataObject_Watchlist(String company_code, String current_index, String diff_index, String diff_per_index, String time_index, String preivous_close, String full_name) {
        this.diff_index = diff_index;
        this.full_name = full_name;
        this.current_index = current_index;
        this.company_code = company_code;
        this.diff_per_index = diff_per_index;
        this.time_index = time_index;
        this.preivous_close = preivous_close;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getDiff_index() {
        return diff_index;
    }

    public void setDiff_index(String diff_index) {
        this.diff_index = diff_index;
    }

    public String getCurrent_index() {
        return current_index;
    }

    public void setCurrent_index(String current_index) {
        this.current_index = current_index;
    }

    public String getCompany_code() {
        return company_code;
    }

    public void setCompany_code(String company_code) {
        this.company_code = company_code;
    }

    public String getDiff_per_index() {
        return diff_per_index;
    }

    public void setDiff_per_index(String diff_per_index) {
        this.diff_per_index = diff_per_index;
    }

    public String getTime_index() {
        return time_index;
    }

    public void setTime_index(String time_index) {
        this.time_index = time_index;
    }

    public String getPreivous_close() {
        return preivous_close;
    }

    public void setPreivous_close(String preivous_close) {
        this.preivous_close = preivous_close;
    }



}