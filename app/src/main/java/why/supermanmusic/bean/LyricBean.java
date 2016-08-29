package why.supermanmusic.bean;

public class LyricBean implements Comparable<LyricBean>{
    private int startTime;
    private String content;

    public LyricBean(int startTime, String content) {
        this.startTime = startTime;
        this.content = content;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int compareTo(LyricBean another) {
        return startTime-another.startTime;
    }
}
