package envyandroid.org.graduationproject.Community;

//-----------------====-----------------
//  커뮤니티 디테일 페이지 - 댓글 리스트
//---------------------====-------------
public class CommentList {

    private String reviewId;
    private String userNumber;
    private String commentID;
    private String course;
    private String userName;
    private String createTime;
    private String recommend;
    private String commentContent;

    public CommentList(String reviewId, String userNumber, String commentID, String course, String userName, String createTime, String recommend, String commentContent) {
        this.reviewId = reviewId;
        this.userNumber = userNumber;
        this.commentID = commentID;
        this.course = course;
        this.userName = userName;
        this.createTime = createTime;
        this.recommend = recommend;
        this.commentContent = commentContent;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}

