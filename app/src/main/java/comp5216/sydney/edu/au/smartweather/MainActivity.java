package comp5216.sydney.edu.au.smartweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 开启一个子线程，进行网络操作，等待有返回结果，使用handler通知UI
        new Thread(networkTask).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Log.i("mylog", "请求结果为-->" + val);
            // TODO
            // UI界面的更新等相关操作
        }
    };

    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // http request.网络请求相关操作
            try {
                HttpClient_GET("-33.865143", "151.209900");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", "请求结果");
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    public List<LocationsWeather> HttpClient_GET(String latitude, String longitude) throws Exception {

        List<LocationsWeather> list = new LinkedList<LocationsWeather>();
        String path = "https://api.darksky.net/forecast/";
        StringBuilder sb = new StringBuilder(path);
        //key
        sb.append("6a700b02d5c29e9cb8e7b41308c994d1");
        sb.append("/");
        //latitude
        sb.append(latitude);
        sb.append(",");
        //longitude
        sb.append(longitude);
        //units
        sb.append("?units=si");
        System.out.println(sb);

        //1.得到浏览器
        HttpClient httpClient = new DefaultHttpClient();//浏览器
        //2指定请求方式
        HttpGet httpGet = new HttpGet(sb.toString());
        //3.执行请求
        HttpResponse httpResponse = httpClient.execute(httpGet);
        //4 判断请求是否成功
        int status = httpResponse.getStatusLine().getStatusCode();

        if (status == 200) {
            //读取响应内容
            String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
/*            //json处理
            JSONArray jsonArray=new JSONArray(result);
            for(int i=0;i<jsonArray.length();i++){
                HeadNews headnews=new HeadNews();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                headnews.setTitle(jsonObject.getString("title"));
                headnews.setContent(jsonObject.getString("content"));
                headnews.setConut(jsonObject.getString("conut"));
                headnews.setImage(jsonObject.getString("image"));
                list.add(headnews);
            }*/
            Log.d("json", result);
        } else Log.d("json", "Error");
        return list;
    }
}
