package hioa.mappe2.studybuddy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
 
public class JSONParser 
{
    public JSONObject makeHttpRequest(String url, String method, List<BasicNameValuePair> params) throws IOException
    {
        try 
        {
            if(method == "POST")
            {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = httpClient.execute(httpPost,responseHandler);
                return new JSONObject(responseBody);
            } 
        } 
        catch (UnsupportedEncodingException e) 
        {
            e.printStackTrace();
        } 
        catch (ClientProtocolException e) 
        {
            e.printStackTrace();
        } catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return null;
    }
    
    public JSONObject makeHttpRequestJson(String url, String method, List<JSONObject> json) throws IOException
    {
        try 
        {
            if(method == "POST")
            {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);                
                //StringEntity entity = new StringEntity("json=" + json.toString());
                //httpPost.setEntity(entity);
                //entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httpPost.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
                httpPost.setHeader("json", json.toString());
                //httpPost.setHeader("TEXT", "application/json");
                //httpPost.setEntity(entity);
                
                //ResponseHandler<String> responseHandler = new BasicResponseHandler();
                HttpResponse httpresponse = httpClient.execute(httpPost);
                HttpEntity entity = httpresponse.getEntity();
                
                if (entity != null) 
                {
                    InputStream instream = entity.getContent();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "iso-8859-1"), 8);
        			sb = new StringBuilder();
        			sb.append(reader.readLine() + "\n");
        			String line = "0";
        			
        			while ((line = reader.readLine()) != null) 
        			{
        				sb.append(line + "\n");
        			}
        			
        			instream.close();
        			String result = sb.toString();

        			return new JSONObject(result);
                }
                
                return null;
            } 
        } 
        catch (UnsupportedEncodingException e) 
        {
            e.printStackTrace();
        } 
        catch (ClientProtocolException e) 
        {
            e.printStackTrace();
        } catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return null;
    }
}