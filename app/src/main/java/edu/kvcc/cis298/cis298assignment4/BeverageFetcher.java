package edu.kvcc.cis298.cis298assignment4;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/** * Created by dpantaleo on 12/8/2015. */
        public class BeverageFetcher {                        //pg 410 ch 23
    private static final String TAG = "Beverage Fragment";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        " with" + urlSpec);                 // check for io error
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);          // write bytes out to o/put stream
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));    //getUrlSpring method chg's to bytes to string
    }


    public List<Beverage> fetchBeverages() {
        List<Beverage> beverages = new ArrayList<>();

        try {
            String url = Uri.parse("http://barnesbrothers.homeserver.com/beverageapi")
                    .buildUpon()
                    .build().toString();
            String jsonString = getUrlString(url);
            JSONArray jsonArray = new JSONArray(jsonString);
            parseBeverage(beverages, jsonArray);
            Log.i(TAG, "Received JSON:" + jsonString);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to Parse Json", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed for Fetch Items from json: " + ioe);
        }
        return beverages;
    }

    // *********************************** ADD CONNECTION TO ADDRESS BOOK TO ACQUIRE EMAIL NAME AND ADDRESS *************
 /*  public ArrayList<String> getNameEmailDetails() {
       ArrayList<String> names = new ArrayList<String>();
       ContentResolver cr = getContentResolver();
       Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
       if (cur.getCount()> 0 ) {
           while (cur.moveToNext()){
               String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
               Cursor cur1 = cr.query(
                       ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,
                       ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id},null);
               while (cur1.moveToNext()) {
                   String name=cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                   Log.e("Name : ", name);
                   String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                   Log.e("Email : ", email);
                   if (email != null){
                       names.add(name);
                   }
               }
               cur1.close();
           }
       }
       return names;
   }
    */



    private void parseBeverage(List<Beverage> beverages, JSONArray jsonArray)
            throws IOException, JSONException {

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject beverageJsonObject = jsonArray.getJSONObject(i);

            String idString = beverageJsonObject.getString("id");
            //     String idForNewBeverage = idString.toString();

            // DID NOT PUT IN ARG...NO CONSTRUCTOR FOR IT BUT NEED TO SET ALL VALUES ANYWAY..I THINK??
            Beverage beverage = new Beverage();    // INSTANTIATE EMPTY CONSTRUCTOR..THEN SET ALL PROPERTIES

            beverage.setId(idString);                                   // SET ALL VALUES TO OBJECT FROM JSON DB
            beverage.setName(beverageJsonObject.getString("name"));
            beverage.setPack(beverageJsonObject.getString("pack"));

            String priceString = beverageJsonObject.getString("price");  // PRICE CONVERTED TO DOUBLE
            double priceForNewBeverage = Double.parseDouble(priceString);
            beverage.setPrice(priceForNewBeverage);

            String activeString = beverageJsonObject.getString("isActive");  // IS ACTIVE CONVERTED TO BOOLEAN
            if (activeString == "1")
                beverage.setActive(true);
            else
                beverage.setActive(false);
            beverages.add(beverage);
        }
    }
};