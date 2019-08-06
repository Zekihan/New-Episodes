import 'package:new_episodes/network.dart';
import 'dart:convert';

class TVMaze{

  // ignore: non_constant_identifier_names
  static final int TVRAGE  = 0;
  // ignore: non_constant_identifier_names
  static final int THETVDB = 1;
  // ignore: non_constant_identifier_names
  static final int IMDB    = 2;

  static final String _rootUrl = "http://api.tvmaze.com";

  static Future<String> nextEpisode (String id) async {
    String url = _rootUrl + "/shows/" + id + "?embed=nextepisode";
    String response = await Network.getHtml(url);
    return response;
  }
  static Future<String> showLookup(String query,int type) async {
    String url;
    String response;
    switch(type) {
      case 0:
        url = _rootUrl + "/lookup/shows?tvrage=" + query;
        response = await Network.getHtml(url);
        return response;
      case 1:
        url = _rootUrl + "/lookup/shows?thetvdb=" + query;
        response = await Network.getHtml(url);
        return response;
      case 2:
        url = _rootUrl + "/lookup/shows?imdb=" + query;
        response = await Network.getHtml(url);
        return response;
      default:
        response = "No response";
        return response;
    }
  }
  static Future<List<String>> getNextEpisodeByImdbTitle(String query) async {
    try{
      List<String> result = new List<String>();
      String show = await showLookup(query,TVMaze.IMDB);
      var showJson = json.decode(show);
      result.add(showJson["name"].toString());
      String episodeUrl = showJson["_links"]["nextepisode"]["href"];
      String episode = await Network.getHtml(episodeUrl);
      var episodeJson = json.decode(episode);
      result.add(episodeJson["season"].toString());
      result.add(episodeJson["number"].toString());
      result.add(episodeJson["airdate"].toString());
      return result;
    }catch(exception){
      print(exception);
      return new List<String>();
    }

  }
}