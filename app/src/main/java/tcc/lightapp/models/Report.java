package tcc.lightapp.models;

/**
 * Created by Henrique on 14/10/2017.
 */

public class Report {
    public long timestamp;
    public String userUid;
    public int positiveWords;
    public int negativeWords;
    public int neutralWords;
    public int classifiedWords;
    public int notClassifiedWords;
    public int totalWords;
    public int positivePhrases;
    public int negativePhrases;
    public int neutralPhrases;

    public Report(){}

    public Report(long timestamp, String userUid, int positiveWords, int negativeWords, int neutralWords, int classifiedWords, int notClassifiedWords, int totalWords, int positivePhrases, int negativePhrases, int neutralPhrases) {
        this.timestamp = timestamp;
        this.userUid = userUid;
        this.positiveWords = positiveWords;
        this.negativeWords = negativeWords;
        this.neutralWords = neutralWords;
        this.classifiedWords = classifiedWords;
        this.notClassifiedWords = notClassifiedWords;
        this.totalWords = totalWords;
        this.positivePhrases = positivePhrases;
        this.negativePhrases = negativePhrases;
        this.neutralPhrases = neutralPhrases;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Report report = (Report) o;

        return timestamp == report.timestamp;

    }

    @Override
    public int hashCode() {
        return (int) (timestamp ^ (timestamp >>> 32));
    }
}
