package envyandroid.org.graduationproject.Home;
//------------------------------------------
//  메인페이지 - 이벤트 리사이클러뷰의 리스트
//------------------------------------------
public class EventList {

    private String eventTitle;
    private String eventImagePath;
    private String eventContent;

    public EventList(String eventTitle, String eventImagePath, String eventContent) {
        this.eventTitle = eventTitle;
        this.eventImagePath = eventImagePath;
        this.eventContent = eventContent;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventImagePath() {
        return eventImagePath;
    }

    public void setEventImagePath(String eventImagePath) {
        this.eventImagePath = eventImagePath;
    }

    public String getEventContent() {
        return eventContent;
    }

    public void setEventContent(String eventContent) {
        this.eventContent = eventContent;
    }
}
