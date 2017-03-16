package com.robot.myhome;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AppsActivity extends BaseActivity
{
    private List<ResolveInfo> apps;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        apps = AppUtils.getInstance().getApps(this);
        recyclerView.setAdapter(adapter);
    }

    private RecyclerView.Adapter adapter = new RecyclerView.Adapter()
    {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            return new RecyclerView.ViewHolder(getLayoutInflater().inflate(R.layout.item_apps, null)){};
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            final ResolveInfo resolveInfo = apps.get(position);
            final ImageView imgIcon = (ImageView) holder.itemView.findViewById(R.id.img_icon);
            final TextView txtName = (TextView) holder.itemView.findViewById(R.id.txt_name);
            imgIcon.setImageDrawable(resolveInfo.loadIcon(getPackageManager()));
            txtName.setText(resolveInfo.loadLabel(getPackageManager()));
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ComponentName component = new ComponentName(resolveInfo.activityInfo.packageName,
                            resolveInfo.activityInfo.name);
                    Intent intent = new Intent();
                    intent.setComponent(component);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount()
        {
            if(apps != null)
            {
                return apps.size();
            }
            return 0;
        }
    };
}
