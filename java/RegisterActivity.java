package com.bloodbank.kanbankasi;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alisan on 1.04.2016.
 */
public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String bloodType, city, sex;
    Context ctx;
    com.kanbankasi.kanbankasi.DBAdapter db;
    Spinner spinner, citySpinner, sexSpinner;
    EditText email, name, surname, gsm, gsm2, postalCode, district;
    EditText[] edt;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_register);
        db = new DBAdapter();
        email = (EditText) findViewById(R.id.email);
        name = (EditText) findViewById(R.id.name);
        surname = (EditText) findViewById(R.id.surname);
        postalCode = (EditText) findViewById(R.id.postalCode);
        gsm = (EditText) findViewById(R.id.gsm);
        gsm2 = (EditText) findViewById(R.id.gsm2);
        gsm.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        gsm2.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        district = (EditText) findViewById(R.id.districtName);
        spinner = (Spinner) findViewById(R.id.bloodtype);
        citySpinner = (Spinner) findViewById(R.id.city);
        sexSpinner = (Spinner) findViewById(R.id.sex);
        edt = new EditText[]{email, name, surname, gsm};


        //Kan Grubu Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.bloodtypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(adapter);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.sex, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexSpinner.setOnItemSelectedListener(this);
        sexSpinner.setAdapter(adapter3);


        //Şehir Spinner
      /*  ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setOnItemSelectedListener(this);
        citySpinner.setAdapter(adapter2);*/
        String cities[] = {"Şehir Seçiniz", "Adana", "Adıyaman", "Afyon", "Ağrı", "Amasya", "Ankara", "Antalya", "Artvin",
                "Aydın", "Balıkesir", "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale",
                "Çankırı", "Çorum", "Denizli", "Diyarbakır", "Edirne", "Elazığ", "Erzincan", "Erzurum", "Eskişehir",
                "Gaziantep", "Giresun", "Gümüşhane", "Hakkari", "Hatay", "Isparta", "Mersin", "İstanbul", "İzmir",
                "Kars", "Kastamonu", "Kayseri", "Kırklareli", "Kırşehir", "Kocaeli", "Konya", "Kütahya", "Malatya",
                "Manisa", "Kahramanmaraş", "Mardin", "Muğla", "Muş", "Nevşehir", "Niğde", "Ordu", "Rize", "Sakarya",
                "Samsun", "Siirt", "Sinop", "Sivas", "Tekirdağ", "Tokat", "Trabzon", "Tunceli", "Şanlıurfa", "Uşak",
                "Van", "Yozgat", "Zonguldak", "Aksaray", "Bayburt", "Karaman", "Kırıkkale", "Batman", "Şırnak",
                "Bartın", "Ardahan", "Iğdır", "Yalova", "Karabük", "Kilis", "Osmaniye", "Düzce"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cities);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        citySpinner.setOnItemSelectedListener(this);
        citySpinner.setAdapter(spinnerArrayAdapter);


    }


    //Buton Kayıt
    public void Register(View v) {
        String emailString = (String) email.getText().toString();

        if (name.getText().length() == 0 || surname.getText().length() == 0 || gsm.getText().length() == 0 || bloodType.equals(null) || email.getText().length() == 0) {

            //Burası değişecek
            for (int x = 0; x < 4; x++) {
                if (edt[x].length() == 0) {
                    edt[x].setError("Bu kısım boş bırakılamaz");
                    edt[x].requestFocus();
                }
            }


        } else if (isEmailValid(emailString) == false) {
            email.setError("Geçerli E-mail giriniz");
            email.requestFocus();
        } else {
            //DB Bağlantı
            try {
                Class.forName(db.driver).newInstance();
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            db.conn = DriverManager.getConnection(db.connString, db.userName, db.password);
                            Log.w("Connection", "open");

                            //Db'ye Kayıt Ekleme
                            db.stmt = db.conn.createStatement();
                            String sql = "INSERT INTO dbo.Contact(Name,Surname,Email,Phone,OtherPhone,BloodGroup,Gender,CityCode,CityName,DistrictCode,DistrictName) values('" +
                                    name.getText() + "','" +
                                    surname.getText() + "','" +
                                    email.getText() + "','" +
                                    gsm.getText() + "','" +
                                    gsm2.getText() + "','" +
                                    bloodType + "','" +
                                    sex + "','" +
                                    citySpinner.getSelectedItemPosition() + "','" +
                                    city + "','" +
                                    postalCode.getText() + "','" +
                                    district.getText() + "')";
                            db.stmt.executeUpdate(sql);
                            showToast();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        try {
                            //DB Bağlantı Kapat
                            db.conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showToast() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ctx, "Kayıt Oluşturuldu", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.bloodtype:

                bloodType = parent.getItemAtPosition(position).toString();
                break;
            case R.id.city:
                city = parent.getItemAtPosition(position).toString();
                break;
            case R.id.sex:
                sex = parent.getItemAtPosition(position).toString();
                if (sex.equals("Erkek"))
                    sex = "E";
                else
                    sex = "K";
                break;
        }
        String item = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
    }


}
