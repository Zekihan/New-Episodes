import 'package:new_episodes/network.dart';

class ImdbParser {

  static Future<List<String>> getWatchList(String url) async {

    String listId = await Network.getHtml(url);
    listId = listId.split("pageId")[1].substring(11,22);
    String html = await Network.getHtml("https://www.imdb.com/list/" + listId + "/export");
    List<String> titles = html.split("\n");
    List<String> strings = new List();

    for (int i = 1;i<titles.length;i++) {

      if(titles[i].contains("tvSeries"))
        strings.add(titles[i].split(",")[1]);
      else print(titles[i]);
    }
    titles = strings;
    return titles;
  }
  static Future<String> getTitle(String url,String tt) async {

    String listId = await Network.getHtml(url);
    listId = listId.split("pageId")[1].substring(11,22);
    String html = await Network.getHtml("https://www.imdb.com/list/" + listId + "/export");
    String result = "";
    result = html.split(tt)[1].split(",")[4];

    return result;
  }
  static void getWatchListGenre(String url) async {

    String listId = await Network.getHtml(url);
    listId = listId.split("pageId")[1].substring(11,22);
    String html = await Network.getHtml("https://www.imdb.com/list/" + listId + "/export");
    List<String> genres = [
      "Action",
      "Adventure",
      "Animation",
      "Biography",
      "Comedy",
      "Crime",
      "Documentary",
      "Drama",
      "Family",
      "Fantasy",
      "Game-Show",
      "History",
      "Horror",
      "Music",
      "Musical",
      "Mystery",
      "News",
      "Reality-TV",
      "Romance",
      "Sci-Fi",
      "Short",
      "Sport",
      "Talk-Show",
      "Thriller",
      "War",
      "Western"
    ];
    List<List> likes = new List(genres.length);
    for(int i = 0 ; i < genres.length ; i++){
      if(html.contains(genres[i])){
        likes[i] = [genres[i],(html.split(genres[i]).length-1)];
      }else
        likes[i] = [genres[i],0];
    }
    likes.sort((a, b) => comparatorReverse(a[1],b[1]));
    print(likes);
  }
  static int comparatorReverse(int a,int b){
    int normal = a.compareTo(b);
    if(normal<0)
      return 1;
    else if(normal > 0)
      return -1;
    else
      return 0;
  }

}