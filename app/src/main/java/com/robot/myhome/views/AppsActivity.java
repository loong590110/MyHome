package com.robot.myhome.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.robot.myhome.R;
import com.robot.myhome.Utils.AppUtils;
import com.robot.myhome.been.AppBean;

import java.util.ArrayList;
import java.util.List;

public class AppsActivity extends BaseActivity
{
    private List<AppBean> apps;
    private List<AppBean> subApps;
    private EditText editSearch;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        ((ImageView)findViewById(R.id.background)).setImageBitmap(AppUtils.getInstance().getWallpaper(this));
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        apps = AppUtils.getInstance().getApps(this);
        recyclerView.setAdapter(adapter);
        editSearch = (EditText) findViewById(R.id.edit_search);
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (EditorInfo.IME_ACTION_SEARCH == actionId)
                {
                    search(editSearch.getText().toString());
                }
                return false;
            }
        });
        editSearch.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                search(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
    }

    private void search(String keyword)
    {
        if (keyword.startsWith("pkg://"))
        {
            showPackageName(keyword);
        } else
        {
            if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
            adapter.notifyDataSetChanged();
        }
    }

    private void showPackageName(String keyword)
    {
        for (AppBean appBean : apps)
        {
            if (keyword.endsWith(appBean.getLabel()))
            {
                if (popupWindow == null)
                {
                    View v = getLayoutInflater().inflate(R.layout.view_app_detail, null);
                    popupWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                if (!popupWindow.isShowing())
                {
                    popupWindow.showAsDropDown(editSearch, 0, getResources().getDimensionPixelSize(R.dimen.offset_1_dp));
                }
                View v = popupWindow.getContentView();
                ((ImageView) v.findViewById(R.id.img_icon)).setImageDrawable(appBean.getIcon());
                ((TextView) v.findViewById(R.id.txt_name)).setText(appBean.getLabel());
                ((TextView) v.findViewById(R.id.txt_detail)).setText("pkg name: " + appBean.getPackageName());
                break;
            }
        }
    }

    private List<AppBean> filterApps(String keyword)
    {
        if (!TextUtils.isEmpty(keyword))
        {
            if (subApps == null) subApps = new ArrayList<>();
            else subApps.clear();
            for (AppBean appBean : apps)
            {
                if (appBean.getLabel().contains(keyword))
                {
                    subApps.add(appBean);
                }
            }
            return subApps;
        }
        return apps;
    }

    private RecyclerView.Adapter adapter = new RecyclerView.Adapter()
    {
        private List<AppBean> apps;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            return new RecyclerView.ViewHolder(getLayoutInflater().inflate(R.layout.item_apps, null))
            {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            final AppBean appBean = apps.get(position);
            final ImageView imgIcon = (ImageView) holder.itemView.findViewById(R.id.img_icon);
            final TextView txtName = (TextView) holder.itemView.findViewById(R.id.txt_name);
            imgIcon.setImageDrawable(appBean.getIcon());
            txtName.setText(appBean.getLabel());
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    startActivity(getPackageManager().getLaunchIntentForPackage(appBean.getPackageName()));
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return (apps = filterApps(editSearch.getText().toString())).size();
        }
    };
}
