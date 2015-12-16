package edu.kvcc.cis298.cis298assignment4;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;


import java.util.ArrayList;

/**
 * Created by David Barnes on 11/3/2015.
 */
public class BeverageFragment extends Fragment {

    //String key that will be used to send data between fragments
    private static final String ARG_BEVERAGE_ID = "crime_id";

    //private class level vars for the model properties
    private EditText mId;
    private EditText mName;
    private EditText mPack;
    private EditText mPrice;
    private CheckBox mActive;

    private Button mEmailButton;                // ch 15 - asmt 4 CLASS LEVEL EMAIL BUTTON
    private Button mSelectContactButton;

    private static final int REQUEST_CONTACT = 1;   // ch 15 - asmt 4 ASKING FOR A CONTACT

    //Private var for storing the beverage that will be displayed with this fragment
    private Beverage mBeverage;

    //Public method to get a properly formatted version of this fragment
    public static BeverageFragment newInstance(String id) {
        //Make a bungle for fragment args
        Bundle args = new Bundle();
        //Put the args using the key defined above
        args.putString(ARG_BEVERAGE_ID, id);

        //Make the new fragment, attach the args, and return the fragment
        BeverageFragment fragment = new BeverageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //When created, get the beverage id from the fragment args.
        String beverageId = getArguments().getString(ARG_BEVERAGE_ID);
        //use the id to get the beverage from the singleton
        mBeverage = BeverageCollection.get(getActivity()).getBeverage(beverageId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Use the inflator to get the view from the layout
        View view = inflater.inflate(R.layout.fragment_beverage, container, false);

        //Get handles to the widget controls in the view
        mId = (EditText) view.findViewById(R.id.beverage_id);
        mName = (EditText) view.findViewById(R.id.beverage_name);
        mPack = (EditText) view.findViewById(R.id.beverage_pack);
        mPrice = (EditText) view.findViewById(R.id.beverage_price);
        mActive = (CheckBox) view.findViewById(R.id.beverage_active);

        //              ***********************************************************************************
        mEmailButton = (Button) view.findViewById(R.id.send_email_button);    // Ch 15 ADDED W Asmt 4 EMAIL BUTTON W/ INTENT USAGE
        mEmailButton.setOnClickListener(new View.OnClickListener() {            // SET UP LISTENER
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);                   //INTENT!
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getBeverageReport());
           //     i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_salutation));
                i = Intent.createChooser(i, getString(R.string.email_chooser_message));
                startActivity(i);
            }
         });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSelectContactButton = (Button) view.findViewById(R.id.select_contact_button);
        mSelectContactButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (mBeverage.getId() != null){
            mSelectContactButton.setText(mBeverage.getName());   // ******* NEED SOMETHING ELSE HERE !!!!!!!!!!
        }

        //Set the widgets to the properties of the beverage
        mId.setText(mBeverage.getId());
        mId.setEnabled(false);
        mName.setText(mBeverage.getName());
        mPack.setText(mBeverage.getPack());
        mPrice.setText(Double.toString(mBeverage.getPrice()));
        mActive.setChecked(mBeverage.isActive());



        //Text changed listenter for the id. It will not be used since the id will be always be disabled.
        //It can be used later if we want to be able to edit the id.
        mId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setId(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Text listener for the name. Updates the model as the name is changed
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Text listener for the Pack. Updates the model as the text is changed
        mPack.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setPack(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Text listener for the price. Updates the model as the text is typed.
        mPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //If the count of characters is greater than 0, we will update the model with the
                //parsed number that is input.
                if (count > 0) {
                    mBeverage.setPrice(Double.parseDouble(s.toString()));
                //else there is no text in the box and therefore can't be parsed. Just set the price to zero.
                } else {
                    mBeverage.setPrice(0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Set a checked changed listener on the checkbox
        mActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBeverage.setActive(isChecked);
            }
        });

        //Lastley return the view with all of this stuff attached and set on it.
        return view;
    }

    // ADDED WITH ASMT 4:CREATE REPORT STRING & RETURN THE REPORT USING THE STRINGS:

    private String getBeverageReport() {           // USING PG 279...DON'T THINK I NEED AN IF - CHECK W/ EMAIL WORK

        String FromEmail = getString(R.string.email_from_address, "From Bob's email");   // NEED TO GET THE NAME & PROBABOLY THE ADDRESS TOO!
        String ToEmail = getString(R.string.email_to_address,"From Dawns Email");
        String Active;
        if (mBeverage.isActive())
            Active = "Currently Active";
                else
             Active = "Not Currently Active";

//The Message:   - like required....
        String report = getString(R.string.email_body,
                                            "Contact name",
                                            mBeverage.getId(),
                                            mBeverage.getName(),
                                            mBeverage.getPack(),
                                            mBeverage.getPrice(),
                                           Active);
        return report;
    }


    // *********************************** ADD CONNECTION TO ADDRESS BOOK TO ACQUIRE EMAIL NAME AND ADDRESS *************
   public ArrayList<String> getNameEmailDetails() {
       ArrayList<String> names = new ArrayList<String>();

       ContentResolver cr = getContentResolver();//query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
       Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
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

    private ContentResolver getContentResolver() {
        return null;
    }


}
