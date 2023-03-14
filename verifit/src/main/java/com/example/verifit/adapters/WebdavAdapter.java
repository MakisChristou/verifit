package com.example.verifit.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.verifit.LoadingDialog;
import com.example.verifit.R;
import com.example.verifit.ui.MainActivity;
import com.example.verifit.webdav.DeleteWebdavThread;
import com.example.verifit.webdav.ImportWebdavThread;
import com.thegrizzlylabs.sardineandroid.DavResource;

import java.util.List;


// Adapter for WorkoutExercise Class
public class WebdavAdapter extends RecyclerView.Adapter<WebdavAdapter.MyViewHolder> {

    Context ct;
    public List<DavResource> Resources;

    public WebdavAdapter(Context ct, List<DavResource> Resources)
    {
        this.ct = ct;
        this.Resources = Resources;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(this.ct);
        View view = inflater.inflate(R.layout.webdav_row,parent,false);
        return new MyViewHolder(view);
    }

    private void showPopupMenu(View view, int position)
    {
        PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
        popupMenu.inflate(R.menu.webdav_floating_context_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                // Load Webdav url and credentials
                SharedPreferences sharedPreferences = ct.getSharedPreferences("shared preferences", 0); // MODE_PRIVATE = 0
                String webdav_url = sharedPreferences.getString("webdav_url", "");
                String webdav_username = sharedPreferences.getString("webdav_username", "");
                String webdav_password = sharedPreferences.getString("webdav_password", "");
                String webdav_resource = Resources.get(position).getName();


                // To Do: Edit Remote Webdav Resource
                if(item.getItemId() == R.id.edit)
                {
                    System.out.println("Sardine Edit Clicked");
                }

                // Delete Remote Webdav Resource
                else if(item.getItemId() == R.id.delete)
                {
                    System.out.println("Sardine Delete Clicked");

                    // Show network loading popup and run stuff on background thread
                    final LoadingDialog loadingDialog = new LoadingDialog((Activity) ct);
                    loadingDialog.loadingAlertDialog();

                    DeleteWebdavThread deleteWebdavThread = new DeleteWebdavThread((Activity) ct, webdav_url, webdav_username, webdav_password, webdav_resource, loadingDialog, MainActivity.webdavAdapter);
                    deleteWebdavThread.start();
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {

        holder.tv_name.setText(Resources.get(position).getName());

        Long bytes =  Resources.get(position).getContentLength();
        holder.tv_size.setText(bytes.toString());
        holder.tv_date.setText(Resources.get(position).getCreation().toString());

        holder.cardview_webdav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(ct,Resources.get(position).getName() +" clicked!", Toast.LENGTH_LONG).show();

                // Load Webdav url and credentials
                SharedPreferences sharedPreferences = ct.getSharedPreferences("shared preferences", 0); // MODE_PRIVATE = 0
                String webdav_url = sharedPreferences.getString("webdav_url", "");
                String webdav_username = sharedPreferences.getString("webdav_username", "");
                String webdav_password = sharedPreferences.getString("webdav_password", "");
                String webdav_resource = Resources.get(position).getName();

                // To Do: Put loading screen here
                // Show network loading popup
                final LoadingDialog loadingDialog = new LoadingDialog((Activity) ct);
                loadingDialog.loadingAlertDialog();


                // Import remote file
                //MainActivity.importWebDav(ct, webdav_url, webdav_username, webdav_password, webdav_resource);
                ImportWebdavThread importWebdavThread = new ImportWebdavThread(ct, webdav_url, webdav_username, webdav_password, webdav_resource, loadingDialog);
                importWebdavThread.start();
            }
        });


        holder.cardview_webdav.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view)
            {
                // Show Popup Menu Edit, Delete options just like in the exercises Activity
                showPopupMenu(view, position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return this.Resources.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder
    {
        TextView tv_name;
        TextView tv_date;
        TextView tv_size;
        RecyclerView recyclerView;
        View blue_line;
        CardView cardview_webdav;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_size = itemView.findViewById(R.id.tv_size);

            recyclerView = itemView.findViewById(R.id.recyclerView_Webdav);
            blue_line = itemView.findViewById(R.id.blue_line_webdav);
            cardview_webdav = itemView.findViewById(R.id.cardview_webdav);
        }
    }
}
