package envyandroid.org.graduationproject.Settings;

public class FavoriteList {

    private String reviewId;
    private String title;
    private String image;
    private String tag;
    private String course;

    public FavoriteList(String reviewId, String title, String image, String tag, String course) {
        this.reviewId = reviewId;
        this.title = title;
        this.image = image;
        this.tag = tag;
        this.course = course;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
