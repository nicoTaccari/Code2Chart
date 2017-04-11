package com.example.proyectofinal.code2chart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CarpetasAdapter extends BaseAdapter implements Filterable {

    private ArrayList<FileComposite> misDatos;
    private ArrayList<FileComposite> filterListMisDatos;
    private CarpetaOArchivoFilter filter;
    private Context miContexto;

    public CarpetasAdapter(Context unContexto, ArrayList<FileComposite> carpetasYArchivos){
        this.miContexto = unContexto;
        this.misDatos = carpetasYArchivos;
        this.filterListMisDatos = carpetasYArchivos;
    }

    @Override
    public int getCount() {
        return misDatos.size();
    }

    @Override
    public Object getItem(int position) {
        return misDatos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return misDatos.indexOf(getItem(position));
    }

    /*--------------------------------------------------------------------------------------*/
    public View getView(int posicion, View convertView, ViewGroup parent){

        LayoutInflater inflater = (LayoutInflater) miContexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item_folder,null);
        }

        TextView titulo = (TextView) convertView.findViewById(R.id.texto);
        ImageView icono = (ImageView) convertView.findViewById(R.id.icono);

        //SET DATA
        titulo.setText(misDatos.get(posicion).getTitulo());
        icono.setImageResource(misDatos.get(posicion).getIcono());


        return convertView;
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new CarpetaOArchivoFilter();
        }
        return filter;
    }

    class CarpetaOArchivoFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if(constraint != null && constraint.length()>0){
                constraint = constraint.toString().toUpperCase();
                ArrayList<FileComposite> filters = new ArrayList<FileComposite>();

                //TODO aplicar collections para un codigo mas limpio
                for (FileComposite obj: filterListMisDatos){
                    if (obj.getTitulo().toUpperCase().contains(constraint) && obj.getClass().isAssignableFrom(Carpeta.class)) {
                        Carpeta carpeta = new Carpeta(obj.getTitulo(),obj.getIcono());
                        filters.add(carpeta);
                    }else if (obj.getTitulo().toUpperCase().contains(constraint) && obj.getClass().isAssignableFrom(Archivo.class)){
                        Archivo archivo = new Archivo(obj.getTitulo(),obj.getIcono());
                        filters.add(archivo);
                    }
                }

                results.count = filters.size();
                results.values = filters;
            }else{
                results.count = filterListMisDatos.size();
                results.values = filterListMisDatos;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            misDatos = (ArrayList<FileComposite>) results.values;
            notifyDataSetChanged();
        }
    }
}