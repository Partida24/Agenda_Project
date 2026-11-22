package com.example.agenda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import java.util.ArrayList;

public class AdaptadorContactes extends ArrayAdapter<Contacte> implements Filterable {

    private final ArrayList<Contacte> llistaOriginal;
    private ArrayList<Contacte> llistaFiltrada;

    public AdaptadorContactes(Context contexto, ArrayList<Contacte> datos) {
        super(contexto, 0, datos);
        this.llistaOriginal = datos;
        this.llistaFiltrada = datos;
    }

    @Override
    public int getCount() {
        return llistaFiltrada.size();
    }

    @Override
    public Contacte getItem(int position) {
        if (position >= 0 && position < llistaFiltrada.size()) {
            return llistaFiltrada.get(position);
        }
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View fila = convertView;

        if (fila == null) {
            fila = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_contacte, parent, false);
        }

        Contacte contacte = getItem(position);

        TextView textNom = fila.findViewById(R.id.textNom);
        TextView textNumero = fila.findViewById(R.id.textNumero);

        if (contacte != null) {
            String nomComplet = (contacte.getNom() != null ? contacte.getNom() : "") + " " +
                    (contacte.getCognom() != null ? contacte.getCognom() : "");
            textNom.setText(nomComplet.trim());
            textNumero.setText(contacte.getNumero() != null ? contacte.getNumero() : "");
        }

        return fila;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint == null || constraint.toString().trim().isEmpty()) {
                    results.values = llistaOriginal;
                    results.count = llistaOriginal.size();
                } else {
                    String busqueda = constraint.toString().toLowerCase().trim();
                    ArrayList<Contacte> filtrats = new ArrayList<>();

                    for (Contacte c : llistaOriginal) {
                        String nom = c.getNom() != null ? c.getNom().toLowerCase() : "";
                        String cognom = c.getCognom() != null ? c.getCognom().toLowerCase() : "";
                        String numero = c.getNumero() != null ? c.getNumero().toLowerCase() : "";
                        String nomComplet = (nom + " " + cognom).trim();

                        if (nom.contains(busqueda) || cognom.contains(busqueda) || nomComplet.contains(busqueda) || numero.contains(busqueda)) {
                            filtrats.add(c);
                        }
                    }

                    results.values = filtrats;
                    results.count = filtrats.size();
                }

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    llistaFiltrada = (ArrayList<Contacte>) results.values;
                } else {
                    llistaFiltrada = llistaOriginal;
                }
                notifyDataSetChanged();
            }
        };
    }
}
