package envyandroid.org.graduationproject.Community;

//-----------------------------------
//  커뮤니티 페이지 - 커뮤니티 리스트
//-----------------------------------
public class CommunityList {

    private String reviewId;
    private String title;
    private String recommend;
    private String views;
    private String tagName;
    private String course;
    private String image;
    private String commentCount;

    public CommunityList(String reviewId, String title, String recommend, String views, String tagName, String course, String image, String commentCount) {
        this.reviewId = reviewId;
        this.title = title;
        this.recommend = recommend;
        this.views = views;
        this.tagName = tagName;
        this.course = course;
        this.image = image;
        this.commentCount = commentCount;
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

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }
}
