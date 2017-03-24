package com.robot.myhome.views;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.robot.myhome.R;
import com.robot.myhome.Utils.AppUtils;
import com.robot.myhome.Utils.PermissionRequester;
import com.robot.myhome.been.AppBean;
import com.robot.myhome.databinding.ActivityAppsBinding;
import com.robot.myhome.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity
{
    private final int REQUEST_CODE_PERMISSION = 100;
    private PermissionRequester permissionRequester;
    private Map<String, AppBean> dockApps;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        String[] permissions = PermissionRequester.checkSelfPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_CONTACTS
        });
        if (permissions.length > 0)
        {
            permissionRequester = PermissionRequester.newInstance(this).requestPermissions(permissions,
                    new PermissionRequester.OnPermissionsGrantedCallback()
                    {
                        @Override
                        public void onPermissionsGranted(String[] permissions)
                        {

                        }
                    }, new PermissionRequester.ShouldShowRequestPermissionRationale()
                    {
                        @Override
                        public void shouldShowRequestPermissionRationale(String permission)
                        {
                            finish();
                        }
                    }, REQUEST_CODE_PERMISSION);
        }
        initView();
        registerReceiver();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver();
    }

    private void initView()
    {
        initView(false);
    }

    private void initView(boolean refresh)
    {
        binding.app3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //startActivity(new Intent(MainActivity.this, AppsActivity.class));
                startAppsView();
            }
        });
        dockApps = AppUtils.getInstance().getDockApps(this, refresh);
        if (dockApps.get(AppUtils.PHONE) != null)
        {
            binding.app1.setImageDrawable(dockApps.get(AppUtils.PHONE).getIcon());
            findViewById(R.id.app1).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    startActivity(getPackageManager().getLaunchIntentForPackage(dockApps.get(AppUtils.PHONE).getPackageName()));
                }
            });
        } else
        {
            binding.app1.setVisibility(View.INVISIBLE);
        }
        if (dockApps.get(AppUtils.MSG) != null)
        {
            binding.app2.setImageDrawable(dockApps.get(AppUtils.MSG).getIcon());
            binding.app2.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    startActivity(getPackageManager().getLaunchIntentForPackage(dockApps.get(AppUtils.MSG).getPackageName()));
                }
            });
        } else
        {
            binding.app2.setVisibility(View.INVISIBLE);
        }
        if (dockApps.get(AppUtils.BROWSER) != null)
        {
            binding.app4.setImageDrawable(dockApps.get(AppUtils.BROWSER).getIcon());
            binding.app4.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    startActivity(getPackageManager().getLaunchIntentForPackage(dockApps.get(AppUtils.BROWSER).getPackageName()));
                }
            });
        } else
        {
            binding.app4.setVisibility(View.INVISIBLE);
        }
        if (dockApps.get(AppUtils.CAMERA) != null)
        {
            binding.app5.setImageDrawable(dockApps.get(AppUtils.CAMERA).getIcon());
            binding.app5.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    startActivity(getPackageManager().getLaunchIntentForPackage(dockApps.get(AppUtils.CAMERA).getPackageName()));
                }
            });
        } else
        {
            binding.app5.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION)
        {
            permissionRequester.onRequestPermissionsResult(grantResults);
        }
    }

    @Override
    public void onBackPressed()
    {

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        closeAppsView();
    }

    private void registerReceiver()
    {
        IntentFilter intent = new IntentFilter();
        intent.addAction(getClass().getName());
        registerReceiver(packageChangeReceiver, intent);
//        registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
//                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    private void unregisterReceiver()
    {
        unregisterReceiver(packageChangeReceiver);
        //unregisterReceiver(mHomeKeyEventReceiver);
    }

    private BroadcastReceiver packageChangeReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            initView(true);
            //sendBroadcast(new Intent().setAction(AppsActivity.class.getName()));
        }
    };

    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM_SWITCH_KEY = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                Log.i("@zls", "key=" + reason);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                    //表示按了home键,程序到了后台
                    closeAppsView();
                }else if(TextUtils.equals(reason, SYSTEM_SWITCH_KEY)){
                    //表示长按home键,显示最近使用的程序列表
                    closeAppsView();
                }
            }
        }
    };

    /**************** Apps Activity ***************/
    private List<AppBean> apps;
    private List<AppBean> subApps;
    private PopupWindow popupWindow;
    private ActivityAppsBinding bind;

    private void closeAppsView()
    {
        if(popupWindow != null)
        {
            popupWindow.dismiss();
        }
    }

    private void startAppsView()
    {
        final View v = getLayoutInflater().inflate(R.layout.activity_apps, null);
        bind = DataBindingUtil.bind(v);
        popupWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(v, 0, 0, 0);
        bind.background.setImageBitmap(AppUtils.getInstance().getWallpaper(this));
        bind.recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        apps = AppUtils.getInstance().getApps(MainActivity.this);
        bind.recyclerView.setAdapter(adapter);
        bind.editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (EditorInfo.IME_ACTION_SEARCH == actionId)
                {
                    search(bind.editSearch.getText().toString());
                }
                return false;
            }
        });
        bind.editSearch.addTextChangedListener(new TextWatcher()
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
        bind.icApps.post(new Runnable()
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
                float transEndY = - (height / 2 - icoSize / 2 - getResources().getDimension(R.dimen.dock_padding));
                ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(bind.icApps, "scaleX", 1, endScaleY * 1.5f);
                ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(bind.icApps, "scaleY", 1, endScaleY * 1.5f);
                ObjectAnimator animatorX = ObjectAnimator.ofFloat(bind.icApps, "translationX", 0, transEndX);
                ObjectAnimator animatorY = ObjectAnimator.ofFloat(bind.icApps, "translationY", 0, transEndY);
                ObjectAnimator animatorY2 = ObjectAnimator.ofFloat(bind.recyclerView, "translationY", -transEndY, 0);
                ObjectAnimator animatorAlpha0 = ObjectAnimator.ofFloat(bind.icApps, "alpha", 1, 0);
                bind.background.setVisibility(View.VISIBLE);
                bind.overlay.setVisibility(View.VISIBLE);
                ObjectAnimator animatorAlpha1 = ObjectAnimator.ofFloat(bind.background, "alpha", 0, 0, 1);
                ObjectAnimator animatorAlpha2 = ObjectAnimator.ofFloat(bind.overlay, "alpha", 0, 0, 1);
                ObjectAnimator animatorAlpha3 = ObjectAnimator.ofFloat(bind.recyclerView, "alpha", 0, 0, 1);
                AnimatorSet animationSet = new AnimatorSet();
                animationSet.playTogether(animatorScaleX, animatorScaleY, animatorX, animatorY,
                        animatorAlpha0, animatorAlpha1, animatorAlpha2, animatorY2, animatorAlpha3);
                animationSet.setDuration(300);
                animationSet.start();
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
                    popupWindow.showAsDropDown(bind.editSearch, 0, getResources().getDimensionPixelSize(R.dimen.offset_1_dp));
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
            apps = filterApps(bind.editSearch.getText().toString());
            if(apps != null)
            {
                return apps.size();
            }
            return 0;
        }
    };
}
