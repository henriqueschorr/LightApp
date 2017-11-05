package tcc.lightapp.models;

/**
 * Created by Henrique on 14/10/2017.
 */

public class Report {
    public long timestamp;
    public String userUid;
    public int positivePhrasesThis;
    public int negativePhrasesThis;
    public int neutralPhrasesThis;
    public double positiveGrowth;
    public double negativeGrowth;
    public int positivePhrases;
    public int negativePhrases;
    public int neutralPhrases;
    public int positiveWords;
    public int negativeWords;
    public int neutralWords;
    public int wordsClassified;
    public int wordsNotClassified;
    public int totalWords;

    public Report(){}

    public Report(long timestamp, String userUid, int positivePhrasesThis, int negativePhrasesThis, int neutralPhrasesThis, double positiveGrowth, double negativeGrowth, int positivePhrases, int negativePhrases, int neutralPhrases, int positiveWords, int negativeWords, int neutralWords, int wordsClassified, int wordsNotClassified, int totalWords) {
        this.timestamp = timestamp;
        this.userUid = userUid;
        this.positivePhrasesThis = positivePhrasesThis;
        this.negativePhrasesThis = negativePhrasesThis;
        this.neutralPhrasesThis = neutralPhrasesThis;
        this.positiveGrowth = positiveGrowth;
        this.negativeGrowth = negativeGrowth;
        this.positivePhrases = positivePhrases;
        this.negativePhrases = negativePhrases;
        this.neutralPhrases = neutralPhrases;
        this.positiveWords = positiveWords;
        this.negativeWords = negativeWords;
        this.neutralWords = neutralWords;
        this.wordsClassified = wordsClassified;
        this.wordsNotClassified = wordsNotClassified;
        this.totalWords = totalWords;
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
