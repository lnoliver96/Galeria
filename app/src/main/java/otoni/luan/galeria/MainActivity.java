package otoni.luan.galeria;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static int RESULT_TAKE_PICTURE = 1;
    String currentPhotoPath;
    List<String> photos = new ArrayList<>();


    MainAdapter mainAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.tbMain);
        setSupportActionBar(toolbar);
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] files = dir.listFiles();
        for(int i = 0; i < files.length;i++){
            RecyclerView rvGallery = findViewById(R.id.rvGallery);
            rvGallery.setAdapter(mainAdapter);

            float w = getResources().getDimension(R.dimen.itemWidth);
            int numberOfColumns = Util.calculateNoOfColumns(MainActivity.this,w);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, numberOfColumns);
            rvGallery.setLayoutManager(gridLayoutManager);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_tb,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.opCamera) {
            dispatchTakePictureIntent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void dispatchTakePictureIntent() {
        File f = null;
        try{
            f = createImageFile();
        } catch(IOException e){
            Toast.makeText(MainActivity.this,"Não foi possível criar arquivo",Toast.LENGTH_LONG).show();
            return;
        }

        currentPhotoPath = f.getAbsolutePath();

        if(f != null){
            Uri fUri = FileProvider.getUriForFile(MainActivity.this,"otoni.luan.galeria.fileprovider",f);
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT,fUri);
            startActivityForResult(i,RESULT_TAKE_PICTURE);

        }
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File f = File.createTempFile(imageFileName, ".jpg",storageDir);
        return f;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == RESULT_TAKE_PICTURE){
            if (requestCode == Activity.RESULT_OK){
                photos.add(currentPhotoPath);
                mainAdapter.notifyItemInserted(photos.size()-1);
            }
            else {
                File f = new File(currentPhotoPath);
                f.delete();
            }
        }
    }

    public void startPhotoActivity(String photoPath) {
        Intent i = new Intent(MainActivity.this,PhotoActivity.class);
        i.putExtra("photo_path",photoPath);
        startActivity(i);
    }
}