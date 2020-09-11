package com.example.verifit;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

// Adapter for WorkoutDay Class
public class StringAdapter extends RecyclerView.Adapter<StringAdapter.MyViewHolder> {

    Context ct;
    ArrayList<String> StringList;

    public StringAdapter(Context ct, ArrayList<String> StringList)
    {
        this.ct = ct;
        this.StringList = new ArrayList<String>(StringList);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(this.ct);
        View view = inflater.inflate(R.layout.personal_record_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        holder.tv_personal_record.setText(StringList.get(position));
    }


    @Override
    public int getItemCount()
    {
        return StringList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder
    {
        TextView tv_personal_record;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_personal_record = itemView.findViewById(R.id.tv_personal_record);

        }
    }
}
