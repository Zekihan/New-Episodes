package com.example.newepisodenotifier;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class TVMaze {

    private static List<String> cookies;
    final private static String USER_AGENT = "Mozilla/5.0";

    final public static int TVRAGE  = 0;
    final public static int THETVDB = 1;
    final public static int IMDB    = 2;

    final private static String rootUrl = "http://api.tvmaze.com";

    public static List<String> getNextEpisodeByImdbTitle(String query){
        try{
            List<String> result = new ArrayList<>();
            String show = showLookup(query,TVMaze.IMDB);
            result.add(show.split("\"name\":\"")[1].split("\"")[0]);
            String episodeUrl = show.split("nextepisode\":\\{\"href\":\"")[1].split("\"")[0];
            String episode = getHtml(episodeUrl);
            result.add(episode.split("\"season\":")[1].split(",")[0]);
            result.add(episode.split("\"number\":")[1].split(",")[0]);
            result.add(episode.split("\"airdate\":\"")[1].split("\"")[0]);
            return result;
        }catch (ArrayIndexOutOfBoundsException e){
            Log.e("getNextEpByImdbTitle",e.getMessage(),e);
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    public static String search(String query){
        String url = rootUrl + "/search/shows?q=" + query;
        String response = getHtml(url);
        return response;
    }
    public static String singleSearch(String query){
        String url = rootUrl + "/singlesearch/shows?q=" + query;
        String response = getHtml(url);
        return response;
    }
    public static String showLookup(String query,int type){
        String url;
        String response;
        switch(type) {
            case 0:
                url = rootUrl + "/lookup/shows?tvrage=" + query;
                response = getHtml(url);
                return response;
            case 1:
                url = rootUrl + "/lookup/shows?thetvdb=" + query;
                response = getHtml(url);
                return response;
            case 2:
                url = rootUrl + "/lookup/shows?imdb=" + query;
                response = getHtml(url);
                return response;
            default:
                response = "No response";
                return response;
        }
    }
    public static String peopleSearch (String query,int type){
        String url = rootUrl + "/search/people?q=" + query;
        String response = getHtml(url);
        return response;
    }

    public static String schedule() {
        String url = rootUrl + "/schedule";
        String response = getHtml(url);
        return response;
    }

    public static String schedule (String countryCode, String date){
        String url = rootUrl + "/schedule?country=" + countryCode + "&date=" + date;
        String response = getHtml(url);
        return response;
    }

    public static String fullSchedule (){
        String url = rootUrl + "/schedule/full";
        String response = getHtml(url);
        return response;
    }

    public static String showMainInformation (String id){
        String url = rootUrl + "/shows/" + id;
        String response = getHtml(url);
        return response;
    }

    public static String showEpisodeList (String id, boolean specials){
        if (specials){
            String url = rootUrl + "/shows/" + id + "/episodes?specials=1";
            String response = getHtml(url);
            return response;
        }else {
            String url = rootUrl + "/shows/" + id + "/episodes";
            String response = getHtml(url);
            return response;
        }
    }

    public static String episodeByNumber (String id, int season, int episode){
        String url = rootUrl + "/shows/" + id + "/episodebynumber?season=" + season + "&number=" + episode;
        String response = getHtml(url);
        return response;
    }

    public static String episodeByDate (String id, String date){
        String url = rootUrl + "/shows/" + id + "/episodesbydate?date=" + date;
        String response = getHtml(url);
        return response;
    }

    public static String showSeasons (String id){
        String url = rootUrl + "/shows/" + id + "/seasons";
        String response = getHtml(url);
        return response;
    }

    public static String seasonEpisodes (String id){
        String url = rootUrl + "/seasons/" + id + "/episodes";
        String response = getHtml(url);
        return response;
    }

    public static String showCast (String id){
        String url = rootUrl + "/shows/" + id + "/cast";
        String response = getHtml(url);
        return response;
    }

    public static String seasonCrew (String id){
        String url = rootUrl + "/shows/" + id + "/crew";
        String response = getHtml(url);
        return response;
    }

    public static String showAkas (String id){
        String url = rootUrl + "/shows/" + id + "/akas";
        String response = getHtml(url);
        return response;
    }

    public static String seasonIndex (){
        String url = rootUrl + "/shows";
        String response = getHtml(url);
        return response;
    }

    public static String seasonIndex (String pageNum){
        String url = rootUrl + "/shows?page=" + pageNum;
        String response = getHtml(url);
        return response;
    }

    public static String personMainInformation (String id){
        String url = rootUrl + "/people/" + id;
        String response = getHtml(url);
        return response;
    }

    public static String personCastCredits (String id){
        String url = rootUrl + "/people/" + id + "/castcredits";
        String response = getHtml(url);
        return response;
    }

    public static String personCrewCredits (String id){
        String url = rootUrl + "/people/" + id + "/crewcredits";
        String response = getHtml(url);
        return response;
    }

    public static String showUpdates (String id){
        String url = rootUrl + "/updates/shows";
        String response = getHtml(url);
        return response;
    }

    public static String nextEpisode (String id){
        String url = rootUrl + "/shows/" + id + "?embed=nextepisode";
        String response = getHtml(url);
        return response;
    }

    private static String getHtml (String url){

        try{
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setRequestMethod("GET");

            conn.setUseCaches(false);

            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            if (cookies != null) {
                for (String cookie : cookies) {
                    conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
                }
            }

            BufferedReader in =
                    new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine).append("\n");
            }
            in.close();

            setCookies(conn.getHeaderFields().get("Set-Cookie"));

            return response.toString();
        }catch (Exception e){
            Log.e("tvmaze",e.toString()+" for "+url);
            e.printStackTrace();
            return "";
        }


    }
    private static void setCookies(List<String> cookiess) {
        cookies = cookiess;
    }

}
