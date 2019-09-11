import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:new_episodes/imdb.dart';
import 'package:new_episodes/tvmaze_api.dart';
import 'package:new_episodes/new_episodes_list_view.dart';
import 'package:new_episodes/genre_list_view.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';


void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'New Episodes',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: FirstRoutePage(),
    );
  }
}

class FirstRoutePage extends StatefulWidget {

  @override
  _FirstRoutePageState createState() => _FirstRoutePageState();
}

class _FirstRoutePageState extends State<FirstRoutePage> {

  FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin;

  @override
  initState() {
    super.initState();

    var initializationSettingsAndroid =
    new AndroidInitializationSettings('@mipmap/ic_launcher');
    var initializationSettingsIOS = new IOSInitializationSettings();
    var initializationSettings = new InitializationSettings(
        initializationSettingsAndroid, initializationSettingsIOS);
    flutterLocalNotificationsPlugin = new FlutterLocalNotificationsPlugin();
    flutterLocalNotificationsPlugin.initialize(initializationSettings,
        onSelectNotification: onSelectNotification);
  }

  Future onSelectNotification(String payload) async {
    showDialog(
      context: context,
      builder: (_) {
        return new AlertDialog(
          title: Text("PayLoad"),
          content: Text("Payload : $payload"),
        );
      },
    );
  }

  Future _showNotification() async {
    var androidPlatformChannelSpecifics = new AndroidNotificationDetails(
        '0', 'your channel name', 'your channel description',
        importance: Importance.Max, priority: Priority.High);
    var iOSPlatformChannelSpecifics = new IOSNotificationDetails();
    var platformChannelSpecifics = new NotificationDetails(
        androidPlatformChannelSpecifics, iOSPlatformChannelSpecifics);
    await flutterLocalNotificationsPlugin.show(
      0,
      'New Post',
      'How to Show Notification in Flutter',
      platformChannelSpecifics,
      payload: 'Default_Sound',
    );
  }
  Future<void> _scheduleNotification() async {
    var scheduledNotificationDateTime =
    DateTime.now().add(Duration(minutes: 15));

    var androidPlatformChannelSpecifics = new AndroidNotificationDetails(
        '0', 'your channel name', 'your channel description',
        playSound: false,
        importance: Importance.Max, priority: Priority.High);
    var iOSPlatformChannelSpecifics = new IOSNotificationDetails();
    var platformChannelSpecifics = new NotificationDetails(
        androidPlatformChannelSpecifics, iOSPlatformChannelSpecifics);
    await flutterLocalNotificationsPlugin.show(
      0,
      'New Post',
      'How to Show Notification in Flutter',
      platformChannelSpecifics,
    );

    await flutterLocalNotificationsPlugin.schedule(
        0,
        'scheduled title',
        'scheduled body',
        scheduledNotificationDateTime,
        platformChannelSpecifics);
  }



  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Tv Shows'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            RaisedButton(
              child: Text('New Episodes'),
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => MyHomePage(title: 'New Episodes')),
                );
              },
            ),
            RaisedButton(
              child: Text('Recommendation'),
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => RecommendationPage(title: 'New Episodes')),
                );
              },
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          _showNotification();
          _scheduleNotification();
        },
        child: Icon(Icons.thumb_up),
        backgroundColor: Colors.pink,
      ),
    );
  }

}

class RecommendationPage extends StatefulWidget {
  RecommendationPage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _RecommendationPageState createState() => _RecommendationPageState();
}
class _RecommendationPageState extends State<RecommendationPage> {

  List<List> _likes = new List<List<String>>();

  @override
  void initState() {
    super.initState();
    _test();
  }

  Future _test() async {
    setState(() {
      _likes = _likes;
    });
    _likes = await ImdbParser.getWatchListGenre("https://www.imdb.com/user/ur102224939/watchlist?ref_=nv_wl_all_0");
    setState(() {
      _likes = _likes;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Expanded(child: Genres(_likes)),
          ],
        ),
      ),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);
  final String title;
  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _progress = 0;
  int _itemCount = 0;
  List<List<String>> _newEpisodeList = new List<List<String>>();

  void _sortShows(bool includeFirst){
    setState(() {
      if (includeFirst)
      _newEpisodeList.sort((a, b) => DateTime.parse(a[1]).compareTo(DateTime.parse(b[1])));
      else{
        _newEpisodeList.setRange(1, _newEpisodeList.length, _newEpisodeList.sublist(1, _newEpisodeList.length)..sort(
                (a, b) => DateTime.parse(a[1]).compareTo(DateTime.parse(b[1]))
        ));
      }

    });
  }

  Future<List<List<String>>> getNewEpisodes() async {
    String url = "https://www.imdb.com/user/ur102224939/watchlist?ref_=nv_wl_all_0";
    List<String> titles = await ImdbParser.getWatchList(url);
    setState(() {
      _itemCount = titles.length;
      _progress = 0;
      updateProgress();
    });
    for (int i = 0; i < titles.length; i++) {
      String s = titles[i];
      List<String> list = await TVMaze.getNextEpisodeByImdbTitle(s);
      if (list.isNotEmpty){
        List<String> x = new List<String>(3);
        x[1] = list[3];
        if(list[1].length == 1){
          list[1] = "0"+list[1];
        }
        if(list[2].length == 1){
          list[2] = "0"+list[2 ];
        }
        x[0] = list[0]+" S"+list[1]+"E"+list[2];
        x[2] = s;
        setState(() {
          _newEpisodeList.add(x);
        });
      }
      setState(() {
        _progress++;
        updateProgress();
      });
    }
    setState(() {
      _newEpisodeList.removeAt(0);
      _sortShows(true);
    });
    return _newEpisodeList;
  }

  @override
  void initState() {
    super.initState();
    List<String> x = new List<String>();
    x.add("dummy");
    x.add("Today");
    x.add(null);
    _newEpisodeList.add(x);
    updateProgress();
    getNewEpisodes();
  }
  void updateProgress(){
    if(_itemCount != 0){
      _newEpisodeList[0][0] = _progress.toString() + "/" + _itemCount.toString();
    }else  _newEpisodeList[0][0] = _progress.toString() + "/??";
    _sortShows(false);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Expanded(child: ListOfEpisodes(_newEpisodeList)),
          ],
        ),
      ),
    );
  }
}
