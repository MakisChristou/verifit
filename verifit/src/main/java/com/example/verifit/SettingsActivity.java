package com.example.verifit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
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
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Preference importwebdav = findPreference("importwebdav");
            Preference excportwebdav = findPreference("exportwebdav");
            Preference webdavurl = findPreference("webdavurl");
            Preference webdavusername = findPreference("webdavusername");
            Preference webdavpassword = findPreference("webdavpassword");
            Preference webdavcheckconnection = findPreference("webdavcheckconnection");
            Preference autowebdavbackup = findPreference("autowebdavbackup");
            Preference autobackup = findPreference("autobackup");

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

                saveSharedPreferences("true", "togglewebdav");

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

                saveSharedPreferences("false", "togglewebdav");
            }

            // Set summary to user config
            webdavurl.setSummary(loadSharedPreferences("webdav_url"));
            webdavusername.setSummary(loadSharedPreferences("webdav_username"));
            webdavpassword.setSummary(getPasswordStarred());

            if (preferenceManager.getSharedPreferences().getBoolean("autowebdavbackup", true))
            {
                saveSharedPreferences("true", "autowebdavbackup");
            }
            else
            {
                saveSharedPreferences("false", "autowebdavbackup");
            }

            if (preferenceManager.getSharedPreferences().getBoolean("autobackup", true))
            {
                saveSharedPreferences("true", "autobackup");
            }
            else
            {
                saveSharedPreferences("false", "autobackup");
            }

            // On user update save Webdav config in shared preferences

            webdavurl.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    saveSharedPreferences(newValue.toString(), "webdav_url");
                    webdavurl.setSummary(loadSharedPreferences("webdav_url"));
                    return false;
                }
            });

            webdavusername.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    saveSharedPreferences(newValue.toString(), "webdav_username");
                    webdavusername.setSummary(loadSharedPreferences("webdav_username"));
                    return false;
                }
            });

            webdavpassword.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    saveSharedPreferences(newValue.toString(), "webdav_password");
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



        public void saveSharedPreferences(String value, String key)
        {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
        }

        public String loadSharedPreferences(String key)
        {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences", MODE_PRIVATE);
            String text = sharedPreferences.getString(key, "");
            return text;
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            String key = preference.getKey();

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
                    saveSharedPreferences("true", "togglewebdav");


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
                    saveSharedPreferences("false", "togglewebdav");


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
                String webdav_url = loadSharedPreferences("webdav_url");
                String webdav_username = loadSharedPreferences("webdav_username");
                String webdav_password = loadSharedPreferences("webdav_password");

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

                    ClickedOnWebdavThread clickedOnWebdavThread = new ClickedOnWebdavThread(getActivity(), webdav_url, webdav_username, webdav_password, loadingDialog, alertDialog, view);
                    clickedOnWebdavThread.start();
                }
            }
            else if (key.equals("exportwebdav"))
            {
                // Load Shared Preferences if they exist
                String webdav_url = loadSharedPreferences("webdav_url");
                String webdav_username = loadSharedPreferences("webdav_username");
                String webdav_password = loadSharedPreferences("webdav_password");

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
                String webdav_url = loadSharedPreferences("webdav_url");
                String webdav_username = loadSharedPreferences("webdav_username");
                String webdav_password = loadSharedPreferences("webdav_password");

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
                    saveSharedPreferences("true", "autowebdavbackup");
                }
                else
                {
                    System.out.println("Auto Webdav backup is off");
                    Toast.makeText(getContext(), "Auto Webdav backup is off", Toast.LENGTH_SHORT).show();
                    saveSharedPreferences("false", "autowebdavbackup");
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
                    saveSharedPreferences("true", "autobackup");
                }
                else
                {
                    System.out.println("Auto backup is off");
                    Toast.makeText(getContext(), "Auto backup is off", Toast.LENGTH_SHORT).show();
                    saveSharedPreferences("false", "autobackup");
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
//                donate();
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
                    MainActivity.Workout_Days.clear();
                    MainActivity.saveWorkoutData(getContext());
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


//            Button bt_bitcoin = view.findViewById(R.id.bt_bitcoin);
//            Button bt_monero = view.findViewById(R.id.bt_monero);

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


//            bt_bitcoin.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View view)
//                {
//                    crypto_imageView.setImageResource(R.drawable.btc);
//
//                    // Copy Corresponding Address to Clipboard
//                    ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(getContext().CLIPBOARD_SERVICE);
//                    ClipData clip = ClipData.newPlainText("btc", "3QdfqBxpLdasMfihYxBKjdoHGEy9YbPcWP");
//                    clipboard.setPrimaryClip(clip);
//                    Toast.makeText(getContext(),"BTC Address Copied",Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            bt_monero.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View view)
//                {
//                    crypto_imageView.setImageResource(R.drawable.xmr);
//                    // Copy Corresponding Address to Clipboard
//                    ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(getContext().CLIPBOARD_SERVICE);
//                    ClipData clip = ClipData.newPlainText("xmr", "42uCPZuxsSS3FNNx6RMDAMVmHVwYBfg3JVMuPKMwadeEfwyykFLkwAH8j4B12ziU7PBCMjLwpPbbDgBw45N4wMpsM3Dy7is");
//                    clipboard.setPrimaryClip(clip);
//                    Toast.makeText(getContext(),"XMR Address Copied",Toast.LENGTH_SHORT).show();
//
//                }
//            });

            // Show Exercise Dialog Box
            alertDialog.show();
        }


        public String getPasswordStarred()
        {
            int passwordLength = loadSharedPreferences("webdav_password").length();
            String password = "";

            for(int i = 0; i < passwordLength; i++)
            {
                password+="*";
            }

            return password;
        }


    }
}