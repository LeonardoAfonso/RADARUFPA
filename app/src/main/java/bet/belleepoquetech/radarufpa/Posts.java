package bet.belleepoquetech.radarufpa;

/**
 * Created by AEDI on 24/04/17.
 */

public class Posts {

    private int id;
    private String imgUrl;
    private String answer;
    private int user_id;
    private int affected;
    private int seen;
    private int unknown;

    public Posts(){}

    public Posts(int id, String answer, int user_id, int affected, int seen, int unknown){
        super();
        this.id = id;
        this.answer = answer;
        this.user_id = user_id;
        this.affected = affected;
        this.seen = seen;
        this.unknown = unknown;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getAffected() {
        return affected;
    }

    public void setAffected(int affected) {
        this.affected = affected;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public int getUnknown() {
        return unknown;
    }

    public void setUnknown(int unknown) {
        this.unknown = unknown;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }






}
