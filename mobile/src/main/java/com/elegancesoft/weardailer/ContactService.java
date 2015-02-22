package com.elegancesoft.weardailer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Imran on 2015-02-05.
 */
public class ContactService {
    private static final String APPLICATION_PATH = "/elegancesoft/weardailer/";
    private static ContactService instance = null;
    private static HashMap<String,Contact> contacts = new HashMap<String,Contact>();
    private static String[] contactsAry = {"Imran Arif","Rizwan Arif","Salman Arif","saf Amna","Amna","Khan","Nadeem","Kashif","Ameer Shahib","Raza. Com","Immigatation Consulta","Vasee","Aslam Raja M"};
    private static String[][] contactsNumberAry = {{"9057564764","4167100059"},{"+1 302 5001759"},{"+92 30058992"},{"4167254731"},null,null,null,null,null,null,null,null,null};
    private static String[] numberAlphabetAry = {"","","ABC","DEF","GHI","JKL","MNO","PQRS","TUV","WZYZ"};
    private static HashMap<String,ArrayList<String>> searchResults = new HashMap<String,ArrayList<String>>();
    private static Context mContext ;

    private ContactService(Context mContext){
        this.mContext = mContext;
    }

    public static ContactService getContactsInstance(Context mContext)
    {
        if(instance == null) {
            instance = new ContactService(mContext);
        }
        return instance;
    }

    public void loadContacts(){
        String phone = null;
        String phonetype = null;
        String emailContact = null;
        String emailType = null;
        String image_uri = "";
        StringBuffer sb = new StringBuffer();

        ContentResolver cr = mContext.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                //sb.append("\n Contact id:" + id);
                //sb.append("\n Contact Name:" + name);
                ArrayList<ContactPhone> phoneAry= new ArrayList<ContactPhone>();
                image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    //System.out.println("name : " + name + ", ID : " + id);
                    //sb.append("\n Contact Name:" + name);
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phonetype = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                       // sb.append("\n Phone number:" + phone);
                        phoneAry.add(new ContactPhone(phonetype,phone));
                        //System.out.println("phone" + phone);
                    }
                    pCur.close();
                    //sb.append("\n........................................");
                }
                if(phoneAry.size()>0){
                    contacts.put(id,new Contact(id,name,phoneAry.toArray(new ContactPhone[0])));
                }


            }
        }

/*        int i =0;
        for(String contactName : contactsAry){
            ArrayList<ContactPhone> phoneAry= new ArrayList<ContactPhone>();
            if(contactsNumberAry[i]!=null){
                for(String number: contactsNumberAry[i] ){
                    ContactPhone phone = new ContactPhone();
                    phone.setNumber(number);
                    phoneAry.add(phone);
                }
            }

            contacts.put(String.valueOf(i), new Contact(String.valueOf(i),contactName,phoneAry.toArray(new ContactPhone[0])));
            i++;
        }*/

    }



    public static HashMap<String, Contact> getContacts() {
        return contacts;
    }

    public static void setContacts(HashMap<String, Contact> contacts) {
        ContactService.contacts = contacts;
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
        ArrayList<String> searchkeys = new ArrayList<String>( Arrays.asList(searchResults.keySet().toArray(new String[0])));
        Collections.sort(searchkeys);
        for(String searchKey : searchkeys){

            ArrayList<String> contactlist = new ArrayList<String>();
            for(String contactId:searchResults.get(searchKey)){
                if(contacts.get(contactId).getName().toUpperCase().indexOf(searchKey)== 0)
                    contactlist.add(contacts.get(contactId).getName());
            }
            if(contactlist.size()>0){
                System.out.print("\n ["+searchKey+"]  ");
                StringBuffer sb = new StringBuffer();
                for(String contact: contactlist){
                    sb.append(contact);
                    sb.append(",");
                }
                System.out.print(sb.subSequence(0, sb.length()-1));
            }


        }
        for(String searchKey : searchkeys){

            ArrayList<String> contactlist = new ArrayList<String>();
            for(String contactId:searchResults.get(searchKey)){
                if(contacts.get(contactId).getName().toUpperCase().indexOf(searchKey) > 0)
                    contactlist.add(contacts.get(contactId).getName());
            }
            if(contactlist.size()>0){
                System.out.print("\n ["+searchKey+"]  ");
                StringBuffer sb = new StringBuffer();
                for(String contact: contactlist){
                    sb.append(contact);
                    sb.append(",");
                }
                System.out.print(sb.subSequence(0, sb.length()-1));
            }


        }

    }

    public static String createContactsMessageXML(ArrayList<Contact> contactsList){
        String message = "";
        StringBuffer sb = new StringBuffer();
        if(contactsList.size() !=0){
            sb.append("<contactsList>");
            for(Contact contact : contactsList){
                sb.append(" <contact>\n");
                sb.append("   <id>"+contact.getId()+"</id>\n");
                sb.append("   <name>"+contact.getName()+"</name>\n");
                if(contact.getContactPhones() != null && contact.getContactPhones().length != 0 ){
                    sb.append("   <phoneList>\n");
                    for(ContactPhone phone: contact.getContactPhones()){
                        sb.append("    <phone>"+phone.getNumber()+"</phone>\n");
                    }
                    sb.append("   </phoneList>\n");
                }
                sb.append(" </contact>\n");
            }
            sb.append("</contactsList>\n");
        }
        message = sb.toString();
        return message;
    }

    public static String createContactsMessageJSON(ArrayList<Contact> contactsList) throws JSONException {
        String message = "";
        JSONArray jsonContactArray = new JSONArray();
        JSONObject jsonContacts = new JSONObject();
        for(Contact contact: contactsList){
            JSONObject jsonContact = new JSONObject();
            jsonContact.put("id", contact.getId());
            jsonContact.put("name", contact.getName());
            JSONArray jsonPhoneArr = new JSONArray();
            for(ContactPhone phone : contact.getContactPhones()){
                JSONObject pnObj = new JSONObject();
                pnObj.put("num", phone.getNumber());
                pnObj.put("type",phone.getType());
                jsonPhoneArr.put(pnObj);
            }
            jsonContact.put("phoneList", jsonPhoneArr);
            jsonContactArray.put(jsonContact);
            jsonContacts.put("contacts",jsonContactArray);
        }
        return jsonContacts.toString();
    }


}
