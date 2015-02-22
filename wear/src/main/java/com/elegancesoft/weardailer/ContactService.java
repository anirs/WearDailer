package com.elegancesoft.weardailer;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Imran on 2015-02-02.
 */
public class ContactService {
    private static final String TAG = "ContactService";
    private static ContactService instance = null;
    private static HashMap<String,Contact> contacts = new HashMap<String,Contact>();
    //private static String[] contactsAry = {"Imran Arif","Rizwan Arif","Salman Arif","saf Amna","Amna","Khan","Nadeem","Kashif","Ameer Shahib","Raza. Com","Immigatation Consulta","Vasee","Aslam Raja M"};
    private static String[] numberAlphabetAry = {"","","ABC","DEF","GHI","JKL","MNO","PQRS","TUV","WZYZ"};
    private static HashMap<String,ArrayList<String>> searchResults = new HashMap<String,ArrayList<String>>();
    private static Context context;
    static final int READ_BLOCK_SIZE = 100;

    private ContactService(Context context){
        this.context = context;
    }

    public static ContactService getContactsInstance(Context context)
    {
        if(instance == null) {
            instance = new ContactService(context);
        }
        return instance;
    }

    public void loadContacts(){

/*        int i =0;
        for(String contactName : contactsAry){
            contacts.put(String.valueOf(i), new Contact(String.valueOf(i),contactName));
            i++;
        }*/
        //ArrayList<Contact> contacts = new ArrayList<Contact>();
        String jsonContacts = readContactFromFile();
        try {
            JSONObject jObj = new JSONObject(jsonContacts);
            JSONArray jsonContactsArry = jObj.getJSONArray("contacts");
            Log.d(TAG,"json size read"+jsonContactsArry.length());
            for (int j=0; j < jsonContactsArry.length(); j++) {
                   JSONObject jsonContact = jsonContactsArry.getJSONObject(j);
                   Contact contact = new Contact(jsonContact.getString("id"),jsonContact.getString("name"));
                   JSONArray jsonPhoneArry = jsonContact.getJSONArray("phoneList");
                    ContactPhone [] contactPhoneAry = new ContactPhone[jsonContactsArry.length()];
                    for (int k=0; k < jsonContactsArry.length(); k++) {
                        JSONObject jsonPhone = jsonContactsArry.getJSONObject(k);
                        String type = "";
                        String num = "";
                        try {
                             type = jsonPhone.getString("type");
                             num = jsonPhone.getString("num");
                        }catch (JSONException e)
                        {
                               Log.d(TAG,"id:Name = "+jsonContact.getString("id")+""+jsonContact.getString("name"));
                        }
                        ContactPhone contactPhone = new ContactPhone(type,num);
                        contactPhoneAry[k] = contactPhone;
                    }
                    contact.setContactPhones(contactPhoneAry);
                contacts.put(jsonContact.getString("id"),contact);
            }

            Log.d(TAG,"load Contacts size:"+contacts.size());

        } catch (JSONException e) {
            Log.d(TAG,e.getMessage());
            e.printStackTrace();
        }


    }

    public String readContactFromFile()
    {
        String s="";
        //reading text from file
        try {
            FileInputStream fileIn=context.openFileInput("contacts.json");
            InputStreamReader InputRead= new InputStreamReader(fileIn);

            char[] inputBuffer= new char[READ_BLOCK_SIZE];

            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;
            }
            InputRead.close();
            Log.d(TAG,"file load"+s);
            //Toast.makeText(getBaseContext(), s,Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.d(TAG,"file load"+e.getMessage());
            e.printStackTrace();
        }
        return s;
    }


    public void saveContacts(String contacts){
        String filename = "contacts.json";
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(contacts.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String,ArrayList<String>> searchContact(String searchNumbers){

        char [] numbersAry = searchNumbers.toCharArray();
        char [] alphabetAry = null;

        searchResults.put("",  new ArrayList<String>( Arrays.asList(contacts.keySet().toArray(new String[0]))));

        for(char number : numbersAry){

            alphabetAry = numberAlphabetAry[Character.getNumericValue(number)].toCharArray();
            HashMap<String,ArrayList<String>> searchResultsTemp = new HashMap<String,ArrayList<String>>();

            for(String searchkey:searchResults.keySet()) {
                for(char c : alphabetAry){
                    ArrayList<String> searchIdAry = new ArrayList<String>();
                    for(String id: searchResults.get(searchkey)){
                        String [] fullname =  contacts.get(id).getName().toUpperCase().split(" ");
                        for(String name : fullname){
                            if(name.startsWith(new String(searchkey+c).toUpperCase())){
                                searchIdAry.add(id);
                                break;
                            }
                        }
                    }
                    if(searchIdAry.size()>0)
                        searchResultsTemp.put(searchkey+c, searchIdAry);
                }
            }
            //searchResults.clear();
            searchResults = (HashMap<String, ArrayList<String>>) searchResultsTemp.clone();
        }

        return searchResults;
    }

    public void printSearchContacts(HashMap<String,ArrayList<String>> searchResults){

        for(String searchKey : searchResults.keySet()){
            Log.d(TAG,"\n ["+searchKey+"]  ");
            for(String contactId:searchResults.get(searchKey))
                //if(contacts.get(contactId).getName().toUpperCase().indexOf(searchKey)== 0)
                    Log.d(TAG, "" + contacts.get(contactId).getName() + ",");

        }
/*        for(String searchKey : searchResults.keySet()){
            System.out.print("\n ["+searchKey+"]  ");
            for(String contactId:searchResults.get(searchKey))
                if(contacts.get(contactId).getName().toUpperCase().indexOf(searchKey)> 0)
                    System.out.print(""+contacts.get(contactId).getName()+",");

        }*/

    }

    public Contact getContact(String contactId){
        return contacts.get(contactId);
    }

    public static String setSearchContactColor(String contactName,String searchKey){
        Log.d(TAG, " Contact Name : " + contactName + " Key :"+searchKey);
        int index = contactName.toUpperCase().indexOf(searchKey.toUpperCase());
        searchKey = contactName.substring(index,searchKey.length());
        Log.d(TAG, " index 2: " + index + " Key 2:"+searchKey);
        contactName = contactName.replace(searchKey,"<font color=\"red\">"+searchKey+"</font>");
        Log.d(TAG, " Contact Name : " + contactName);
        return contactName;
    }
}
