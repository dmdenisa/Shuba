package shuba.shuba.model;

public class User {

    private String name;
    private String groupName;
    private String username;
    private String email;
    private Integer level;
    private boolean login;

    public User(String name, String username, String groupName, String email){
        this.name = name;
        this.username = username;
        this.groupName = groupName;
        this.email = email;
        this.login = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getLevel(){ return  level;    }

    public void setLevel(int level){ this.level = level; }

    public Boolean getLogin() {
        return login;
    }

    public void setLogin(Boolean logedIn) {
        this.login = logedIn;
    }
}