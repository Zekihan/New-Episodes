package com.example.newepisodenotifier;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private MonthlyMenuCustomAdapter adapter;
    private Calendar calendar;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final List<String[]> listOfTitles = new ArrayList<>();
        final List<String[]> progress = new ArrayList<>();



        calendar = Calendar.getInstance();

        final RecyclerView recyclerView = findViewById(R.id.rv);
        adapter = new MonthlyMenuCustomAdapter(progress);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try{
                    TextView tv = recyclerView.getChildAt(position).findViewById(R.id.urlInfo);
                    String s = (tv.getText().toString());
                    if(!s.equals("")){
                        chromeTab("https://www.imdb.com/title/"+s+"/episodes?ref_=tt_ov_epl");
                    }
                }catch (Exception e){
                    Log.e("tag",e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onLongClick(View view, int position) { }

        }));
        String[] x = new String[3];
        x[0] = "Progress "+"0/??";
        x[1] = getStringFromCalender(calendar);
        x[2] = "";
        progress.add(x);
        adapter.notifyDataSetChanged();

        final Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                long start = Calendar.getInstance().getTimeInMillis();
                final String[] titles = ImdbParser.getWatchList("https://www.imdb.com/user/ur102224939/watchlist?ref_=nv_wl_all_0");
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        String[] x = new String[3];
                        x[0] = "Progress "+"0"+"/"+titles.length;
                        x[1] = getStringFromCalender(calendar);
                        x[2] = "";
                        progress.remove(0);
                        progress.add(x);
                        adapter.notifyDataSetChanged();
                    }
                });
                int count = 1;
                for (String s:titles) {
                    final String url = "https://www.imdb.com/title/" + s;
                    List<String> list = ImdbParser.getLastSeasonEpisodeList(url);
                    if (!list.isEmpty()){
                        String[] x = new String[3];
                        x[1] = getNearestFutureDate(list.subList(2,list.size()));
                        if(calendar.getTimeInMillis()<=getCalenderFromString(x[1]).getTimeInMillis()){
                            x[0] = list.get(0)+" S"+list.get(1)+"E"+getEpisode(list,x[1]);
                            x[2] = s;
                            listOfTitles.add(x);
                        }
                    }
                    final int finalCount = count;
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if(finalCount == titles.length){
                                progress.clear();
                                progress.addAll(listOfTitles);
                                adapter.notifyDataSetChanged();
                            }else {
                                String[] x = new String[3];
                                x[0] = "Progress "+finalCount +"/"+titles.length;
                                x[1] = getStringFromCalender(calendar);
                                x[2] = "";
                                progress.clear();
                                progress.add(x);
                                progress.addAll(listOfTitles);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                    count++;
                }
                Log.e("timer","took :"+(Calendar.getInstance().getTimeInMillis()-start));
            }
        });
        //t1.start();
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                long start = Calendar.getInstance().getTimeInMillis();
                final String[] titles = ImdbParser.getWatchList("https://www.imdb.com/user/ur102224939/watchlist?ref_=nv_wl_all_0");
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        String[] x = new String[3];
                        x[0] = "Progress "+"0"+"/"+titles.length;
                        x[1] = getStringFromCalender(calendar);
                        x[2] = "";
                        progress.remove(0);
                        progress.add(x);
                        adapter.notifyDataSetChanged();
                    }
                });
                int count = 1;
                for (String s:titles
                ) {
                    List<String> list = TVMaze.getNextEpisodeByImdbTitle(s);
                    if (!list.isEmpty()){
                        String[] x = new String[3];
                        x[1] = list.get(3);
                        Log.e("tag",x[1]);
                        x[0] = list.get(0)+" S"+list.get(1)+"E"+list.get(2);
                        x[2] = s;
                        listOfTitles.add(x);
                    }
                    final int finalCount = count;
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if(finalCount == titles.length){
                                progress.clear();
                                progress.addAll(listOfTitles);
                                Collections.sort(progress, new Comparator<String[]>() {
                                    @Override
                                    public int compare(String[] o1, String[] o2) {
                                        Calendar c1 = getString2Calender(o1[1]);
                                        Calendar c2 = getString2Calender(o2[1]);
                                        return c1.compareTo(c2);
                                    }
                                });
                                adapter.notifyDataSetChanged();
                            }else {
                                String[] x = new String[3];
                                x[0] = "Progress "+finalCount +"/"+titles.length;
                                x[1] = getStringFromCalender(calendar);
                                x[2] = "";
                                progress.clear();
                                progress.add(x);
                                progress.addAll(listOfTitles);
                                Collections.sort(progress, new Comparator<String[]>() {
                                    @Override
                                    public int compare(String[] o1, String[] o2) {
                                        Calendar c1 = getString2Calender(o1[1]);
                                        Calendar c2 = getString2Calender(o2[1]);
                                        return c1.compareTo(c2);
                                    }
                                });
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                    count++;
                }
                Log.e("timer","took :"+(Calendar.getInstance().getTimeInMillis()-start));
            }
        });
        t.start();
    }

    private String getEpisode(List<String> episodeList,String date){
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < episodeList.size(); i++) {
            if(episodeList.get(i).equals(date)){
                s.append(i).append("&");
            }
        }
        return s.substring(0,s.length()-1);
    }

    private String getNearestFutureDate(List<String> list){
        Calendar c = Calendar.getInstance();
        for (int i = 0; i < list.size(); i++) {
            if(c.compareTo(getCalenderFromString(list.get(i)))<=0){
                return list.get(i);
            }
        }
        c = getCalenderFromString(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            if(c.compareTo(getCalenderFromString(list.get(i)))<=0){
                c = getCalenderFromString(list.get(i));
            }
        }
        return getStringFromCalender(c);
    }

    private Calendar getString2Calender(String date){
        try {
            Calendar result = Calendar.getInstance();
            String[] dates = date.split("-");
            int year = Integer.parseInt(dates[0]);
            int month = Integer.parseInt(dates[1]);
            int day = Integer.parseInt(dates[2]);
            result.set(year,month,day,0,0,0);
            return result;
        }catch (NumberFormatException e){
            Log.e("Parser",e.toString()+" for "+date);
            return Calendar.getInstance();
        }
    }

    private String getStringFromCalender(Calendar date){

        String s;

        switch (date.get(Calendar.MONTH)) {
            case 0 :
                s = date.get(Calendar.DATE)+" "+"Jan."+" "+date.get(Calendar.YEAR);
                break;

            case 1 :
                s = date.get(Calendar.DATE)+" "+"Feb."+" "+date.get(Calendar.YEAR);
                break;

            case 2 :
                s = date.get(Calendar.DATE)+" "+"Mar."+" "+date.get(Calendar.YEAR);
                break;

            case 3 :
                s = date.get(Calendar.DATE)+" "+"Apr."+" "+date.get(Calendar.YEAR);
                break;

            case 4 :
                s = date.get(Calendar.DATE)+" "+"May "+" "+date.get(Calendar.YEAR);
                break;

            case 5 :
                s = date.get(Calendar.DATE)+" "+"Jun."+" "+date.get(Calendar.YEAR);
                break;

            case 6 :
                s = date.get(Calendar.DATE)+" "+"Jul."+" "+date.get(Calendar.YEAR);
                break;

            case 7 :
                s = date.get(Calendar.DATE)+" "+"Aug."+" "+date.get(Calendar.YEAR);
                break;

            case 8 :
                s = date.get(Calendar.DATE)+" "+"Sep."+" "+date.get(Calendar.YEAR);
                break;

            case 9 :
                s = date.get(Calendar.DATE)+" "+"Oct."+" "+date.get(Calendar.YEAR);
                break;

            case 10 :
                s = date.get(Calendar.DATE)+" "+"Nov."+" "+date.get(Calendar.YEAR);
                break;

            case 11 :
                s = date.get(Calendar.DATE)+" "+"Dec."+" "+date.get(Calendar.YEAR);
                break;

            default :
                s = date.get(Calendar.DATE)+" "+"Jan."+" "+date.get(Calendar.YEAR);
                break;
        }
        return s;
    }
    private Calendar getCalenderFromString(String date){

        try{
            date = date.trim().replaceAll("\\s{2,}", " ");

            if(date.split(" ").length<2){
                Calendar c = Calendar.getInstance();
                c.set(Integer.parseInt(date),0,0);
                return c;
            }

            int year = Integer.parseInt(date.split(" ")[2]);
            int day = Integer.parseInt(date.split(" ")[0]);
            String month = date.split(" ")[1];
            Calendar c = Calendar.getInstance();
            switch (month) {
                case "Jan." :
                    c.set(year,0,day);
                    break;

                case "Feb." :
                    c.set(year,1,day);
                    break;

                case "Mar." :
                    c.set(year,2,day);
                    break;

                case "Apr." :
                    c.set(year,3,day);
                    break;

                case "May" :
                    c.set(year,4,day);
                    break;

                case "Jun." :
                    c.set(year,5,day);
                    break;

                case "Jul." :
                    c.set(year,6,day);
                    break;

                case "Aug." :
                    c.set(year,7,day);
                    break;

                case "Sep." :
                    c.set(year,8,day);
                    break;

                case "Oct." :
                    c.set(year,9,day);
                    break;

                case "Nov." :
                    c.set(year,10,day);
                    break;

                case "Dec." :
                    c.set(year,11,day);
                    break;

                default :
                    //Log.e("Main",date);
                    c.set(year,0,day);
                    break;
            }
            //Log.e("Main",getStringFromCalender(c));
            return c;
        }catch (Exception e){
            Log.e("Main:CalenderFromString",e.getMessage()+" for "+date);
            Calendar c = Calendar.getInstance();
            c.set(c.get(Calendar.YEAR),0,0);
            return c;
        }
    }

    private void chromeTab(String url){
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setStartAnimations(this,R.anim.slide_in_right , R.anim.slide_out_left);
        intentBuilder.setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_in_left);
        intentBuilder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        intentBuilder.addDefaultShareMenuItem();
        CustomTabsIntent customTabsIntent = intentBuilder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

}

