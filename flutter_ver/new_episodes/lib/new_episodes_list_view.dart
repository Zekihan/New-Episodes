import 'package:flutter/material.dart';
import 'browser.dart';

class ListOfEpisodes extends StatelessWidget {
  final List<List<String>> newEpisodes;
  ListOfEpisodes(this.newEpisodes);

  Widget _buildProductItem(BuildContext context, int index) {
    return ListTile(
      title: Text(newEpisodes[index][0]),
      subtitle: Text(newEpisodes[index][1]),

      onTap: () {
        if(newEpisodes[index][2]!=null){
          String url = "https://www.imdb.com/title/" + newEpisodes[index][2] + "/episodes?ref_=tt_ov_epl";
          BrowserTab.launchURL(context, url);
        }
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