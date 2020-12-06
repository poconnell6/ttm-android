package edu.cvtc.ttm.tabletopmanager;

public class CharacterObject {

    private int charID;
    private String charName;
    private int charGold;
    private int charWeight;

    public int getCharID() {
        return charID;
    }

    public void setCharID(int charID) {
        this.charID = charID;
    }

    public String getCharName() {
        return charName;
    }

    public void setCharName(String charName) {
        this.charName = charName;
    }

    public int getCharGold() {
        return charGold;
    }

    public void setCharGold(int charGold) {
        this.charGold = charGold;
    }

    public int getCharWeight() {
        return charWeight;
    }

    public void setCharWeight(int charWeight) {
        this.charWeight = charWeight;
    }

    public CharacterObject(int charID, String charName, int charGold, int charWeight) {
        this.charID = charID;
        this.charName = charName;
        this.charGold = charGold;
        this.charWeight = charWeight;
    }

    public CharacterObject() {

    }

}
