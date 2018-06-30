package sk8_is_lif3.fliptrick.transitions;

public class Challenge {
    public String mDescription, mId, mName, mRequirement, mType;

    public Challenge(){}

    public Challenge(String description, String id, String name, String requirement, String type){
        mDescription = description;
        mId = id;
        mName = name;
        mRequirement = requirement;
        mType = type;
    }

    public String getDescription() { return mDescription;}
    public String getId() { return mId;}
    public String getName() { return mName;}
    public String getRequirement() { return mRequirement;}
    public String getType() { return mType;}

    public void setDescription(String description) { this.mDescription = description;}
    public void setId(String id) { this.mId = id;}
    public void setName(String name) {this.mName = name;}
    public void setRequirement(String requirement) {this.mRequirement = requirement;}
    public void setType(String type) {this.mType = type;}
}
