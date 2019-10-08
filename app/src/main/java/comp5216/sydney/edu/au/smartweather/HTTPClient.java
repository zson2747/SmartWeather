package comp5216.sydney.edu.au.smartweather;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.LinkedList;
import java.util.List;

public class HTTPClient {

    /**
     * 天气API连接
     *
     * @param latitude
     * @param longitude
     * @param KEY
     * @return
     * @throws Exception
     */
    public String HttpClient_GET(String latitude, String longitude, String KEY) throws Exception {
        String result = null;
        List<LocationsWeather> list = new LinkedList<LocationsWeather>();
        String path = "https://api.darksky.net/forecast/";
        StringBuilder sb = new StringBuilder(path);
        //key
        sb.append(KEY);
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
            result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        } else Log.d("json", "Error");
        return result;
    }
}
