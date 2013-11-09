package com.example.prayerbox;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

public class LoginPageActivity extends ListActivity {
	public ArrayList<String> your_array_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
      
        new GetData().execute(""); 
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_page, menu);
        return true;
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Prayer item = (Prayer) getListAdapter().getItem(position);
        String request = item.request;
        String subject = item.subject;
        Intent nextScreen = new Intent(getApplicationContext(), PrayerDetailsActivity.class);
        
        //Sending data to another Activity
        nextScreen.putExtra("subject", subject);
        nextScreen.putExtra("request", request);

        startActivity(nextScreen);
    }
    
    private class GetData extends AsyncTask<String, Void, String>{
    	private String result;
    	@Override
    	protected String doInBackground(String... params){
    	    HttpPost httpMethod = new HttpPost("http://www.uwccf.ca/prayerbox/api/prayerproxy.php");
            
            DefaultHttpClient client =  new DefaultHttpClient();
            result = null;
            try {
                HttpResponse response = client.execute(httpMethod);

                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);
                return result;
                } catch (Exception e) {
                e.printStackTrace();
            }
			return null;
    	}
    @Override
    protected void onPostExecute(String result) {
        PrayerParser pray_parser = new PrayerParser(result);
        ArrayList<Prayer> prayer_list = pray_parser.parse();
        PrayerAdapter prayerAdapter = new PrayerAdapter(LoginPageActivity.this,prayer_list);
        setListAdapter(prayerAdapter);
    }
 }
}