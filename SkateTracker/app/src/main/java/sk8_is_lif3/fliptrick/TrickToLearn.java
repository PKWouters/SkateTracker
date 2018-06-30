package sk8_is_lif3.fliptrick;

import java.util.ArrayList;
import java.util.Map;

public class TrickToLearn {
    public String mName, mDbID, mDifficulty, mUrl, mArticle, mCredits;
    double mAvgRatio;
    int mTotalLandings;
    ArrayList<String> mPrevTricks;

    public TrickToLearn(){

    }

    public String getName(){ return mName; }
    public String getArticle(){ return mArticle; }
    public String getCredits(){return mCredits; }
    public String getDifficulty(){ return mDifficulty; }
    public String getId(){ return mDbID; }
    public String getUrl(){ return mUrl; }
    public ArrayList<String> getPrevTricks() { return mPrevTricks; }

    public void setName(String name){ mName = name; }
    public void setArticle(String article){ mArticle = article; }
    public void setCredits(String credits){ mCredits = credits; }
    public void setDifficulty(String diff){ mDifficulty = diff; }
    public void setId(String dbID){ mDbID = dbID; }
    public void setUrl(String url) {mUrl = url; }
    public void setPrevTricks(ArrayList<String> sessions) { mPrevTricks = sessions; }

}
