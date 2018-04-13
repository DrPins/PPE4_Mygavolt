package android.sio2.efficom.fr.applitoto.adapters;

import android.annotation.SuppressLint;
import android.sio2.efficom.fr.applitoto.R;
import android.sio2.efficom.fr.applitoto.model.Intervention;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

/**
 * Created by yassine on 20/02/2018.
 */

public class InterventionAdapter extends RecyclerView.Adapter<InterventionAdapter.ViewHolder> {

    List<Intervention.Liste_int> items;
    View.OnClickListener adapterClicListener;

    public InterventionAdapter(List<Intervention.Liste_int> items, View.OnClickListener adapterClicListener) {
        this.items = items;
        this.adapterClicListener = adapterClicListener;
    }

    @Override
    public InterventionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.intervention_item_row, parent, false);
        InterventionAdapter.ViewHolder viewHolder = new InterventionAdapter.ViewHolder(v);
        return viewHolder;
    }

    //Donne le visuel d'un item de la liste
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(InterventionAdapter.ViewHolder holder, final int position) {
        //récupère l'item qui correspond à la position demandée
        final Intervention.Liste_int item = items.get(position);
        //met le contenu de l'item courant



        String heure;
        if(item.time_inter != null) {
            heure = item.time_inter.substring(0, 5);
        }
        else {
           heure = "Pas d'horaire défini";
        }

        holder.itemMotiveTextView.setText(item.motive);
        holder.itemTimeTextView.setText(heure);
        holder.itemDateTextView.setText(DateFormat.getDateInstance(DateFormat.FULL).format(item.date_inter));
        holder.itemTempTextView.setText(item.city );
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setTag(item);
                adapterClicListener.onClick(view);
            }
        });

        if(item.pending == 1){
            holder.itemDateTextView.setBackgroundResource(R.color.colorGrey);
            holder.itemTimeTextView.setTextColor(R.color.colorGrey);

            holder.itemMotiveTextView.setTextColor(R.color.colorWhite);
            holder.itemMotiveTextView.setBackgroundResource(R.color.colorGrey);
            holder.itemMotiveTextView.setText("Intervention réalisée");
            holder.itemMotiveTextView.setTextSize(17);

        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemDateTextView, itemMotiveTextView, itemTempTextView, itemTimeTextView;


        public ViewHolder(View itemView) {
            super(itemView);

            //récupère une référence vers les items pour une prochaine utilisation
            itemDateTextView = itemView.findViewById(R.id.itemMotiveTextView);
            itemMotiveTextView = itemView.findViewById(R.id.itemDateTextView);
            itemTempTextView = itemView.findViewById(R.id.itemCityTextView);
            itemTimeTextView = itemView.findViewById(R.id.itemTimeTextView);

        }
    }
}
