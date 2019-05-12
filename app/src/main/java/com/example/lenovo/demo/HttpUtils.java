//package com.example.lenovo.demo;
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.List;
// interface Tools {
//    void onSuccess(List list);
//    void onFail();
//    List handleJosnDataToList(String josnData);
//}
//public class HttpUtils<T> {
//    private List<T> mlist;
//    private String urlString;
//    private  Tools listener;
//
//    public HttpUtils(List<T> mlist, String url, Tools listener) {
//        this.mlist = mlist;
//        this.urlString = url;
//        this.listener = listener;
//    }
//
//    public  String getJosnData() {
////这里可以加一个判断网络，如果无网络直接用Onfail方法
//
//        String JosnString = "";
//        HttpURLConnection connection = null;
//        BufferedReader reader = null;
//        try {
//            URL url = new URL(urlString);
//            connection= (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setConnectTimeout(8000);
//            connection.setReadTimeout(8000);
//           connection.setDoInput(true);
//           connection.setDoOutput(true);
//            InputStream in = connection.getInputStream();
//            reader = new BufferedReader(new InputStreamReader(in));
//            StringBuilder response = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                response.append(line);
//            }
//            JosnString = response.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return JosnString;
//    }
//
//
//    public void    getTargetList () {
//        String josnData=getJosnData();
//     if (josnData == ""){
//         listener.onFail();
//     return;
//     }
//        mlist=listener.handleJosnDataToList(josnData);
//        listener.onSuccess(mlist);//在重写这个方法时把mlist赋值给外层传入的list
//    }
// }
