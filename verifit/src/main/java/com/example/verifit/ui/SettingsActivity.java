package com.example.verifit.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.verifit.LoadingDialog;
import com.example.verifit.R;
import com.example.verifit.SharedPreferences;
import com.example.verifit.verifitrs.UsersApi;
import com.example.verifit.webdav.CheckWebdavThread;
import com.example.verifit.webdav.ClickedOnWebdavThread;
import com.example.verifit.webdav.ExportWebdavThread;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    public static class SettingsFragment extends PreferenceFragmentCompat implements PreferenceManager.OnPreferenceTreeClickListener{

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            SharedPreferences sharedPreferences = new SharedPreferences(getContext());


            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Preference importwebdav = findPreference("importwebdav");
            Preference excportwebdav = findPreference("exportwebdav");
            Preference webdavurl = findPreference("webdavurl");
            Preference webdavusername = findPreference("webdavusername");
            Preference webdavpassword = findPreference("webdavpassword");
            Preference webdavcheckconnection = findPreference("webdavcheckconnection");
            Preference autowebdavbackup = findPreference("autowebdavbackup");
            Preference autobackup = findPreference("autobackup");


            setVerifitRsSettingsVisibility();


            PreferenceManager preferenceManager = getPreferenceManager();
            if (preferenceManager.getSharedPreferences().getBoolean("togglewebdav", true))
            {
                // Webdav switch is on
                importwebdav.setVisible(true);
                excportwebdav.setVisible(true);
                webdavurl.setVisible(true);
                webdavusername.setVisible(true);
                webdavpassword.setVisible(true);
                webdavcheckconnection.setVisible(true);
                autowebdavbackup.setVisible(true);

                sharedPreferences.save("true", "togglewebdav");

            }
            else
            {
                // Webdav switch is off
                importwebdav.setVisible(false);
                excportwebdav.setVisible(false);
                webdavurl.setVisible(false);
                webdavusername.setVisible(false);
                webdavpassword.setVisible(false);
                webdavcheckconnection.setVisible(false);
                autowebdavbackup.setVisible(false);

                sharedPreferences.save("false", "togglewebdav");
            }

            // Set summary to user config
            webdavurl.setSummary(sharedPreferences.load("webdav_url"));
            webdavusername.setSummary(sharedPreferences.load("webdav_username"));
            webdavpassword.setSummary(getPasswordStarred());

            if (preferenceManager.getSharedPreferences().getBoolean("autowebdavbackup", true))
            {
                sharedPreferences.save("true", "autowebdavbackup");
            }
            else
            {
                sharedPreferences.save("false", "autowebdavbackup");
            }

            if (preferenceManager.getSharedPreferences().getBoolean("autobackup", true))
            {
                sharedPreferences.save("true", "autobackup");
            }
            else
            {
                sharedPreferences.save("false", "autobackup");
            }

            // On user update save Webdav config in shared preferences

            webdavurl.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    sharedPreferences.save(newValue.toString(), "webdav_url");
                    webdavurl.setSummary(sharedPreferences.load("webdav_url"));
                    return false;
                }
            });

            webdavusername.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    sharedPreferences.save(newValue.toString(), "webdav_username");
                    webdavusername.setSummary(sharedPreferences.load("webdav_username"));
                    return false;
                }
            });

            webdavpassword.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    sharedPreferences.save(newValue.toString(), "webdav_password");
                    webdavpassword.setSummary(getPasswordStarred());
                    return false;
                }
            });

            // Make password not shown when typing
            EditTextPreference preference = findPreference("webdavpassword");

            if (preference!= null)
            {
                preference.setOnBindEditTextListener(
                        new EditTextPreference.OnBindEditTextListener()
                        {
                            @Override
                            public void onBindEditText(@NonNull EditText editText)
                            {
                                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            }
                        });
            }
        }


        public void setVerifitRsSettingsVisibility()
        {
            SharedPreferences sharedPreferences = new SharedPreferences(getContext());

            Preference verifit_rs_logged_in = findPreference("verifit_rs_logged_in");
            Preference verifit_rs_settings = findPreference("verifit_rs_settings");
            Preference verifit_rs_login_signup_logout = findPreference("verifit_rs_login_signup_logout");

            // User is not logged in
            if(sharedPreferences.load("verifit_rs_token").isEmpty())
            {
                verifit_rs_logged_in.setVisible(false);
                verifit_rs_settings.setVisible(false);
                verifit_rs_login_signup_logout.setTitle("Login/Sign Up");
                verifit_rs_login_signup_logout.setSummary("Login or create a free account");
            }
            else
            {
                verifit_rs_logged_in.setVisible(false);
                verifit_rs_settings.setVisible(true);
                verifit_rs_login_signup_logout.setTitle("Logout");
                verifit_rs_login_signup_logout.setSummary("You are logged in as " + sharedPreferences.load("verifit_rs_username"));
            }
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            String key = preference.getKey();
            SharedPreferences sharedPreferences = new SharedPreferences(getContext());

            // Backup & Restore
            if (key.equals("importcsv"))
            {
                // Prepare to show exercise dialog box
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View view = inflater.inflate(R.layout.import_warning_dialog, null);
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(view).create();


                Button bt_yes3 = view.findViewById(R.id.bt_yes3);
                Button bt_no3 = view.findViewById(R.id.bt_no3);

                bt_yes3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent in = new Intent(getActivity(), MainActivity.class);
                        in.putExtra("doit", "importcsv");
                        startActivity(in);
                    }
                });

                bt_no3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });


                alertDialog.show();


            }
            else if (key.equals("exportcsv"))
            {
                Intent in = new Intent(getActivity(), MainActivity.class);
                in.putExtra("doit", "exportcsv");
                startActivity(in);
            }
            else if (key.equals("deletedata"))
            {
                deleteData();
            }
            else if (key.equals("togglewebdav"))
            {
                PreferenceManager preferenceManager = getPreferenceManager();
                if (preferenceManager.getSharedPreferences().getBoolean("togglewebdav", true)) {
                    // Your switch is on
                    System.out.println("Toggle is on");
                    sharedPreferences.save("true", "togglewebdav");


                    Toast.makeText(getContext(), "Webdav is on", Toast.LENGTH_SHORT).show();
                    Preference importwebdav = findPreference("importwebdav");
                    importwebdav.setVisible(true);
                    Preference excportwebdav = findPreference("exportwebdav");
                    excportwebdav.setVisible(true);
                    Preference webdavurl = findPreference("webdavurl");
                    webdavurl.setVisible(true);
                    Preference webdavusername = findPreference("webdavusername");
                    webdavusername.setVisible(true);
                    Preference webdavpassword = findPreference("webdavpassword");
                    webdavpassword.setVisible(true);
                    Preference webdavcheckconnection = findPreference("webdavcheckconnection");
                    webdavcheckconnection.setVisible(true);
                    Preference autowebdavbackup = findPreference("autowebdavbackup");
                    autowebdavbackup.setVisible(true);
                }
                else
                {
                    // Your switch is off
                    System.out.println("Toggle is off");
                    Toast.makeText(getContext(), "Webdav is off", Toast.LENGTH_SHORT).show();
                    sharedPreferences.save("false", "togglewebdav");


                    Preference importwebdav = findPreference("importwebdav");
                    importwebdav.setVisible(false);
                    Preference excportwebdav = findPreference("exportwebdav");
                    excportwebdav.setVisible(false);
                    Preference webdavurl = findPreference("webdavurl");
                    webdavurl.setVisible(false);
                    Preference webdavusername = findPreference("webdavusername");
                    webdavusername.setVisible(false);
                    Preference webdavpassword = findPreference("webdavpassword");
                    webdavpassword.setVisible(false);
                    Preference webdavcheckconnection = findPreference("webdavcheckconnection");
                    webdavcheckconnection.setVisible(false);
                    Preference autowebdavbackup = findPreference("autowebdavbackup");
                    autowebdavbackup.setVisible(false);
                }
            }
            else if (key.equals("importwebdav"))
            {
                System.out.println("Clicked on Import Webdav");
                // Load Shared Preferences if they exist
                String webdav_url = sharedPreferences.load("webdav_url");
                String webdav_username = sharedPreferences.load("webdav_username");
                String webdav_password = sharedPreferences.load("webdav_password");

                if(webdav_url.isEmpty() || webdav_username.isEmpty() || webdav_password.isEmpty())
                {
                    Toast.makeText(getContext(), "Some fields are empty, cannot export", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // Show network loading popup
                    final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                    loadingDialog.loadingAlertDialog();


                    // Prepare to show remote files dialog box
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    View view = inflater.inflate(R.layout.choose_webdav_file_dialog,null);
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(view).create();

                    ClickedOnWebdavThread clickedOnWebdavThread = new ClickedOnWebdavThread(getActivity(), webdav_url, webdav_username, webdav_password, loadingDialog, alertDialog, view, MainActivity.webdavAdapter);
                    clickedOnWebdavThread.start();
                }
            }
            else if (key.equals("exportwebdav"))
            {
                // Load Shared Preferences if they exist
                String webdav_url = sharedPreferences.load("webdav_url");
                String webdav_username = sharedPreferences.load("webdav_username");
                String webdav_password = sharedPreferences.load("webdav_password");

                if(webdav_url.isEmpty() || webdav_username.isEmpty() || webdav_password.isEmpty())
                {
                    Toast.makeText(getContext(), "Some fields are empty, cannot export", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // Show loading popup
                    final LoadingDialog loadingDialog = new LoadingDialog((Activity) getContext());
                    loadingDialog.loadingAlertDialog();

                    ExportWebdavThread exportWebdavThread = new ExportWebdavThread(getContext(), webdav_url, webdav_username, webdav_password, loadingDialog);
                    exportWebdavThread.start();
                }
            }
            else if (key.equals("webdavurl"))
            {
                System.out.println("Webdav Url");
            }
            else if (key.equals("webdavusername"))
            {
                System.out.println("Webdav Username");
            }
            else if (key.equals("webdavpassword"))
            {
                System.out.println("Webdav Password");
            }
            else if (key.equals("webdavcheckconnection"))
            {
                System.out.println("Webdav Check Connection");

                // Load Shared Preferences if they exist
                String webdav_url = sharedPreferences.load("webdav_url");
                String webdav_username = sharedPreferences.load("webdav_username");
                String webdav_password = sharedPreferences.load("webdav_password");

                if(webdav_url.isEmpty() || webdav_username.isEmpty() || webdav_password.isEmpty())
                {
                    Toast.makeText(getContext(), "Some fields are empty, cannot export", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // Show network loading popup
                    final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                    loadingDialog.loadingAlertDialog();


                    CheckWebdavThread checkWebdavThread = new CheckWebdavThread((Activity) getContext(), webdav_url, webdav_username, webdav_password, loadingDialog);
                    checkWebdavThread.start();
                }
            }

            else if(key.equals("autowebdavbackup"))
            {
                PreferenceManager preferenceManager = getPreferenceManager();
                if (preferenceManager.getSharedPreferences().getBoolean("autowebdavbackup", true))
                {
                    System.out.println("Auto Webdav backup is on");
                    Toast.makeText(getContext(), "Auto Webdav backup is on", Toast.LENGTH_SHORT).show();
                    sharedPreferences.save("true", "autowebdavbackup");
                }
                else
                {
                    System.out.println("Auto Webdav backup is off");
                    Toast.makeText(getContext(), "Auto Webdav backup is off", Toast.LENGTH_SHORT).show();
                    sharedPreferences.save("false", "autowebdavbackup");
                }
            }

            // Not being used currently
            else if(key.equals("autobackup"))
            {
                PreferenceManager preferenceManager = getPreferenceManager();
                if (preferenceManager.getSharedPreferences().getBoolean("autobackup", true))
                {
                    System.out.println("Auto backup is on");
                    Toast.makeText(getContext(), "Auto backup is on", Toast.LENGTH_SHORT).show();
                    sharedPreferences.save("true", "autobackup");
                }
                else
                {
                    System.out.println("Auto backup is off");
                    Toast.makeText(getContext(), "Auto backup is off", Toast.LENGTH_SHORT).show();
                    sharedPreferences.save("false", "autobackup");
                }
            }

            // General
            else if (key.equals("theme"))
            {
                Toast.makeText(getContext(), "Dark Theme not implemented yet", Toast.LENGTH_SHORT).show();
            }
            else if (key.equals("github"))
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/MakisChristou/verifit"));
                startActivity(browserIntent);
            }
            else if (key.equals("version"))
            {
                Toast.makeText(getContext(),"Nothing to see here",Toast.LENGTH_SHORT).show();
            }
            else if (key.equals("donate"))
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/donate/?hosted_button_id=YFZX88G8XDSN4"));
                startActivity(browserIntent);
            }
            else if(key.equals("privacy_policy"))
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://verifit.xyz/policy/"));
                startActivity(browserIntent);
            }
            else if(key.equals("reddit"))
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.reddit.com/r/verifitApp/"));
                startActivity(browserIntent);
            }
            else if (key.equals("licence"))
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gnu.org/licenses/gpl-3.0.en.html"));
                startActivity(browserIntent);
            }
            else if (key.equals("help"))
            {
                Toast.makeText(getContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();
            }
            else if(key.equals("verifit_rs_login_signup_logout"))
            {
                // If user is not logged in
                if(sharedPreferences.load("verifit_rs_token").isEmpty())
                {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
                else
                {
                    UsersApi users = new UsersApi(getContext(),"http://192.168.1.116:3000", sharedPreferences.load("verifit_rs_username"), sharedPreferences.load("verifit_rs_password"));
                    users.logout();
                }
            }

            return true;
        }

        // Delete all currently saved workout data
        public void deleteData()
        {
            // Prepare to show exercise dialog box
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.delete_all_dialog,null);
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(view).create();

            Button bt_yes = view.findViewById(R.id.bt_yes3);
            Button bt_no = view.findViewById(R.id.bt_no3);


            bt_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    MainActivity.dataStorage.getWorkoutDays().clear();
                    MainActivity.dataStorage.saveWorkoutData(getContext());
                    alertDialog.dismiss();
                    Toast.makeText(getContext(),"Data Deleted",Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(getContext(),MainActivity.class);
                    startActivity(in);
                }
            });

            bt_no.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });


            // Show Exercise Dialog Box
            alertDialog.show();
        }

        // Donations activity
        // Delete all currently saved workout data
        public void donate()
        {
            // Prepare to show exercise dialog box
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.donate_dialog,null);
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(view).create();

            ImageView crypto_imageView = view.findViewById(R.id.crypto_imageView);


            crypto_imageView.setImageResource(R.drawable.xmr);
            // Copy Corresponding Address to Clipboard
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(getContext().CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("xmr", "42uCPZuxsSS3FNNx6RMDAMVmHVwYBfg3JVMuPKMwadeEfwyykFLkwAH8j4B12ziU7PBCMjLwpPbbDgBw45N4wMpsM3Dy7is");
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(),"XMR Address Copied",Toast.LENGTH_SHORT).show();


            Button monero_button = view.findViewById(R.id.monero_button);


            monero_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.getmonero.org/"));
                    startActivity(browserIntent);
                }
            });

            // Show Exercise Dialog Box
            alertDialog.show();
        }


        public String getPasswordStarred()
        {
            SharedPreferences sharedPreferences = new SharedPreferences(getContext());

            int passwordLength = sharedPreferences.load("webdav_password").length();
            String password = "";

            for(int i = 0; i < passwordLength; i++)
            {
                password+="*";
            }

            return password;
        }


    }
}