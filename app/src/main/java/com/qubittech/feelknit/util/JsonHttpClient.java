package com.qubittech.feelknit.util;

import android.content.Context;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.splunk.mint.Mint;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

public class JsonHttpClient {


    private Context context;

    public JsonHttpClient(Context context) {
        this.context = context;
    }

    public <T> T PostObject(final String httpsURL, final T object, final Class<T> objectClass) {
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(httpsURL);
        try {

            StringEntity stringEntity = new StringEntity(new GsonBuilder().create().toJson(object));
            httpPost.setEntity(stringEntity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Accept-Encoding", "gzip");
            httpPost.setHeader("Authorization", ApplicationHelper.getAuthorizationToken(context));

            HttpResponse httpResponse = defaultHttpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                InputStream inputStream = httpEntity.getContent();
                Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    inputStream = new GZIPInputStream(inputStream);
                }

                String resultString = convertStreamToString(inputStream);
                inputStream.close();
                return new GsonBuilder().create().fromJson(resultString, objectClass);

            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClientProtocolException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public String PostParams(String httpsURL, final List<NameValuePair> params) {

        String returnString = "";
        URLConnection con;
        String inputLine;
        try {
            con = GetUrlConnection(httpsURL);

            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);
//            con.setRequestMethod("GET");
            // Add your data
//            List nameValuePairs = new ArrayList(2);
//            nameValuePairs.add(new BasicNameValuePair("username", "xxx"));
//            nameValuePairs.add(new BasicNameValuePair("password", "xxx123"));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params);

            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", ApplicationHelper.getAuthorizationToken(context));
            OutputStream post = con.getOutputStream();
            entity.writeTo(post);
            post.flush();
            InputStream ins = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(ins);
            BufferedReader in = new BufferedReader(isr);


            while ((inputLine = in.readLine()) != null) {
                returnString += inputLine;
            }

            in.close();
            return returnString;
        } catch (Exception ex) {
            Mint.logException(ex);
        }

        return returnString;
//        HttpResponse response;

//        try {
//            JSONObject jsonObj = new JSONObject();
//
//            for (NameValuePair param : params) {
//                jsonObj.put(param.getName(), param.getValue());
//            }
//
//// Create the POST object and add the parameters
//            HttpPost httpPost = new HttpPost(url);
//            StringEntity entity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
//            entity.setContentType("application/json");
//            httpPost.setHeader("Content-Type", "application/json");
//            httpPost.setHeader("Accept", "application/json");
//            httpPost.setHeader("Authorization", ApplicationHelper.getAuthorizationToken(context));
//            httpPost.setEntity(entity);
//
//            HttpClient client = new DefaultHttpClient();
//
//            try {
//                response = client.execute(httpPost);
//                StatusLine statusLine = response.getStatusLine();
//                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
//                    ByteArrayOutputStream out = new ByteArrayOutputStream();
//                    response.getEntity().writeTo(out);
//                    out.close();
//                    returnString = out.toString();
//                } else {
//                    // Closes the connection.
//                    response.getEntity().getContent().close();
//                    throw new IOException(statusLine.getReasonPhrase());
//                }
//            } catch (Exception e) {
//                Mint.logException(e);
//            }
//        } catch (Exception ex) {
//
//            Mint.logException(ex);
//        }
        //return returnString;
    }

    private URLConnection GetUrlConnection(String url) throws IOException {
        if(url.startsWith("http"))
            return new URL(url).openConnection();

        return (HttpsURLConnection) new URL(url).openConnection();
    }

    public String PostUrlParams(String url, final List<NameValuePair> params) {
        HttpResponse response;
        Log.d("Feelknit", "Url:" + url);
        String returnString = "";
        try {


// Create the POST object and add the parameters
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Authorization", ApplicationHelper.getAuthorizationToken(context));
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpClient client = new DefaultHttpClient();

            try {
                response = client.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    returnString = out.toString();
                } else {
                    // Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (Exception e) {
                Mint.logException(e);
            }
        } catch (Exception ex) {
            Mint.logException(ex);
            ex.printStackTrace();
        }
        return returnString;
    }

    public <T> T PostParams(String url, final List<NameValuePair> params, final Class<T> objectClass) {
        String paramString = URLEncodedUtils.format(params, "utf-8");
        url += "?" + paramString;
        return PostObject(url, null, objectClass);
    }

    private String convertStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        return stringBuilder.toString();
    }

//    public <T> T Get(String url, List<NameValuePair> params, final Class<T> objectClass) {
//        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
//        String paramString = URLEncodedUtils.format(params, "utf-8");
//        url += "?" + paramString;
//        HttpGet httpGet = new HttpGet(url);
//        try {
//
//            httpGet.setHeader("Accept", "application/json");
//            httpGet.setHeader("Accept-Encoding", "gzip");
//
//            HttpResponse httpResponse = defaultHttpClient.execute(httpGet);
//            HttpEntity httpEntity = httpResponse.getEntity();
//            if (httpEntity != null) {
//                InputStream inputStream = httpEntity.getContent();
//                Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");
//                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
//                    inputStream = new GZIPInputStream(inputStream);
//                }
//
//                String resultString = convertStreamToString(inputStream);
//                inputStream.close();
//                return new GsonBuilder().create().fromJson(resultString, objectClass);
//
//            }
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//        return null;
//    }

    public String Get(String url, List<NameValuePair> params) {
        String inputLine, response = "";
        try {
            String paramString = URLEncodedUtils.format(params, "utf-8");
            url += "?" + paramString;
            url = url.replace(" ", "%20");
            URLConnection request = GetUrlConnection(url);
            request.setRequestProperty("Accept", "application/json");
            request.setRequestProperty("Authorization", ApplicationHelper.getAuthorizationToken(context));
            BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                response += inputLine;
            }

            in.close();
        } catch (Exception e) {
            Mint.logException(e);
        }
        return response;
    }
}
