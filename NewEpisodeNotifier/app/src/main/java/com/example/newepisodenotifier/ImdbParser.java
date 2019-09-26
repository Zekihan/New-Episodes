package com.example.newepisodenotifier;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ImdbParser {

    private static List<String> cookies;

    private static final String USER_AGENT = "Mozilla/5.0";


    public static String[] getWatchList(String url){

        String listId = getHtml(url).split("pageId")[1].substring(11,22);
        String html = getHtml("https://www.imdb.com/list/" + listId + "/export");
        String[] titles = html.split("\n");
        List<String> strings = new ArrayList<>();

        for (int i = 1;i<titles.length;i++) {

            if(titles[i].contains("tvSeries"))
                strings.add(titles[i].split(",")[1]);
            else Log.e("WatchList/NotTvSeries", titles[i]);
        }
        titles = strings.toArray(new String[0]);
        return titles;
    }

    public static List<String> getLastSeasonEpisodeList(String url){

        String episodeUrl = url + "/episodes?ref_=tt_ov_epl";
        List<String> list = new ArrayList<>();

        try {
            String s = getHtml(episodeUrl);
            list.add(s.split("Season")[1].split("title")[7].split("' content=\"")[1].split(" \\(")[0]);
            list.add(s.split("Season")[1].substring(1,2));
            s = s.split("Episode List")[1];
            //Log.e("Parse",s);
            String reg = "<div class=\"airdate\">\n {12}";
            //Log.e("Parse",s.contains(reg)+"");
            String[] ss = s.split(reg);
            //Log.e("Parse",s);
            for (int i = 1; i < ss.length; i++) {
                list.add(ss[i].split("\n {4}</div>")[0]);
            }
            //Log.e("Parse",list.toString());
            return list;
        } catch (Exception e) {
            list = new ArrayList<>();
            String s = getHtml(episodeUrl);
            String[] ss = s.split("Season")[3].split(" </select>")[0].split("<option");
            for (int i = 1; i < ss.length; i++) {
                ss[i] = ss[i].split(">\n" +
                        " {8}")[1].split("\n" +
                        " {6}</option>")[0];
            }
            if(ss[ss.length-1].equals("Unknown")){
                list = getEpisodeListBySeason(url,ss[ss.length-2]);
            }else {
                Log.e("Parser",e.toString()+" for "+url);
                e.printStackTrace();
            }
            return list;
        }
    }
    private static List<String> getEpisodeListBySeason(String url, String season){
        url += "/episodes?season="+season;
        List<String> list = new ArrayList<>();

        String s = getHtml(url);
        list.add(s.split("Season")[1].split("title")[7].split("' content=\"")[1].split(" \\(")[0]);
        list.add(s.split("Season")[1].substring(1,2));
        s = s.split("Episode List")[1];
        //Log.e("Parse",s);
        String reg = "<div class=\"airdate\">\n {12}";
        //Log.e("Parse",s.contains(reg)+"");
        String[] ss = s.split(reg);
        //Log.e("Parse",s);
        for (int i = 1; i < ss.length; i++) {
            list.add(ss[i].split("\n {4}</div>")[0]);
        }
        //Log.e("Parse",list.toString());


        return list;
    }

    private static String getHtml (String url){

        try{
            URL obj = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

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
            Log.e("Parser",e.toString()+" for "+url);
            e.printStackTrace();
            return "";
        }


    }
    private static void setCookies(List<String> cookiess) {
        cookies = cookiess;
    }
}
