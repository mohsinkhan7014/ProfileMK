package com.mohsin.profilemk;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    ImageView im;
    Button upload;
    TextView t1;
    FirebaseStorage fa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        im=findViewById(R.id.image);
        upload=findViewById(R.id.btn);
        t1=findViewById(R.id.text);
        fa=FirebaseStorage.getInstance();
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i,1);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                im.setDrawingCacheEnabled(true);
                im.buildDrawingCache();
                Bitmap b=im.getDrawingCache();
                ByteArrayOutputStream ba=new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.PNG,100,ba);
                im.setDrawingCacheEnabled(false);
                byte[] bt=ba.toByteArray();
                String path="FireFOlder"+ UUID.randomUUID()+".png";
                StorageReference ref=fa.getReference(path);
                StorageMetadata md=new  StorageMetadata.Builder().setCustomMetadata("text",t1.getText().toString()).build();
                im.setEnabled(false);
                UploadTask ut=ref.putBytes(bt,md);
                ut.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        im.setEnabled(true);
                        Task<Uri> url=taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        t1.setText(url.toString());
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK);
        {
            if(data.getData()!=null)
            {
                Uri uri=data.getData();
                im.setImageURI(uri);
            }
        }
    }
}
