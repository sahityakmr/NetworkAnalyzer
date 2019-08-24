package the_anonymous_koder_2.networkanalyzer.Model;

/**
 * Created by Sahitya on 3/21/2018.
 */

public class User {
    private String name;
    private String email;
    private String photo;
    private String token;
    private String password;

    public User(String name, String email, String photo, String token, String password) {
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.token = token;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
