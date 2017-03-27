package com.robot.myhome.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.robot.myhome.R;
import com.robot.myhome.Utils.AppUtils;
import com.robot.myhome.been.AppBean;
import com.robot.myhome.databinding.ActivityAppsBinding;

import java.util.ArrayList;
import java.util.List;

public class AppsActivity extends BaseActivity
{
    private List<AppBean> apps;
    private List<AppBean> subApps;
    private PopupWindow popupWindow;
    private ActivityAppsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_apps);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        binding.background.setImageBitmap(AppUtils.getInstance().getWallpaper(this));
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        apps = AppUtils.getInstance().getApps(AppsActivity.this);
        binding.recyclerView.setAdapter(adapter);
        binding.editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (EditorInfo.IME_ACTION_SEARCH == actionId)
                {
                    search(binding.editSearch.getText().toString());
                }
                return false;
            }
        });
        binding.editSearch.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                search(String.valueOf(s));
                if (s.length() > 0)
                {
                    binding.btnMore.setVisibility(View.INVISIBLE);
                } else
                {
                    binding.btnMore.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
        binding.btnMore.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PopupMenu popupMenu = new PopupMenu(AppsActivity.this, v);
                popupMenu.inflate(R.menu.menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        switch (item.getItemId())
                        {
                            case R.id.about:
                                startActivity(new Intent(AppsActivity.this, AboutActivity.class));
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        binding.icApps.post(new Runnable()
        {
            @Override
            public void run()
            {
                float icoSize = getResources().getDimension(R.dimen.icon_width);
                float width = getWindow().getDecorView().getWidth();
                float height = getWindow().getDecorView().getHeight();
                float endScaleX = width / icoSize;
                float endScaleY = height / icoSize;
                float transEndX = 0;
                float transEndY = -(height / 2 - icoSize / 2 - getResources().getDimension(R.dimen.dock_padding));
                ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(binding.icApps, "scaleX", 1, endScaleY * 1.5f);
                ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(binding.icApps, "scaleY", 1, endScaleY * 1.5f);
                ObjectAnimator animatorX = ObjectAnimator.ofFloat(binding.icApps, "translationX", 0, transEndX);
                ObjectAnimator animatorY = ObjectAnimator.ofFloat(binding.icApps, "translationY", 0, transEndY);
                ObjectAnimator animatorY2 = ObjectAnimator.ofFloat(binding.recyclerView, "translationY", -transEndY, 0);
                ObjectAnimator animatorAlpha0 = ObjectAnimator.ofFloat(binding.icApps, "alpha", 1, 0);
                binding.background.setVisibility(View.VISIBLE);
                binding.overlay.setVisibility(View.VISIBLE);
                ObjectAnimator animatorAlpha1 = ObjectAnimator.ofFloat(binding.background, "alpha", 0, 0, 1);
                ObjectAnimator animatorAlpha2 = ObjectAnimator.ofFloat(binding.overlay, "alpha", 0, 0, 1);
                ObjectAnimator animatorAlpha3 = ObjectAnimator.ofFloat(binding.recyclerView, "alpha", 0, 0, 1);
                AnimatorSet animationSet = new AnimatorSet();
                animationSet.playTogether(animatorScaleX, animatorScaleY, animatorX, animatorY,
                        animatorAlpha0, animatorAlpha1, animatorAlpha2, animatorY2, animatorAlpha3);
                animationSet.setDuration(300);
                animationSet.start();
            }
        });
        registerReceiver();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver();
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
                    popupWindow.showAsDropDown(binding.editSearch, 0, getResources().getDimensionPixelSize(R.dimen.offset_1_dp));
                }
                View v = popupWindow.getContentView();
                ((ImageView) v.findViewById(R.id.img_icon)).setImageDrawable(appBean.getIcon());
                ((TextView) v.findViewById(R.id.txt_name)).setText(appBean.getLabel());
                ((TextView) v.findViewById(R.id.txt_detail)).setText("包名：" + appBean.getPackageName());
                break;
            }
        }
    }

    private List<AppBean> filterApps(String keyword)
    {
        if (!TextUtils.isEmpty(keyword) && apps != null)
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
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    startActivity(new Intent(Intent.ACTION_UNINSTALL_PACKAGE)
                            .setData(Uri.parse("package:" + appBean.getPackageName())));
                    return false;
                }
            });
        }

        @Override
        public int getItemCount()
        {
            apps = filterApps(binding.editSearch.getText().toString());
            if(apps != null)
            {
                return apps.size();
            }
            return 0;
        }
    };

    private void registerReceiver()
    {
        IntentFilter intent = new IntentFilter();
        intent.addAction(getClass().getName());
        registerReceiver(packageChangeReceiver, intent);
    }

    private void unregisterReceiver()
    {
        unregisterReceiver(packageChangeReceiver);
    }

    private BroadcastReceiver packageChangeReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            adapter.notifyDataSetChanged();
        }
    };

}
