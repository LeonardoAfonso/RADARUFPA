package bet.belleepoquetech.radarufpa;

/**
 * Created by AEDI on 06/04/17.
 */

public class CommentItem {
    private int id, post_id;
    private String texto, name, profilePic, timestamp;

    CommentItem(){}

    public CommentItem(int id, int post_id, String texto, String name, String profilePic, String timestamp) {
        super();
        this.id = id;
        this.post_id = post_id;
        this.texto = texto;
        this.name = name;
        this.profilePic = profilePic;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
