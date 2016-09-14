package com.bloodbank.kanbankasi;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    com.kanbankasi.kanbankasi.DBAdapter db;
    Context ctx;


    ArrayList<District> districts;
    RelativeLayout rl;
    int cityCode;
    ProgressDialog mDialog;
    Spinner citySpinner, bloodSpinner, sexSpinner, districtSpinner;
    String bloodType, city, sex, selectedDistrict;

    String cities[] = {"Şehir Seçiniz", "Adana", "Adıyaman", "Afyon", "Ağrı", "Amasya", "Ankara", "Antalya", "Artvin",
            "Aydın", "Balıkesir", "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale",
            "Çankırı", "Çorum", "Denizli", "Diyarbakır", "Edirne", "Elazığ", "Erzincan", "Erzurum", "Eskişehir",
            "Gaziantep", "Giresun", "Gümüşhane", "Hakkari", "Hatay", "Isparta", "Mersin", "İstanbul", "İzmir",
            "Kars", "Kastamonu", "Kayseri", "Kırklareli", "Kırşehir", "Kocaeli", "Konya", "Kütahya", "Malatya",
            "Manisa", "Kahramanmaraş", "Mardin", "Muğla", "Muş", "Nevşehir", "Niğde", "Ordu", "Rize", "Sakarya",
            "Samsun", "Siirt", "Sinop", "Sivas", "Tekirdağ", "Tokat", "Trabzon", "Tunceli", "Şanlıurfa", "Uşak",
            "Van", "Yozgat", "Zonguldak", "Aksaray", "Bayburt", "Karaman", "Kırıkkale", "Batman", "Şırnak",
            "Bartın", "Ardahan", "Iğdır", "Yalova", "Karabük", "Kilis", "Osmaniye", "Düzce"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        rl = (RelativeLayout) findViewById(R.id.relative);
        db = new DBAdapter();
        ctx = this;


        citySpinner = (Spinner) findViewById(R.id.city);
        bloodSpinner = (Spinner) findViewById(R.id.bloodType);
        sexSpinner = (Spinner) findViewById(R.id.sex);
        districtSpinner = (Spinner) findViewById(R.id.district);
        districts = new ArrayList<District>();


        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cities);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        citySpinner.setOnItemSelectedListener(this);
        citySpinner.setAdapter(spinnerArrayAdapter);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.bloodtypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodSpinner.setOnItemSelectedListener(this);
        bloodSpinner.setAdapter(adapter);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.sex, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexSpinner.setOnItemSelectedListener(this);
        sexSpinner.setAdapter(adapter3);

        cityCode = citySpinner.getSelectedItemPosition();

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.bloodtype:

                bloodType = parent.getItemAtPosition(position).toString();
                break;
            case R.id.city: {
                city = parent.getItemAtPosition(position).toString();
                if (!city.equals("Şehir Seçiniz"))
                    new DistrictSearch(parent.getSelectedItemPosition()).execute();
                break;
            }
            case R.id.sex:
                sex = parent.getItemAtPosition(position).toString();
                if (sex.equals("Erkek"))
                    sex = "E";
                else
                    sex = "K";
                break;
            case R.id.district:
                selectedDistrict = parent.getItemAtPosition(position).toString();
                break;
        }
        String item = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class DistrictSearch extends AsyncTask<String, Void, ArrayList<District>> {
        int searchCity;

        public DistrictSearch(int sc) {
            this.searchCity = sc;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(ctx);
            mDialog.setMessage("Sonuçlar Aranıyor");
            mDialog.show();

        }

        @Override
        protected ArrayList<District> doInBackground(String... strings) {


            try {
                Class.forName(db.driver).newInstance();
                try {
                    db.conn = DriverManager.getConnection(db.connString, db.userName, db.password);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    db.stmt = db.conn.createStatement();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                String sql = "SELECT * FROM dbo.District where CityCode='" + searchCity + "'";
                ResultSet rs = db.stmt.executeQuery(sql);
                while (rs.next()) {

                    String districtName = rs.getString("DistrictName");
                    int districtCode = rs.getInt("DistrictCode");
                    int cityCode = rs.getInt("CityCode");
                    District district = new District(districtName, districtCode, cityCode);
                    districts.add(district);
                }
                rs.close();

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }


            return districts;
        }

        @Override
        protected void onPostExecute(ArrayList<District> districts) {
            super.onPostExecute(districts);

            String[] district = new String[districts.size() + 1];
            for (int i = 0; i < districts.size(); i++)
                district[i] = districts.get(i).getName();

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_item, district);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            citySpinner.setOnItemSelectedListener(SearchActivity.this);
            citySpinner.setAdapter(spinnerArrayAdapter);
            citySpinner.setVisibility(View.VISIBLE);
            mDialog.dismiss();

        }
    }
}