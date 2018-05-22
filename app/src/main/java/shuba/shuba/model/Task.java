package shuba.shuba.model;

public class Task {

    private String name;
    private String description;
    private String groupName;
    private boolean state;
    private int number;

    public Task(String name, String description, String groupName, boolean state, int number){
        this.name = name;
        this.description = description;
        this.groupName = groupName;
        this.state = state;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupId(String groupName) {
        this.groupName = groupName;
    }

    public boolean getState() {
        return state;
    }

    public void setName(boolean state) {
        this.state = state;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}