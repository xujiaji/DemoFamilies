package com.demofamilies.app.base;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * author: xujiaji
 * created on: 2018/5/18 10:31
 * description: 对请求权限进行统一处理
 */

@RuntimePermissions
public abstract class RequestPermissionActivity extends BaseActivity
{

    protected void checkAndRunCameraHandle()
    {
        RequestPermissionActivityPermissionsDispatcher.showCameraWithPermissionCheck(this);
    }

    protected void checkAndRunStorageHandle()
    {
        RequestPermissionActivityPermissionsDispatcher.showStorageWithPermissionCheck(this);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void showCamera()
    {
        onCameraHandle();
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showStorage()
    {
        onStorageHandle();
    }

    protected void onCameraHandle()
    {
    }

    protected void onStorageHandle()
    {
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    void showRationaleForCamera(final PermissionRequest request)
    {
        new AlertDialog.Builder(this)
                .setMessage("请求相机权限")
                .setPositiveButton("允许", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        request.proceed();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForStorage(final PermissionRequest request)
    {
        new AlertDialog.Builder(this)
                .setMessage("请求存储权限")
                .setPositiveButton("允许", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        request.proceed();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    void showDeniedForCamera()
    {
        Toast.makeText(this, "相机权限被拒绝", Toast.LENGTH_SHORT).show();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showDeniedForStorage()
    {
        Toast.makeText(this, "存储权限被拒绝", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    void showNeverAskForCamera()
    {
        Toast.makeText(this, "已设置：再也不询问相机权限", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showNeverAskForStorage()
    {
        Toast.makeText(this, "已设置：再也不询问存储权限", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RequestPermissionActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
