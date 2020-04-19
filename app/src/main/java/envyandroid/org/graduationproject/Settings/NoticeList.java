package envyandroid.org.graduationproject.Settings;

public class NoticeList {

    private String noticeTitle;
    private String noticeDate;
    private String noticeContent;

    public NoticeList(String noticeTitle, String noticeDate, String noticeContent) {
        this.noticeTitle = noticeTitle;
        this.noticeDate = noticeDate;
        this.noticeContent = noticeContent;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getNoticeDate() {
        return noticeDate;
    }

    public void setNoticeDate(String noticeDate) {
        this.noticeDate = noticeDate;
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

}
