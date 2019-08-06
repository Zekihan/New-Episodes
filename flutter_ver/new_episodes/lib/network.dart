

import 'package:http/http.dart' as http;

class Network {
  static Future<String> getHtml(String url) async {

    final response = await http.get(url);
    return response.body.toString();
  }

}