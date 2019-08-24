package the_anonymous_koder_2.networkanalyzer.Model;

/**
 * Created by Sahitya on 3/22/2018.
 */

public class Operator {
    private String logo;
    private String name;

    public Operator(String logo, String name) {
        this.logo = logo;
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
