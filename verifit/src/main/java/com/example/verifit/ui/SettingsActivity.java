package com.example.verifit.ui;

import static com.example.verifit.ui.MainActivity.READ_REQUEST_CODE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;

import com.example.verifit.LoadingDialog;
import com.example.verifit.R;
import com.example.verifit.SharedPreferences;
import com.example.verifit.SnackBarWithMessage;
import com.example.verifit.verifitrs.UsersApi;
import com.example.verifit.verifitrs.WorkoutSetsApi;
import com.example.verifit.webdav.CheckWebdavThread;
import com.example.verifit.webdav.ClickedOnWebdavThread;
import com.example.verifit.webdav.ExportWebdavThread;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
            Preference exportwebdav = findPreference("exportwebdav");
            Preference webdavurl = findPreference("webdavurl");
            Preference webdavusername = findPreference("webdavusername");
            Preference webdavpassword = findPreference("webdavpassword");
            Preference webdavcheckconnection = findPreference("webdavcheckconnection");
            Preference autowebdavbackup = findPreference("autowebdavbackup");
            Preference togglewebdav = findPreference("togglewebdav");

            PreferenceManager preferenceManager = getPreferenceManager();
            if (preferenceManager.getSharedPreferences().getBoolean("togglewebdav", true))
            {
                System.out.println("onCreatePreferences() Webdav on");
                // Webdav switch is on
                importwebdav.setVisible(true);
                exportwebdav.setVisible(true);
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
                exportwebdav.setVisible(false);
                webdavurl.setVisible(false);
                webdavusername.setVisible(false);
                webdavpassword.setVisible(false);
                webdavcheckconnection.setVisible(false);
                autowebdavbackup.setVisible(false);

                sharedPreferences.save("false", "togglewebdav");
            }

            setVerifitRsSettingsVisibility();

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
            Preference verifit_rs_login_signup_logout = findPreference("verifit_rs_login_signup_logout");
            Preference verifit_rs_import = findPreference("verifit_rs_import");
            Preference verifit_rs_export = findPreference("verifit_rs_export");
            Preference verifit_rs_delete_all = findPreference("verifit_rs_delete_all");

            // User is not logged in
            if(sharedPreferences.isOfflineMode())
            {
                verifit_rs_import.setVisible(false);
                verifit_rs_export.setVisible(false);
                verifit_rs_delete_all.setVisible(false);

                verifit_rs_login_signup_logout.setTitle("Login/Sign Up");
                verifit_rs_login_signup_logout.setSummary("Login or create a free account");
            }
            else
            {
                verifit_rs_import.setVisible(true);
                verifit_rs_export.setVisible(true);
                verifit_rs_delete_all.setVisible(true);

                verifit_rs_login_signup_logout.setTitle("Logout");
                verifit_rs_login_signup_logout.setSummary("You are logged in as " + sharedPreferences.load("verifit_rs_username"));

                disableOfflineSettings();
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
                        logoutFromVerifitRs();
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

                if (preferenceManager.getSharedPreferences().getBoolean("togglewebdav", true))
                {
                    System.out.println("Turning webdav on");
                    turnWebdavOn();
                }
                else
                {
                    System.out.println("Turning webdav off");
                    turnWebdavOff();
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
                    Toast.makeText(getContext(), "Some fields are empty", Toast.LENGTH_SHORT).show();
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
                // Prepare to show exercise dialog box
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View view = inflater.inflate(R.layout.privacy_policy_dialog,null);
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(view).create();

                alertDialog.show();
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
            else if (key.equals("contact_us"))
            {
                composeEmail("support@verifit.xyz", "", getContext());
            }
            else if(key.equals("verifit_rs_login_signup_logout"))
            {
                // If user is not logged in
                if(sharedPreferences.isOfflineMode())
                {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
                else
                {
                    final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                    loadingDialog.loadingAlertDialog();

                    UsersApi users = new UsersApi(getContext(),getString(R.string.API_ENDPOINT), sharedPreferences.load("verifit_rs_username"), sharedPreferences.load("verifit_rs_password"));
                    users.logout(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            // Handle error
                            loadingDialog.dismissDialog();
                            SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(getContext());
                            snackBarWithMessage.showSnackbar("Can't connect to server");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            sharedPreferences.enableOfflineMode();

                            loadingDialog.dismissDialog();

                            // Whether success or not we still logout the user
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.putExtra("message", "verifit_rs_logout"); // Replace "key" with a key identifier and "value" with the actual string value
                            getContext().startActivity(intent);
                        }
                    });
                }
            }
            else if(key.equals("verifit_rs_import"))
            {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View view = inflater.inflate(R.layout.import_mild_warning_dialog, null);
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(view).create();

                TextView tv_date = view.findViewById(R.id.tv_date);

                tv_date.setText("Upload all sets?");

                Button bt_yes3 = view.findViewById(R.id.bt_yes3);
                Button bt_no3 = view.findViewById(R.id.bt_no3);

                bt_yes3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        fileSearch();
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
            else if(key.equals("verifit_rs_export"))
            {
                // Same as offline export since we are exporting everything in local data structures
                Intent in = new Intent(getActivity(), MainActivity.class);
                in.putExtra("doit", "exportcsv");
                startActivity(in);
            }
            else if(key.equals("verifit_rs_delete_all"))
            {

                // Prepare to show exercise dialog box
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View view = inflater.inflate(R.layout.import_red_warning_dialog, null);
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(view).create();

                TextView tv_date = view.findViewById(R.id.tv_date);

                tv_date.setText("Delete all account data?");

                Button bt_yes3 = view.findViewById(R.id.bt_yes3);
                Button bt_no3 = view.findViewById(R.id.bt_no3);

                bt_yes3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                        loadingDialog.loadingAlertDialog();

                        WorkoutSetsApi workoutSetsApi = new WorkoutSetsApi(getContext(), getString(R.string.API_ENDPOINT));
                        workoutSetsApi.deleteWorkoutSets(MainActivity.dataStorage.getSets(), new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e)
                            {
                                loadingDialog.dismissDialog();
                                SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(getContext());
                                snackBarWithMessage.showSnackbar("Can't connect to server");
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException
                            {
                                alertDialog.dismiss();
                                loadingDialog.dismissDialog();

                                if(200 == response.code())
                                {

                                    MainActivity.dataStorage.clearDataStructures(getContext());
                                    SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(getContext());
                                    snackBarWithMessage.showSnackbar("All data deleted");
                                }
                                else
                                {
                                    SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(getContext());
                                    snackBarWithMessage.showSnackbar(response.message().toString());
                                }
                            }
                        });
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

            return true;
        }


        // Select a file using the build in file manager
        public void fileSearch()
        {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/*");
            startActivityForResult(intent,READ_REQUEST_CODE);
        }

        // When File explorer stops this function runs
        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
        {
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == Activity.RESULT_OK)
            {
                if (data != null)
                {
                    Uri uri = data.getData();
                    if (requestCode == READ_REQUEST_CODE)
                    {
                        if(MainActivity.dataStorage.readFile(uri, getContext()))
                        {

                            final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                            loadingDialog.loadingAlertDialog();

                                WorkoutSetsApi workoutSetsApi = new WorkoutSetsApi(getContext(), getString(R.string.API_ENDPOINT));
                                workoutSetsApi.postWorkoutSets(MainActivity.dataStorage.getSets(), new Callback()
                                {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e)
                                    {
                                        loadingDialog.dismissDialog();
                                        SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(getContext());
                                        snackBarWithMessage.showSnackbar("Can't connect to server");
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException
                                    {
                                        loadingDialog.dismissDialog();

                                        if(200 == response.code()) {

                                            SharedPreferences sharedPreferences = new SharedPreferences(getContext());
                                            sharedPreferences.disableCaching();

                                            SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(getContext());
                                            snackBarWithMessage.showSnackbar("Data imported successfully");
                                        }
                                        else{
                                            SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(getContext());
                                            snackBarWithMessage.showSnackbar(response.message().toString());
                                        }
                                    }
                                });
                        }
                    }
                }
            }
        }


        public void composeEmail(String address, String subject, Context context) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, address);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                System.out.println("Hello");
                startActivity(intent);
            }
        }

        public void turnWebdavOff()
        {
            Toast.makeText(getContext(), "Webdav is off", Toast.LENGTH_SHORT).show();

            SharedPreferences sharedPreferences = new SharedPreferences(getContext());
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

        public void turnWebdavOn()
        {
            SharedPreferences sharedPreferences = new SharedPreferences(getContext());
            sharedPreferences.save("true", "togglewebdav");

            logoutFromVerifitRs();

            System.out.println("turnWebdavOn() Webdav on");

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


        public void logoutFromVerifitRs()
        {
            SharedPreferences sharedPreferences = new SharedPreferences(getContext());
            sharedPreferences.save("offline", "mode");
            sharedPreferences.save("", "verifit_rs_token");
        }


        public void disableOfflineSettings()
        {
            System.out.println("Disabling Offline Settings");

            SharedPreferences sharedPreferences = new SharedPreferences(getContext());
            sharedPreferences.save("false", "togglewebdav");

            Preference importwebdav = findPreference("importwebdav");
            Preference exportwebdav = findPreference("exportwebdav");
            Preference webdavurl = findPreference("webdavurl");
            Preference webdavusername = findPreference("webdavusername");
            Preference webdavpassword = findPreference("webdavpassword");
            Preference webdavcheckconnection = findPreference("webdavcheckconnection");
            Preference autowebdavbackup = findPreference("autowebdavbackup");
            Preference togglewebdav = findPreference("togglewebdav");

            importwebdav.setVisible(false);
            exportwebdav.setVisible(false);
            webdavurl.setVisible(false);
            webdavusername.setVisible(false);
            webdavpassword.setVisible(false);
            webdavcheckconnection.setVisible(false);
            autowebdavbackup.setVisible(false);
            togglewebdav.setVisible(false);

            Preference importcsv = findPreference("importcsv");
            Preference exportcsv = findPreference("exportcsv");
            Preference deletedata = findPreference("deletedata");

            importcsv.setVisible(false);
            exportcsv.setVisible(false);
            deletedata.setVisible(false);

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
//        public void donate()
//        {
//            // Prepare to show exercise dialog box
//            LayoutInflater inflater = LayoutInflater.from(getContext());
//            View view = inflater.inflate(R.layout.donate_dialog,null);
//            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(view).create();
//
//            ImageView crypto_imageView = view.findViewById(R.id.crypto_imageView);
//
//
//            crypto_imageView.setImageResource(R.drawable.xmr);
//            // Copy Corresponding Address to Clipboard
//            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(getContext().CLIPBOARD_SERVICE);
//            ClipData clip = ClipData.newPlainText("xmr", "42uCPZuxsSS3FNNx6RMDAMVmHVwYBfg3JVMuPKMwadeEfwyykFLkwAH8j4B12ziU7PBCMjLwpPbbDgBw45N4wMpsM3Dy7is");
//            clipboard.setPrimaryClip(clip);
//            Toast.makeText(getContext(),"XMR Address Copied",Toast.LENGTH_SHORT).show();
//
//
//            Button monero_button = view.findViewById(R.id.monero_button);
//
//
//            monero_button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.getmonero.org/"));
//                    startActivity(browserIntent);
//                }
//            });
//
//            // Show Exercise Dialog Box
//            alertDialog.show();
//        }


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