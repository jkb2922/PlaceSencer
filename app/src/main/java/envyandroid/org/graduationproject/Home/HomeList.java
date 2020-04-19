package envyandroid.org.graduationproject.Home;

//-----------------------------------------------------
//  메인페이지 - 관심지역 / 추천 리사이클러뷰의 리스트
//-----------------------------------------------------
public class HomeList {

    private String reviewId;
    private String image;
    private String title;

    public HomeList(String reviewId, String image, String title) {
        this.reviewId = reviewId;
        this.image = image;
        this.title = title;
    }


    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
