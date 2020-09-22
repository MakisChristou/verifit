package com.example.verifit;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

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
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            String key = preference.getKey();

            // Backup & Restore
            if(key.equals("importcsv"))
            {

                // Prepare to show exercise dialog box
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View view = inflater.inflate(R.layout.import_warning_dialog,null);
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(view).create();


                Button bt_yes3 = view.findViewById(R.id.bt_yes3);
                Button bt_no3 = view.findViewById(R.id.bt_no3);

                bt_yes3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Intent in = new Intent(getActivity(),MainActivity.class);
                        in.putExtra("doit","importcsv");
                        startActivity(in);
                    }
                });

                bt_no3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        alertDialog.dismiss();
                    }
                });


                alertDialog.show();



            }
            else if(key.equals("exportcsv"))
            {
                Intent in = new Intent(getActivity(),MainActivity.class);
                in.putExtra("doit","exportcsv");
                startActivity(in);
            }
            else if(key.equals("deletedata"))
            {
                deleteData();
            }
            else if(key.equals("nextsync"))
            {
                Toast.makeText(getContext(),"Not implemented yet",Toast.LENGTH_SHORT).show();
            }
            // General
            else if(key.equals("theme"))
            {
                Toast.makeText(getContext(),"Dark Theme not implemented yet",Toast.LENGTH_SHORT).show();
            }
            else if(key.equals("github"))
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/MakisChristou/verifit"));
                startActivity(browserIntent);
            }
            else if(key.equals("version"))
            {
                // Toast.makeText(getContext(),"Nothing to see here",Toast.LENGTH_SHORT).show();
            }
            else if(key.equals("donate"))
            {
                donate();
            }
            else if(key.equals("licence"))
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gnu.org/licenses/gpl-3.0.en.html"));
                startActivity(browserIntent);
            }
            else if(key.equals("help"))
            {
                Toast.makeText(getContext(),"Not implemented yet",Toast.LENGTH_SHORT).show();
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


            Button bt_bitcoin = view.findViewById(R.id.bt_bitcoin);
            Button bt_ethereum = view.findViewById(R.id.bt_ethereum);
            Button bt_vericoin = view.findViewById(R.id.bt_vericoin);
            Button bt_monero = view.findViewById(R.id.bt_monero);

            ImageView crypto_imageView = view.findViewById(R.id.crypto_imageView);

            bt_bitcoin.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    crypto_imageView.setImageResource(R.drawable.btc);

                    // Copy Corresponding Address to Clipboard
                    ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(getContext().CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("btc", "3MMsuEGz7LcAt3u7djuheUpCWFWCWPfHKG");
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getContext(),"BTC Address Copied",Toast.LENGTH_SHORT).show();
                }
            });


            bt_ethereum.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    crypto_imageView.setImageResource(R.drawable.eth);

                    // Copy Corresponding Address to Clipboard
                    ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(getContext().CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("btc", "0x6AA6fdB6FEc26DeEBF6571d3dabA16c15AE36F98");
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getContext(),"ETH Address Copied",Toast.LENGTH_SHORT).show();
                }
            });


            bt_vericoin.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    crypto_imageView.setImageResource(R.drawable.vrc);
                    // Copy Corresponding Address to Clipboard
                    ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(getContext().CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("btc", "VEPR5c1AC6dJARs7oAEhwqDMwowuAU2yWV");
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getContext(),"VRC Address Copied",Toast.LENGTH_SHORT).show();
                }
            });

            bt_monero.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    crypto_imageView.setImageResource(R.drawable.xmr);
                    // Copy Corresponding Address to Clipboard
                    ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(getContext().CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("btc", "48MakisiDP1W9VLPPHv3a47fAU4bKt13T4X6rpghVFx88KM3jRY3BNK1xw2Sq26rb4JDhKf8iQa7cU7PkbBZoyKmMi8s8W7");
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getContext(),"XMR Address Copied",Toast.LENGTH_SHORT).show();

                }
            });

            // Show Exercise Dialog Box
            alertDialog.show();
        }

    }
}