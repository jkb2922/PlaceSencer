package envyandroid.org.graduationproject.Plan;

public class PlanList {

    private String planId;
    private String planTitle;
    private String planDate;
    private String planContent;
    private String planPlace;

    public PlanList(String planId, String planTitle, String planDate, String planContent, String planPlace) {
        this.planId = planId;
        this.planTitle = planTitle;
        this.planDate = planDate;
        this.planContent = planContent;
        this.planPlace = planPlace;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlanTitle() {
        return planTitle;
    }

    public void setPlanTitle(String planTitle) {
        this.planTitle = planTitle;
    }

    public String getPlanDate() {
        return planDate;
    }

    public void setPlanDate(String planDate) {
        this.planDate = planDate;
    }

    public String getPlanContent() {
        return planContent;
    }

    public void setPlanContent(String planContent) {
        this.planContent = planContent;
    }

    public String getPlanPlace() {
        return planPlace;
    }

    public void setPlanPlace(String planPlace) {
        this.planPlace = planPlace;
    }
}
