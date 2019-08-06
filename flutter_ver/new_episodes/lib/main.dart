import 'package:flutter/material.dart';
import 'package:new_episodes/imdb.dart';
import 'package:new_episodes/tvmaze_api.dart';
import 'package:new_episodes/new_episodes_list_view.dart';


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
      home: FirstRoute(),
    );
  }
}

class FirstRoute extends StatelessWidget {
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

  List<List<String>> _newEpisodeList = new List<List<String>>();

  @override
  void initState() {
    super.initState();
  }

  void _test(){
    ImdbParser.getWatchListGenre("https://www.imdb.com/user/ur102224939/watchlist?ref_=nv_wl_all_0");
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
      floatingActionButton: FloatingActionButton(
        onPressed: _test,
        tooltip: 'Increment',
        child: Icon(Icons.add),
      ), // This trailing comma makes auto-formatting nicer for build methods.
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
