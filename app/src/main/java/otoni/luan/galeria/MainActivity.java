package otoni.luan.galeria;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<MyItem> itens;
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

    }

    public class MainAdapter extends RecyclerView.Adapter{
        MainActivity mainActivity;
        List<String> photos;

        public MainAdapter(MainActivity mainActivity, List<String> photos){
            this.mainActivity = mainActivity;
            this.photos = photos;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //Obtem o inflador de layout
            LayoutInflater inflater = LayoutInflater.from(mainActivity);
            //cria os elementos de interface referentes a um item e os guarda em uma View
            View v = inflater.inflate(R.layout.item_list,parent,false);
            //O objeto view é guardado dentro de um objeto MyViewHolder
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            //obtem o item que vai ser usado para preencher a UI
            MyItem myItem = itens.get(position);

            //Obtem o objeto view armazenado no ViewHolder
            View v = holder.itemView;

            //preenche UI com os dados do item respectivamente com foto, titulo e descricao
            ImageView imvphoto = v.findViewById(R.id.imvPhoto);
            imvphoto.setImageBitmap(myItem.photo);

            TextView tvTitle = v.findViewById(R.id.tvTitle);
            tvTitle.setText(myItem.title);

            TextView tvdesc = v.findViewById(R.id.tvDesc);
            tvdesc.setText((myItem.description));
        }

        @Override
        public int getItemCount() {
            //retorna tamanho da lista de itens
            return itens.size();
        }
    }



}