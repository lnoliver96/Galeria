package otoni.luan.galeria;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter {
    // instancia para a classe main
    MainActivity mainActivity;
    //Lista de Strings de caminhos para foto salva na pasta pictures
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
        View v = inflater.inflate(R.layout.list_item,parent,false);
        //O objeto view é guardado dentro de um objeto MyViewHolder
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ImageView imPhoto = holder.itemView.findViewById(R.id.imItem);
        int w = (int)mainActivity.getResources().getDimension(R.dimen.itemWidth);
        int h = (int)mainActivity.getResources().getDimension(R.dimen.itemHeight);
        Bitmap bitmap = Util.getBitmap(photos.get(position),w,h);
        imPhoto.setImageBitmap(bitmap);
        imPhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mainActivity.startPhotoActivity(photos.get(position));
            }

        });
    }

    @Override
    public int getItemCount() {
        //retorna tamanho da lista de itens
        return photos.size();
    }
}
