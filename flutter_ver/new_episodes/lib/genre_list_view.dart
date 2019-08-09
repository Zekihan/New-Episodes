import 'package:flutter/material.dart';
import 'browser.dart';

class Genres extends StatelessWidget {
  final List<List> newEpisodes;
  Genres(this.newEpisodes);

  Widget _buildProductItem(BuildContext context, int index) {
    return ListTile(
      title: Text(newEpisodes[index][0].toString()),
      subtitle: Text(newEpisodes[index][1].toString()),

      onTap: () {
        String url = "https://www.imdb.com/search/title/?genres=" + newEpisodes[index][0];
        BrowserTab.launchURL(context, url);
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return ListView.separated(
      itemBuilder: _buildProductItem,
      itemCount: newEpisodes.length,
      separatorBuilder: (context, index) {
        return Divider();
      },
    );
  }
}