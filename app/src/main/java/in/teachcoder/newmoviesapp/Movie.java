package in.teachcoder.newmoviesapp;

/**
 * Created by Arnav on 2/4/2016.
 */
public class Movie {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    String title;
    String year;
    String posterUrl;
    String plot;

    public Movie(String t, String y, String p, String mPlot) {
        this.title = t;
        this.plot = mPlot;
        this.year = y;
        this.posterUrl = p;
    }
}
