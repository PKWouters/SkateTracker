package sk8_is_lif3.skatetracker.transitions;

import java.util.ArrayList;

public class Achievement {
    public ArrayList <String> mChallenges;
    public String mDescription, mId, mName, mStickerUrl;

    public Achievement(){}

    public Achievement(String description, String id, String name, String stickerurl, ArrayList<String> challenges){
        mDescription = description;
        mId = id;
        mName = name;
        mStickerUrl = stickerurl;
        mChallenges = challenges;
    }

    public String getDescription() { return mDescription; }
    public String getId(){return mId;}
    public String getName(){return mName;}
    public String getStickerUrl(){return mStickerUrl;}
    public ArrayList<String> getChallenges() { return mChallenges;}

    public void setDescription(String description){mDescription = description;}
    public void setId(String id){mId = id;}
    public void setName(String name){mName = name;}
    public void setStickerUrl(String stickerUrl){mStickerUrl = stickerUrl;}
    public void setChallenges(ArrayList<String>challenges){mChallenges = challenges;}
}
