package models;

public class Player {

    private String name;

    public Player() {
    }

    public Player(String n) {
        this.name = n;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
