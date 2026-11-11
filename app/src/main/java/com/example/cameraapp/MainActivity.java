package com.example.cameraapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ImageView preview;
    private File photoFile;

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && photoFile != null && photoFile.exists()) {
                    Bitmap bmp = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                    preview.setImageBitmap(bmp);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(32, 32, 32, 32);

        Button shoot = new Button(this);
        shoot.setText("Take Photo");
        shoot.setOnClickListener(v -> openCamera());

        preview = new ImageView(this);
        preview.setAdjustViewBounds(true);
        preview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        preview.setLayoutParams(lp);

        root.addView(shoot);
        root.addView(preview);
        setContentView(root);
    }

    private void openCamera() {
        try {
            File dir = new File(getCacheDir(), "photos");
            if (!dir.exists()) dir.mkdirs();
            photoFile = new File(dir, "photo_" + System.currentTimeMillis() + ".jpg");

            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            cameraLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Camera error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
